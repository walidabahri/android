package com.walid.abahri.mealplanner.DB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_plans")
data class MealPlan(
    @PrimaryKey(autoGenerate = true) val planId: Int = 0,
    val recipeId: Int,    // Foreign key referencing a Recipe
    val day: String       // Could be a date or day name (e.g., "2025-05-16" or "Monday")
    // You might include a userId if meal plans are user-specific
)