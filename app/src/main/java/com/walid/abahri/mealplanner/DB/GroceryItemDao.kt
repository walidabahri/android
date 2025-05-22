package com.walid.abahri.mealplanner.DB

import androidx.lifecycle.LiveData
import androidx.room.*
import com.walid.abahri.mealplanner.DB.GroceryItem

@Dao
interface GroceryItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: GroceryItem)

    @Update
    suspend fun updateItem(item: GroceryItem)

    @Delete
    suspend fun deleteItem(item: GroceryItem)

    @Query("SELECT * FROM grocery_items ORDER BY itemId ASC")
    fun getAllItems(): LiveData<List<GroceryItem>>
}