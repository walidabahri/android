package com.walid.abahri.mealplanner.DB

import androidx.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username AND password_hash = :password")
    suspend fun getUser(username: String, password: String): User?

    @Query("DELETE FROM users")
    suspend fun deleteAll()    // for example, to clear table (optional)
}