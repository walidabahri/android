package com.walid.abahri.mealplanner.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.walid.abahri.mealplanner.DB.GroceryItem

@Database(
    entities = [User::class, Recipe::class, MealPlan::class, GroceryItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recipeDao(): RecipeDao
    abstract fun mealPlanDao(): MealPlanDao
    abstract fun groceryItemDao(): GroceryItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Return the existing instance if exists (to avoid multiple instances)
            return INSTANCE ?: synchronized(this) {
                // Build the database if not already created
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "meal_planner_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}