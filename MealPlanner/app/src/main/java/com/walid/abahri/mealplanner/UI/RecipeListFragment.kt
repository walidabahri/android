package com.walid.abahri.mealplanner.UI

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.launch
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.walid.abahri.mealplanner.DB.AppDatabase
import com.walid.abahri.mealplanner.DB.Recipe
import com.walid.abahri.mealplanner.R
import com.walid.abahri.mealplanner.RecipeAdapter
import com.walid.abahri.mealplanner.ViewModel.RecipeViewModel
import com.walid.abahri.mealplanner.ViewModel.RecipeViewModelFactory
import com.walid.abahri.mealplanner.databinding.FragmentRecipeListBinding
import com.walid.abahri.mealplanner.repository.RecipeRepository

class RecipeListFragment : Fragment() {
    private var _binding: FragmentRecipeListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecipeListBinding.inflate(inflater, container, false)
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
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupSearchView()
    }
    
    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(
            onRecipeClick = { recipe ->
                // Navigate to recipe detail
                // You need to add a navigation action in your nav graph
                // findNavController().navigate(RecipeListFragmentDirections.actionRecipeListToRecipeDetail(recipe.id))
            },
            onFavoriteClick = { recipe ->
                recipeViewModel.toggleFavorite(recipe.id, !recipe.isFavorite)
            },
            onEditClick = { recipe ->
                // Navigate to edit recipe
                try {
                    findNavController().navigate(
                        R.id.action_recipeListFragment_to_addEditRecipeFragment,
                        Bundle().apply {
                            putInt("recipeId", recipe.id)
                        }
                    )
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Error navigating to edit recipe: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onDeleteClick = { recipe ->
                showDeleteConfirmationDialog(recipe)
            }
        )
        
        binding.recyclerViewRecipes.apply {
            adapter = recipeAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }
    
    private fun setupObservers() {
        // Observe filtered recipes with favorites filtering
        recipeViewModel.filteredRecipesWithFavorites.observe(viewLifecycleOwner) { recipes ->
            updateRecipesList(recipes)
        }
        
        // Observe categories for filter chips
        recipeViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            setupCategoryChips(categories)
        }
        
        // Observe showing favorites state to update UI
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                recipeViewModel.showingFavorites.collect { showingFavorites ->
                    binding.chipFavorites.isChecked = showingFavorites
                }
            }
        }
    }
    
    private fun updateRecipesList(recipes: List<Recipe>) {
        recipeAdapter.submitList(recipes)
        
        // Show/hide empty state
        if (recipes.isEmpty()) {
            binding.textViewEmptyState.visibility = View.VISIBLE
            binding.recyclerViewRecipes.visibility = View.GONE
        } else {
            binding.textViewEmptyState.visibility = View.GONE
            binding.recyclerViewRecipes.visibility = View.VISIBLE
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddRecipe.setOnClickListener {
            // Navigate to the AddEditRecipeFragment
            try {
                findNavController().navigate(
                    R.id.action_recipeListFragment_to_addEditRecipeFragment,
                    Bundle().apply {
                        putInt("recipeId", 0) // 0 means new recipe
                    }
                )
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error navigating to add recipe: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        
        binding.chipFavorites.setOnClickListener {
            // Show only favorite recipes
            toggleFavoritesFilter()
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
    
    private fun setupCategoryChips(categories: List<String>) {
        // Clear existing chips except "All" and "Favorites"
        binding.chipGroupCategories.removeViews(2, binding.chipGroupCategories.childCount - 2)
        
        // Add category chips dynamically
        categories.forEach { category ->
            val chip = com.google.android.material.chip.Chip(requireContext())
            chip.text = category
            chip.isCheckable = true
            chip.setOnClickListener {
                recipeViewModel.setSelectedCategory(category)
            }
            binding.chipGroupCategories.addView(chip)
        }
    }
    
    private fun toggleFavoritesFilter() {
        // Implementation for showing only favorites
        recipeViewModel.toggleFavoritesFilter()
        // No need to update UI here as we're observing the state flow
    }
    
    private fun showDeleteConfirmationDialog(recipe: Recipe) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Recipe")
            .setMessage("Are you sure you want to delete '${recipe.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                recipeViewModel.deleteRecipe(recipe)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipe_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                showFilterDialog()
                true
            }
            R.id.action_sort -> {
                showSortDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun showFilterDialog() {
        // Implementation for advanced filters dialog
    }
    
    private fun showSortDialog() {
        // Implementation for sorting options dialog
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
