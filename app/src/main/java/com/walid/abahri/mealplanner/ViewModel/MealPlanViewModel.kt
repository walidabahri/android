package com.walid.abahri.mealplanner.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.walid.abahri.mealplanner.DB.AppDatabase
import com.walid.abahri.mealplanner.DB.MealPlan
import com.walid.abahri.mealplanner.repository.MealPlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MealPlanViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MealPlanRepository
    val allMealPlans: LiveData<List<MealPlan>>

    init {
        val mealPlanDao = AppDatabase.Companion.getDatabase(application).mealPlanDao()
        repository = MealPlanRepository(mealPlanDao)
        allMealPlans = repository.allMealPlans   // LiveData list of meal plans
    }

    fun addMealPlan(plan: MealPlan) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertMeal(plan)
        }
    }

    fun deleteMealPlan(plan: MealPlan) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMeal(plan)
        }
    }
}
