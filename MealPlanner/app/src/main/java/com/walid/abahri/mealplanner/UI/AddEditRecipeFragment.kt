package com.walid.abahri.mealplanner.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.walid.abahri.mealplanner.DB.AppDatabase
import com.walid.abahri.mealplanner.DB.Recipe
import com.walid.abahri.mealplanner.R
import com.walid.abahri.mealplanner.ViewModel.RecipeViewModel
import com.walid.abahri.mealplanner.ViewModel.RecipeViewModelFactory
import com.walid.abahri.mealplanner.databinding.FragmentAddEditRecipeBinding
import com.walid.abahri.mealplanner.repository.RecipeRepository
import java.util.*

class AddEditRecipeFragment : Fragment() {
    private var _binding: FragmentAddEditRecipeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var recipeViewModel: RecipeViewModel
    private var recipeId: Int = 0
    private var isEditMode = false
    
    // Categories and difficulty levels for dropdowns
    private val categories = listOf("Breakfast", "Lunch", "Dinner", "Dessert", "Snack", "Appetizer", "Soup", "Salad", "Main Course", "Side Dish", "Beverage", "Other")
    private val difficultyLevels = listOf("Easy", "Medium", "Hard")
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize ViewModel
        val database = AppDatabase.getDatabase(requireContext())
        val repository = RecipeRepository(database.recipeDao())
        val viewModelFactory = RecipeViewModelFactory(repository)
        recipeViewModel = ViewModelProvider(this, viewModelFactory)[RecipeViewModel::class.java]
        
        // Check if we're editing an existing recipe
        arguments?.let { args ->
            if (args.containsKey("recipeId")) {
                recipeId = args.getInt("recipeId")
                isEditMode = recipeId > 0
                if (isEditMode) {
                    binding.textTitleHeader.text = "Edit Recipe"
                    loadRecipeData(recipeId)
                }
            }
        }
        
        setupDropdowns()
        setupSaveButton()
    }
    
    private fun setupDropdowns() {
        // Setup category dropdown
        val categoryAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, categories)
        (binding.dropdownCategory as? AutoCompleteTextView)?.setAdapter(categoryAdapter)
        
        // Setup difficulty dropdown
        val difficultyAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, difficultyLevels)
        (binding.dropdownDifficulty as? AutoCompleteTextView)?.setAdapter(difficultyAdapter)
    }
    
    private fun loadRecipeData(recipeId: Int) {
        recipeViewModel.getRecipeById(recipeId).observe(viewLifecycleOwner) { recipe ->
            if (recipe != null) {
                // Populate form fields with recipe data
                binding.editTextTitle.setText(recipe.title)
                binding.editTextDescription.setText(recipe.description)
                binding.editTextIngredients.setText(recipe.ingredients)
                binding.editTextInstructions.setText(recipe.instructions)
                binding.editTextPrepTime.setText(recipe.prepTimeMinutes.toString())
                binding.editTextCookTime.setText(recipe.cookTimeMinutes.toString())
                binding.editTextServings.setText(recipe.servings.toString())
                binding.editTextCalories.setText(recipe.caloriesPerServing.toString())
                binding.dropdownCategory.setText(recipe.category, false)
                binding.dropdownDifficulty.setText(recipe.difficultyLevel, false)
            }
        }
    }
    
    private fun setupSaveButton() {
        binding.buttonSaveRecipe.setOnClickListener {
            if (validateForm()) {
                saveRecipe()
            }
        }
    }
    
    private fun validateForm(): Boolean {
        var isValid = true
        
        // Validate title (required)
        if (binding.editTextTitle.text.isNullOrBlank()) {
            binding.inputLayoutTitle.error = "Title is required"
            isValid = false
        } else {
            binding.inputLayoutTitle.error = null
        }
        
        // Validate ingredients (required)
        if (binding.editTextIngredients.text.isNullOrBlank()) {
            binding.inputLayoutIngredients.error = "Ingredients are required"
            isValid = false
        } else {
            binding.inputLayoutIngredients.error = null
        }
        
        // Validate instructions (required)
        if (binding.editTextInstructions.text.isNullOrBlank()) {
            binding.inputLayoutInstructions.error = "Instructions are required"
            isValid = false
        } else {
            binding.inputLayoutInstructions.error = null
        }
        
        // Category validation
        if (binding.dropdownCategory.text.isNullOrBlank()) {
            binding.inputLayoutCategory.error = "Please select a category"
            isValid = false
        } else {
            binding.inputLayoutCategory.error = null
        }
        
        // Numeric fields validation (optional but must be numbers if provided)
        val prepTime = binding.editTextPrepTime.text.toString()
        val cookTime = binding.editTextCookTime.text.toString()
        val servings = binding.editTextServings.text.toString()
        val calories = binding.editTextCalories.text.toString()
        
        if (prepTime.isNotBlank() && prepTime.toIntOrNull() == null) {
            binding.inputLayoutPrepTime.error = "Must be a number"
            isValid = false
        } else {
            binding.inputLayoutPrepTime.error = null
        }
        
        if (cookTime.isNotBlank() && cookTime.toIntOrNull() == null) {
            binding.inputLayoutCookTime.error = "Must be a number"
            isValid = false
        } else {
            binding.inputLayoutCookTime.error = null
        }
        
        if (servings.isNotBlank() && servings.toIntOrNull() == null) {
            binding.inputLayoutServings.error = "Must be a number"
            isValid = false
        } else {
            binding.inputLayoutServings.error = null
        }
        
        if (calories.isNotBlank() && calories.toIntOrNull() == null) {
            binding.inputLayoutCalories.error = "Must be a number"
            isValid = false
        } else {
            binding.inputLayoutCalories.error = null
        }
        
        return isValid
    }
    
    private fun saveRecipe() {
        val title = binding.editTextTitle.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()
        val ingredients = binding.editTextIngredients.text.toString().trim()
        val instructions = binding.editTextInstructions.text.toString().trim()
        val prepTimeMinutes = binding.editTextPrepTime.text.toString().toIntOrNull() ?: 0
        val cookTimeMinutes = binding.editTextCookTime.text.toString().toIntOrNull() ?: 0
        val servings = binding.editTextServings.text.toString().toIntOrNull() ?: 1
        val caloriesPerServing = binding.editTextCalories.text.toString().toIntOrNull() ?: 0
        val category = binding.dropdownCategory.text.toString()
        val difficultyLevel = binding.dropdownDifficulty.text.toString()
        
        val currentTime = System.currentTimeMillis()
        
        val recipe = if (isEditMode) {
            // Update existing recipe
            Recipe(
                id = recipeId,
                title = title,
                description = description,
                ingredients = ingredients,
                instructions = instructions,
                prepTimeMinutes = prepTimeMinutes,
                cookTimeMinutes = cookTimeMinutes,
                servings = servings,
                caloriesPerServing = caloriesPerServing,
                category = category,
                difficultyLevel = difficultyLevel,
                imageUrl = "", // Handle image URL if needed
                isFavorite = false, // Preserve existing value in a real implementation
                rating = 0f, // Preserve existing value in a real implementation
                createdAt = 0, // Preserve existing value in a real implementation
                updatedAt = currentTime
            )
        } else {
            // Create new recipe
            Recipe(
                title = title,
                description = description,
                ingredients = ingredients,
                instructions = instructions,
                prepTimeMinutes = prepTimeMinutes,
                cookTimeMinutes = cookTimeMinutes,
                servings = servings,
                caloriesPerServing = caloriesPerServing,
                category = category,
                difficultyLevel = difficultyLevel,
                imageUrl = "",
                isFavorite = false,
                rating = 0f,
                createdAt = currentTime,
                updatedAt = currentTime
            )
        }
        
        if (isEditMode) {
            recipeViewModel.updateRecipe(recipe)
            Toast.makeText(requireContext(), "Recipe updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            recipeViewModel.insertRecipe(recipe)
            Toast.makeText(requireContext(), "Recipe added successfully", Toast.LENGTH_SHORT).show()
        }
        
        // Navigate back to recipe list
        findNavController().navigateUp()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
