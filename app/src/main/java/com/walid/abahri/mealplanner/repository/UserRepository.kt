package com.walid.abahri.mealplanner.repository

import com.walid.abahri.mealplanner.DB.User
import com.walid.abahri.mealplanner.DB.UserDao

class UserRepository(private val userDao: UserDao) {
    suspend fun insertUser(user: User) = userDao.insertUser(user)

    suspend fun validateLogin(username: String, password: String): Boolean {
        // Check if a user with given credentials exists
        val user = userDao.getUser(username, password)
        return user != null
    }
}
