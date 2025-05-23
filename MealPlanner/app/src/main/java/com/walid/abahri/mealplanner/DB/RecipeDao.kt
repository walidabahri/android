package com.walid.abahri.mealplanner.DB

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY created_at DESC")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE category = :category ORDER BY title ASC")
    fun getRecipesByCategory(category: String): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE is_favorite = 1 ORDER BY title ASC")
    fun getFavoriteRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getRecipeById(id: Int): Flow<Recipe?>

    @Query("""
        SELECT * FROM recipes 
        WHERE title LIKE '%' || :query || '%' 
           OR ingredients LIKE '%' || :query || '%'
           OR description LIKE '%' || :query || '%'
        ORDER BY title ASC
    """)
    fun searchRecipes(query: String): Flow<List<Recipe>>

    @Query("""
        SELECT * FROM recipes 
        WHERE category = :category 
          AND prep_time_minutes + cook_time_minutes <= :maxTimeMinutes
        ORDER BY title ASC
    """)
    fun getRecipesByCategoryAndTime(category: String, maxTimeMinutes: Int): Flow<List<Recipe>>

    @Query("SELECT DISTINCT category FROM recipes ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Query("UPDATE recipes SET is_favorite = :isFavorite WHERE id = :recipeId")
    suspend fun updateFavoriteStatus(recipeId: Int, isFavorite: Boolean)

    @Query("UPDATE recipes SET rating = :rating WHERE id = :recipeId")
    suspend fun updateRating(recipeId: Int, rating: Float)

    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteRecipeById(recipeId: Int)
}