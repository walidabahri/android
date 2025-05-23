package com.walid.abahri.mealplanner.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.walid.abahri.mealplanner.DB.GroceryItem

@Database(
    entities = [User::class, Recipe::class, MealPlan::class, GroceryItem::class],
    version = 2,
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

        // Migration from version 1 to version 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create any tables that don't exist
                // Users table
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS users_new ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "username TEXT NOT NULL, "
                    + "password_hash TEXT NOT NULL, "
                    + "email TEXT NOT NULL, "
                    + "created_at INTEGER NOT NULL, "
                    + "daily_calorie_goal INTEGER, "
                    + "dietary_preferences TEXT)"
                )
                
                // Try to migrate data if possible from old schema to new schema
                try {
                    database.execSQL(
                        "INSERT OR IGNORE INTO users_new (id, username, password_hash, email, created_at) "
                        + "SELECT userId, username, password, '', " + System.currentTimeMillis() + " FROM users"
                    )
                    // Drop the old table
                    database.execSQL("DROP TABLE IF EXISTS users")
                    // Rename the new table to the correct name
                    database.execSQL("ALTER TABLE users_new RENAME TO users")
                } catch (e: Exception) {
                    // If the old table doesn't exist or has a different schema, just create the new table
                    database.execSQL("DROP TABLE IF EXISTS users_new")
                    database.execSQL(
                        "CREATE TABLE IF NOT EXISTS users ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                        + "username TEXT NOT NULL, "
                        + "password_hash TEXT NOT NULL, "
                        + "email TEXT NOT NULL, "
                        + "created_at INTEGER NOT NULL, "
                        + "daily_calorie_goal INTEGER, "
                        + "dietary_preferences TEXT)"
                    )
                }
                
                // --- Start of recipes table migration (safer strategy) ---
                // 1. Rename existing 'recipes' table to 'recipes_old' if it exists.
                var recipesOldExists = false
                try {
                    database.execSQL("ALTER TABLE recipes RENAME TO recipes_old")
                    recipesOldExists = true // If rename succeeded, old table existed.
                } catch (e: Exception) {
                    // Assuming error means 'recipes' table did not exist.
                    // android.util.Log.i("Migration", "Original 'recipes' table not found, will create new one.");
                }
 
                // 2. Create the new 'recipes' table with the correct schema (description TEXT - nullable)
                database.execSQL(
                    "CREATE TABLE recipes (" // Not "IF NOT EXISTS" - we want this exact schema
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "title TEXT NOT NULL, "
                    + "description TEXT, " // Nullable
                    + "ingredients TEXT NOT NULL, "
                    + "instructions TEXT NOT NULL, "
                    + "prep_time_minutes INTEGER NOT NULL, "
                    + "cook_time_minutes INTEGER NOT NULL, "
                    + "servings INTEGER NOT NULL, "
                    + "calories_per_serving INTEGER, "
                    + "category TEXT NOT NULL, "
                    + "difficulty_level TEXT NOT NULL, "
                    + "image_url TEXT, "
                    + "is_favorite INTEGER NOT NULL DEFAULT 0, "
                    + "rating REAL NOT NULL DEFAULT 0, "
                    + "created_at INTEGER NOT NULL, "
                    + "updated_at INTEGER NOT NULL)"
                )
 
                // 3. If 'recipes_old' exists, copy data from it to the new 'recipes' table.
                if (recipesOldExists) {
                    try {
                        database.execSQL(
                            "INSERT INTO recipes (id, title, description, ingredients, instructions, prep_time_minutes, cook_time_minutes, servings, calories_per_serving, category, difficulty_level, image_url, is_favorite, rating, created_at, updated_at) "
                            + "SELECT id, title, description, ingredients, instructions, prep_time_minutes, cook_time_minutes, servings, calories_per_serving, category, difficulty_level, image_url, is_favorite, rating, created_at, updated_at FROM recipes_old"
                        )
                    } catch (e: Exception) {
                        // Data copy failed. New 'recipes' table will be empty.
                        // android.util.Log.e("Migration", "Failed to copy data from recipes_old to recipes: " + e.message);
                    }
                    
                    // 4. Drop 'recipes_old' table.
                    database.execSQL("DROP TABLE recipes_old")
                }
                // --- End of recipes table migration ---
 
                // Migration for 'meal_plans' table
                database.execSQL("ALTER TABLE meal_plans RENAME TO meal_plans_old_temp_migration")

                database.execSQL("""
                    CREATE TABLE meal_plans (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        date TEXT NOT NULL,
                        meal_type TEXT NOT NULL,
                        recipe_id INTEGER NOT NULL,
                        servings INTEGER NOT NULL,
                        notes TEXT,
                        created_at INTEGER NOT NULL
                    )
                """)

                val mealPlanMigrationTime = System.currentTimeMillis()
                database.execSQL("""
                    INSERT INTO meal_plans (id, date, meal_type, recipe_id, servings, notes, created_at)
                    SELECT planId, day, 'Unknown', recipeId, 1, NULL, $mealPlanMigrationTime
                    FROM meal_plans_old_temp_migration
                """)

                database.execSQL("DROP TABLE meal_plans_old_temp_migration")

                // Migration for 'grocery_items' table
                database.execSQL("ALTER TABLE grocery_items RENAME TO grocery_items_old_temp")

                database.execSQL("""
                    CREATE TABLE grocery_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        amount REAL NOT NULL,
                        unit TEXT NOT NULL,
                        category TEXT NOT NULL,
                        is_checked INTEGER NOT NULL,
                        added_date INTEGER NOT NULL,
                        recipe_source_id INTEGER
                    )
                """)

                val groceryMigrationTime = System.currentTimeMillis()
                try {
                    // Copy data from old table to new table
                    // Convert 'acquired' to 'is_checked', parse 'quantity' to extract amount and unit if possible
                    database.execSQL("""
                        INSERT INTO grocery_items (id, name, amount, unit, category, is_checked, added_date, recipe_source_id)
                        SELECT 
                            itemId, 
                            name, 
                            1.0, -- Default amount
                            '', -- Default empty unit
                            'Other', -- Default category
                            acquired, 
                            $groceryMigrationTime, -- Current time as added_date
                            NULL -- No recipe source
                        FROM grocery_items_old_temp
                    """)
                } catch (e: Exception) {
                    // In case of error during data migration, we'll still have the new table structure
                    // Log error if needed
                }

                database.execSQL("DROP TABLE grocery_items_old_temp")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            // Return the existing instance if exists (to avoid multiple instances)
            return INSTANCE ?: synchronized(this) {
                // Build the database if not already created
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "meal_planner_db"
                )
                // First attempt with migration
                .addMigrations(MIGRATION_1_2) // Add the migration strategy
                // Fallback to destructive migration if needed
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}