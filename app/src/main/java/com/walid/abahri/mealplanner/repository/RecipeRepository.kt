package com.walid.abahri.mealplanner.repository

import androidx.lifecycle.LiveData
import com.walid.abahri.mealplanner.DB.Recipe
import com.walid.abahri.mealplanner.DB.RecipeDao

class RecipeRepository(private val recipeDao: RecipeDao) {
    val allRecipes: LiveData<List<Recipe>> = recipeDao.getAllRecipes()  // LiveData stream of recipes

    suspend fun insertRecipe(recipe: Recipe) = recipeDao.insertRecipe(recipe)
    suspend fun updateRecipe(recipe: Recipe) = recipeDao.updateRecipe(recipe)
    suspend fun deleteRecipe(recipe: Recipe) = recipeDao.deleteRecipe(recipe)
}
