package com.walid.abahri.mealplanner.DB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val recipeId: Int = 0,
    val name: String,
    val description: String
    // You could add fields like ingredients, etc.
)