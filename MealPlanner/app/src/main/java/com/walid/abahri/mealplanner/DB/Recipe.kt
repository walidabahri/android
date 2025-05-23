package com.walid.abahri.mealplanner.DB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "ingredients")
    val ingredients: String, // JSON string format

    @ColumnInfo(name = "instructions")
    val instructions: String,

    @ColumnInfo(name = "prep_time_minutes")
    val prepTimeMinutes: Int,

    @ColumnInfo(name = "cook_time_minutes")
    val cookTimeMinutes: Int,

    @ColumnInfo(name = "servings")
    val servings: Int,

    @ColumnInfo(name = "calories_per_serving")
    val caloriesPerServing: Int?,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "difficulty_level")
    val difficultyLevel: String, // Easy, Medium, Hard

    @ColumnInfo(name = "image_url")
    val imageUrl: String?,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "rating")
    val rating: Float = 0f,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

// Helper data class for ingredients
data class Ingredient(
    val name: String,
    val amount: String,
    val unit: String,
    val category: String? = null
)

// Extension functions for Recipe
fun Recipe.getTotalTimeMinutes(): Int = prepTimeMinutes + cookTimeMinutes

fun Recipe.getIngredientsList(): List<Ingredient> {
    return try {
        // Simple parsing of ingredients from a delimited string format
        // Format expected: "ingredient:amount:unit,ingredient:amount:unit,..."
        ingredients.split(",").mapNotNull { ingredientStr ->
            val parts = ingredientStr.split(":")
            if (parts.size >= 3) {
                Ingredient(
                    name = parts[0].trim(),
                    amount = parts[1].trim(),
                    unit = parts[2].trim()
                )
            } else null
        }
    } catch (e: Exception) {
        emptyList()
    }
}