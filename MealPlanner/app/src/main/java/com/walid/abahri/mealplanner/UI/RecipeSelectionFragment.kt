package com.walid.abahri.mealplanner.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.walid.abahri.mealplanner.DB.AppDatabase
import com.walid.abahri.mealplanner.DB.MealPlan
import com.walid.abahri.mealplanner.DB.Recipe
import com.walid.abahri.mealplanner.R
import com.walid.abahri.mealplanner.ViewModel.MealPlanViewModel
import com.walid.abahri.mealplanner.ViewModel.RecipeViewModel
import com.walid.abahri.mealplanner.ViewModel.RecipeViewModelFactory
import com.walid.abahri.mealplanner.databinding.FragmentRecipeSelectionBinding
import com.walid.abahri.mealplanner.repository.RecipeRepository
import java.text.SimpleDateFormat
import java.util.*

class RecipeSelectionFragment : Fragment() {
    private var _binding: FragmentRecipeSelectionBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var mealPlanViewModel: MealPlanViewModel
    private lateinit var recipeAdapter: RecipeSelectionAdapter
    
    private var selectedRecipe: Recipe? = null
    private var servingsCount = 1
    
    // Arguments from navigation
    private lateinit var date: String
    private lateinit var mealType: String
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get arguments from navigation
        arguments?.let { args ->
            date = args.getString("date", getCurrentDate())
            mealType = args.getString("mealType", "Breakfast")
        }
        
        // Update header text with meal info
        val formattedDate = formatDate(date)
        binding.textMealInfo.text = "For $mealType on $formattedDate"
        
        // Initialize ViewModels
        val database = AppDatabase.getDatabase(requireContext())
        val repository = RecipeRepository(database.recipeDao())
        val viewModelFactory = RecipeViewModelFactory(repository)
        recipeViewModel = ViewModelProvider(this, viewModelFactory)[RecipeViewModel::class.java]
        mealPlanViewModel = ViewModelProvider(this)[MealPlanViewModel::class.java]
        
        setupRecyclerView()
        setupObservers()
        setupSearchView()
        setupCategoryChips()
        setupServingsCounter()
        setupAddToMealPlanButton()
    }
    
    private fun setupRecyclerView() {
        recipeAdapter = RecipeSelectionAdapter { recipe ->
            // Recipe selection logic
            selectedRecipe = recipe
            binding.buttonAddToMealPlan.isEnabled = true
        }
        
        binding.recyclerViewRecipes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recipeAdapter
        }
    }
    
    private fun setupObservers() {
        recipeViewModel.filteredRecipesWithFavorites.observe(viewLifecycleOwner) { recipes ->
            recipeAdapter.submitList(recipes)
            
            // Show/hide empty state
            if (recipes.isEmpty()) {
                binding.textEmptyState.visibility = View.VISIBLE
                binding.recyclerViewRecipes.visibility = View.GONE
            } else {
                binding.textEmptyState.visibility = View.GONE
                binding.recyclerViewRecipes.visibility = View.VISIBLE
            }
        }
        
        // Observe categories for filter chips
        recipeViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            setupCategoryChips(categories)
        }
    }
    
    private fun setupSearchView() {
        binding.searchViewRecipes.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { recipeViewModel.setSearchQuery(it) }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { recipeViewModel.setSearchQuery(it) }
                return true
            }
        })
    }
    
    private fun setupCategoryChips(categories: List<String> = emptyList()) {
        // Clear existing chips except "All"
        val chipGroup = binding.chipGroupCategories
        val allChip = chipGroup.findViewById<Chip>(R.id.chip_all)
        chipGroup.removeAllViews()
        chipGroup.addView(allChip)
        
        // Add category chips dynamically
        categories.forEach { category ->
            val chip = Chip(requireContext()).apply {
                text = category
                isCheckable = true
                isCheckedIconVisible = true
                setOnClickListener {
                    recipeViewModel.setSelectedCategory(category)
                }
            }
            chipGroup.addView(chip)
        }
        
        // Set click listener for "All" chip
        allChip.setOnClickListener {
            recipeViewModel.clearFilters()
        }
    }
    
    private fun setupServingsCounter() {
        binding.textServingsCount.text = servingsCount.toString()
        
        binding.buttonDecreaseServings.setOnClickListener {
            if (servingsCount > 1) {
                servingsCount--
                binding.textServingsCount.text = servingsCount.toString()
            }
        }
        
        binding.buttonIncreaseServings.setOnClickListener {
            servingsCount++
            binding.textServingsCount.text = servingsCount.toString()
        }
    }
    
    private fun setupAddToMealPlanButton() {
        binding.buttonAddToMealPlan.setOnClickListener {
            selectedRecipe?.let { recipe ->
                val mealPlan = MealPlan(
                    date = date,
                    mealType = mealType,
                    recipeId = recipe.id,
                    servings = servingsCount
                )
                
                mealPlanViewModel.addMealPlan(mealPlan)
                Toast.makeText(
                    requireContext(),
                    "${recipe.title} added to $mealType",
                    Toast.LENGTH_SHORT
                ).show()
                
                // Navigate back to MealPlanFragment
                findNavController().navigateUp()
            }
        }
    }
    
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
    
    private fun formatDate(date: String): String {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            return parsedDate?.let { outputFormat.format(it) } ?: date
        } catch (e: Exception) {
            return date
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    // RecipeSelectionAdapter
    private inner class RecipeSelectionAdapter(
        private val onRecipeSelected: (Recipe) -> Unit
    ) : RecyclerView.Adapter<RecipeSelectionAdapter.RecipeViewHolder>() {
        
        private var recipes = listOf<Recipe>()
        private var selectedPosition = RecyclerView.NO_POSITION
        
        fun submitList(list: List<Recipe>) {
            recipes = list
            notifyDataSetChanged()
        }
        
        inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleTextView: TextView = itemView.findViewById(R.id.text_recipe_title)
            val categoryTextView: TextView = itemView.findViewById(R.id.text_recipe_category)
            val timeTextView: TextView = itemView.findViewById(R.id.text_recipe_time)
            val caloriesTextView: TextView = itemView.findViewById(R.id.text_recipe_calories)
            
            init {
                itemView.setOnClickListener {
                    val previousSelected = selectedPosition
                    selectedPosition = adapterPosition
                    
                    // Update UI for previously selected item
                    if (previousSelected != RecyclerView.NO_POSITION) {
                        notifyItemChanged(previousSelected)
                    }
                    
                    // Update UI for newly selected item
                    notifyItemChanged(selectedPosition)
                    
                    // Callback to fragment
                    onRecipeSelected(recipes[adapterPosition])
                }
            }
        }
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recipe_selection, parent, false)
            return RecipeViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
            val recipe = recipes[position]
            
            holder.titleTextView.text = recipe.title
            holder.categoryTextView.text = recipe.category
            holder.timeTextView.text = "${recipe.prepTimeMinutes + recipe.cookTimeMinutes} min"
            holder.caloriesTextView.text = "${recipe.caloriesPerServing} cal"
            
            // Highlight selected item
            holder.itemView.isSelected = selectedPosition == position
        }
        
        override fun getItemCount() = recipes.size
    }
}
