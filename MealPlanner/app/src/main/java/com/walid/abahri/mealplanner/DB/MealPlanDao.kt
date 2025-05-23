package com.walid.abahri.mealplanner.DB

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPlanDao {
    // Get all meal plans
    @Query("SELECT * FROM meal_plans ORDER BY date, meal_type")
    fun getAllMealPlans(): LiveData<List<MealPlan>>
    
    // Get meal plans for a specific date
    @Query("SELECT * FROM meal_plans WHERE date = :date ORDER BY meal_type")
    fun getMealPlansForDate(date: String): LiveData<List<MealPlan>>

    // Get meal plans for a date range (non-LiveData for synchronous access)
    @Query("SELECT * FROM meal_plans WHERE date BETWEEN :startDate AND :endDate ORDER BY date, meal_type")
    suspend fun getMealPlansInRange(startDate: String, endDate: String): List<MealPlan>
    
    // Get weekly meal plans with LiveData
    @Query("SELECT * FROM meal_plans WHERE date BETWEEN :startDate AND :endDate ORDER BY date, meal_type")
    fun getMealPlansForWeek(startDate: String, endDate: String): LiveData<List<MealPlan>>

    // Insert a meal plan
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(mealPlan: MealPlan)

    // Delete a meal plan
    @Delete
    suspend fun deleteMeal(mealPlan: MealPlan)

    // Delete all meal plans for a specific date
    @Query("DELETE FROM meal_plans WHERE date = :date")
    suspend fun deleteMealPlansForDate(date: String)
    
    // Delete meal by date and type
    @Query("DELETE FROM meal_plans WHERE date = :date AND meal_type = :mealType")
    suspend fun deleteMealByDateAndType(date: String, mealType: String)
    
    // Update servings for a meal plan
    @Query("UPDATE meal_plans SET servings = :servings WHERE id = :mealPlanId")
    suspend fun updateMealPlanServings(mealPlanId: Int, servings: Int)
}