package com.walid.abahri.mealplanner.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.walid.abahri.mealplanner.DB.AppDatabase
import com.walid.abahri.mealplanner.DB.User
import com.walid.abahri.mealplanner.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepo: UserRepository
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    init {
        // Initialize repository using the database instance
        val userDao = AppDatabase.Companion.getDatabase(application).userDao()
        userRepo = UserRepository(userDao)
        viewModelScope.launch {
            val testUser = User(
                username = "admin", 
                email = "admin@example.com", 
                passwordHash = "admin", 
                dailyCalorieGoal = 2000, 
                dietaryPreferences = null
            )
            userRepo.insertUser(testUser)
        }

    }

    fun login(username: String, password: String) {
        // Launch a coroutine for the login check
        viewModelScope.launch(Dispatchers.IO) {
            // Create a test user if needed
            val testUser = User(
                username = "admin", 
                email = "admin@example.com", 
                passwordHash = password, 
                dailyCalorieGoal = 2000, 
                dietaryPreferences = null
            )
            userRepo.insertUser(testUser)
            val success = userRepo.validateLogin(username, password)
            _loginResult.postValue(success)  // post result to LiveData (true if credentials valid)
        }
    }

    fun register(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepo.insertUser(user)
            // You could post some LiveData flag or result if needed
        }
    }
}
