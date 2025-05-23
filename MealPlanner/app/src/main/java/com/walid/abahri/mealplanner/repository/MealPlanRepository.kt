package com.walid.abahri.mealplanner.repository

import androidx.lifecycle.LiveData
import com.walid.abahri.mealplanner.DB.MealPlan
import com.walid.abahri.mealplanner.DB.MealPlanDao

class MealPlanRepository(private val mealPlanDao: MealPlanDao) {
    val allMealPlans: LiveData<List<MealPlan>> = mealPlanDao.getAllMealPlans()
    
    // Get meal plans for a specific date
    fun getMealPlansForDate(date: String): LiveData<List<MealPlan>> {
        return mealPlanDao.getMealPlansForDate(date)
    }
    
    // Get meal plans within a date range (for grocery list generation)
    suspend fun getMealPlansInRange(startDate: String, endDate: String): List<MealPlan> {
        return mealPlanDao.getMealPlansInRange(startDate, endDate)
    }
    
    // Clear all meal plans for a specific date
    suspend fun clearMealPlansForDate(date: String) {
        mealPlanDao.deleteMealPlansForDate(date)
    }
    
    // Update servings for a meal plan
    suspend fun updateMealPlanServings(mealPlanId: Int, servings: Int) {
        mealPlanDao.updateMealPlanServings(mealPlanId, servings)
    }

    // Basic operations
    suspend fun insertMeal(plan: MealPlan) = mealPlanDao.insertMeal(plan)
    suspend fun deleteMeal(plan: MealPlan) = mealPlanDao.deleteMeal(plan)
}
