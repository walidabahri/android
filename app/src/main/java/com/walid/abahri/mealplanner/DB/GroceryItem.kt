package com.walid.abahri.mealplanner.DB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grocery_items")
data class GroceryItem(
    @PrimaryKey(autoGenerate = true) val itemId: Int = 0,
    val name: String,
    val quantity: String,
    val acquired: Boolean = false
    // You might include userId if grocery list is per user
)