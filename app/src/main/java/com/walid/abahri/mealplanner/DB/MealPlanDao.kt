package com.walid.abahri.mealplanner.DB

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MealPlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(plan: MealPlan)

    @Delete
    suspend fun deleteMeal(plan: MealPlan)

    @Query("SELECT * FROM meal_plans ORDER BY planId ASC")
    fun getAllMealPlans(): LiveData<List<MealPlan>>

    // Optional: query to get plans for a specific day or user
    @Query("SELECT * FROM meal_plans WHERE day = :day")
    suspend fun getPlansByDay(day: String): List<MealPlan>
}