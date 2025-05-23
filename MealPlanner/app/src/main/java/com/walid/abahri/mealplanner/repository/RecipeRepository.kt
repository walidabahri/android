package com.walid.abahri.mealplanner.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.walid.abahri.mealplanner.DB.Recipe
import com.walid.abahri.mealplanner.DB.RecipeDao
import kotlinx.coroutines.flow.Flow

class RecipeRepository(private val recipeDao: RecipeDao) {

    // Get all recipes
    fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes()
    }

    // Get recipes by category
    fun getRecipesByCategory(category: String): Flow<List<Recipe>> {
        return recipeDao.getRecipesByCategory(category)
    }

    // Get favorite recipes
    fun getFavoriteRecipes(): Flow<List<Recipe>> {
        return recipeDao.getFavoriteRecipes()
    }

    // Get recipe by ID
    fun getRecipeById(id: Int): Flow<Recipe?> {
        return recipeDao.getRecipeById(id)
    }

    // Search recipes
    fun searchRecipes(query: String): Flow<List<Recipe>> {
        return recipeDao.searchRecipes(query)
    }

    // Filter recipes by category and cooking time
    fun getRecipesByCategoryAndTime(category: String, maxTimeMinutes: Int): Flow<List<Recipe>> {
        return recipeDao.getRecipesByCategoryAndTime(category, maxTimeMinutes)
    }

    // Get all categories
    fun getAllCategories(): Flow<List<String>> {
        return recipeDao.getAllCategories()
    }

    // Insert recipe
    @WorkerThread
    suspend fun insertRecipe(recipe: Recipe): Long {
        return recipeDao.insertRecipe(recipe)
    }

    // Update recipe
    @WorkerThread
    suspend fun updateRecipe(recipe: Recipe) {
        recipeDao.updateRecipe(recipe.copy(updatedAt = System.currentTimeMillis()))
    }

    // Delete recipe
    @WorkerThread
    suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(recipe)
    }

    // Toggle favorite status
    @WorkerThread
    suspend fun toggleFavorite(recipeId: Int, isFavorite: Boolean) {
        recipeDao.updateFavoriteStatus(recipeId, isFavorite)
    }

    // Update rating
    @WorkerThread
    suspend fun updateRating(recipeId: Int, rating: Float) {
        recipeDao.updateRating(recipeId, rating)
    }

    // Delete recipe by ID
    @WorkerThread
    suspend fun deleteRecipeById(recipeId: Int) {
        recipeDao.deleteRecipeById(recipeId)
    }
}
