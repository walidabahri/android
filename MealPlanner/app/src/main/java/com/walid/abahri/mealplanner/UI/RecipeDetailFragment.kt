package com.walid.abahri.mealplanner.UI

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.walid.abahri.mealplanner.DB.AppDatabase
import com.walid.abahri.mealplanner.DB.Ingredient
import com.walid.abahri.mealplanner.DB.Recipe
import com.walid.abahri.mealplanner.DB.getIngredientsList
import com.walid.abahri.mealplanner.DB.getTotalTimeMinutes
import com.walid.abahri.mealplanner.R
import com.walid.abahri.mealplanner.ViewModel.RecipeViewModel
import com.walid.abahri.mealplanner.ViewModel.RecipeViewModelFactory
import com.walid.abahri.mealplanner.databinding.FragmentRecipeDetailBinding
import com.walid.abahri.mealplanner.repository.RecipeRepository

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private var currentRecipe: Recipe? = null
    
    // If you're using Safe Args navigation, uncomment this line:
    // private val args: RecipeDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize the repository and ViewModelFactory
        val database = AppDatabase.getDatabase(requireContext())
        val repository = RecipeRepository(database.recipeDao())
        val viewModelFactory = RecipeViewModelFactory(repository)
        
        // Initialize ViewModel using the factory
        recipeViewModel = ViewModelProvider(this, viewModelFactory)[RecipeViewModel::class.java]
        
        setupToolbar()
        setupIngredientsRecyclerView()
        setupClickListeners()
        
        // Get the recipe ID from arguments bundle
        val recipeId = arguments?.getInt("recipeId", -1) ?: -1
        if (recipeId != -1) {
            loadRecipeDetails(recipeId)
        } else {
            // Handle case where no recipe ID was provided
            Snackbar.make(binding.root, "Error: No recipe found", Snackbar.LENGTH_LONG).show()
            findNavController().navigateUp()
        }
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupIngredientsRecyclerView() {
        ingredientsAdapter = IngredientsAdapter()
        binding.recyclerViewIngredients.apply {
            adapter = ingredientsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    private fun setupClickListeners() {
        // Favorite button
        binding.fabFavorite.setOnClickListener {
            currentRecipe?.let { recipe ->
                recipeViewModel.toggleFavorite(recipe.id, !recipe.isFavorite)
                updateFavoriteIcon(!recipe.isFavorite)
                val message = if (!recipe.isFavorite) "Added to favorites" else "Removed from favorites"
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
            }
        }
        
        // Edit button
        binding.fabEdit.setOnClickListener {
            currentRecipe?.let { recipe ->
                // Navigate to edit recipe screen
                try {
                    findNavController().navigate(
                        R.id.action_recipeDetailFragment_to_addEditRecipeFragment,
                        Bundle().apply {
                            putInt("recipeId", recipe.id)
                        }
                    )
                } catch (e: Exception) {
                    Snackbar.make(
                        binding.root,
                        "Error navigating to edit recipe: ${e.message}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
        
        // Rating bar
        binding.ratingBarDetailRecipe.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser && currentRecipe != null) {
                recipeViewModel.updateRating(currentRecipe!!.id, rating)
                Snackbar.make(binding.root, "Rating updated", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun loadRecipeDetails(recipeId: Int) {
        recipeViewModel.getRecipeById(recipeId).observe(viewLifecycleOwner) { recipe ->
            recipe?.let {
                currentRecipe = it
                displayRecipeDetails(it)
            }
        }
    }
    
    private fun displayRecipeDetails(recipe: Recipe) {
        // Set collapsing toolbar title
        binding.collapsingToolbar.title = recipe.title
        
        // Load recipe image
        Glide.with(this)
            .load(recipe.imageUrl)
            .placeholder(R.drawable.ic_recipe_placeholder)
            .error(R.drawable.ic_recipe_placeholder)
            .into(binding.imageViewRecipeDetail)
        
        // Set recipe stats
        binding.textViewTotalTime.text = "${recipe.getTotalTimeMinutes()} min"
        binding.textViewServings.text = recipe.servings.toString()
        binding.textViewCaloriesDetail.text = "${recipe.caloriesPerServing ?: 0} cal"
        binding.textViewDifficulty.text = recipe.difficultyLevel
        
        // Set description and instructions
        binding.textViewRecipeDescription.text = recipe.description
        binding.textViewInstructions.text = recipe.instructions
        
        // Set rating
        binding.ratingBarDetailRecipe.rating = recipe.rating
        
        // Update favorite icon
        updateFavoriteIcon(recipe.isFavorite)
        
        // Load ingredients list
        val ingredients = recipe.getIngredientsList()
        ingredientsAdapter.submitList(ingredients)
    }
    
    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val iconResId = if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
        binding.fabFavorite.setImageResource(iconResId)
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipe_detail_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                shareRecipe()
                true
            }
            R.id.action_add_to_meal_plan -> {
                addToMealPlan()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun shareRecipe() {
        // Implementation for sharing recipe
    }
    
    private fun addToMealPlan() {
        // Implementation for adding recipe to meal plan
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    // Inner class for ingredients adapter
    private inner class IngredientsAdapter : 
        androidx.recyclerview.widget.ListAdapter<Ingredient, IngredientsAdapter.IngredientViewHolder>(
            object : androidx.recyclerview.widget.DiffUtil.ItemCallback<Ingredient>() {
                override fun areItemsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
                    return oldItem.name == newItem.name
                }
                
                override fun areContentsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
                    return oldItem == newItem
                }
            }
        ) {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_ingredient, parent, false)
            return IngredientViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
            holder.bind(getItem(position))
        }
        
        inner class IngredientViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
            fun bind(ingredient: Ingredient) {
                itemView.findViewById<android.widget.TextView>(R.id.textViewIngredientName).text = ingredient.name
                itemView.findViewById<android.widget.TextView>(R.id.textViewIngredientAmount).text = 
                    "${ingredient.amount} ${ingredient.unit}"
            }
        }
    }
}
