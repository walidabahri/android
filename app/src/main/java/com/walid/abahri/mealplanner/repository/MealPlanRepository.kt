package com.walid.abahri.mealplanner.repository

import androidx.lifecycle.LiveData
import com.walid.abahri.mealplanner.DB.MealPlan
import com.walid.abahri.mealplanner.DB.MealPlanDao

class MealPlanRepository(private val mealPlanDao: MealPlanDao) {
    val allMealPlans: LiveData<List<MealPlan>> = mealPlanDao.getAllMealPlans()

    suspend fun insertMeal(plan: MealPlan) = mealPlanDao.insertMeal(plan)
    suspend fun deleteMeal(plan: MealPlan) = mealPlanDao.deleteMeal(plan)
    // You could add methods like getPlansByDay if needed
}
