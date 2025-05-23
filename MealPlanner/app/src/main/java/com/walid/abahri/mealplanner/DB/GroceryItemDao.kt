package com.walid.abahri.mealplanner.DB

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Data Access Object for the grocery_items table
 */
@Dao
interface GroceryItemDao {
    /**
     * Get all grocery items ordered by category and name
     */
    @Query("SELECT * FROM grocery_items ORDER BY category, name")
    fun getAllItems(): LiveData<List<GroceryItem>>
    
    /**
     * Get grocery items by category
     */
    @Query("SELECT * FROM grocery_items WHERE category = :category ORDER BY name")
    fun getItemsByCategory(category: String): LiveData<List<GroceryItem>>
    
    /**
     * Get unchecked grocery items
     */
    @Query("SELECT * FROM grocery_items WHERE is_checked = 0 ORDER BY category, name")
    fun getUncheckedItems(): LiveData<List<GroceryItem>>
    
    /**
     * Insert a single grocery item
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: GroceryItem): Long
    
    /**
     * Insert multiple grocery items at once
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<GroceryItem>): List<Long>
    
    /**
     * Update a grocery item
     */
    @Update
    suspend fun updateItem(item: GroceryItem)
    
    /**
     * Delete a grocery item
     */
    @Delete
    suspend fun deleteItem(item: GroceryItem)
    
    /**
     * Update just the checked status of an item
     */
    @Query("UPDATE grocery_items SET is_checked = :isChecked WHERE id = :itemId")
    suspend fun updateItemCheckedStatus(itemId: Int, isChecked: Boolean)
    
    /**
     * Delete all checked items
     */
    @Query("DELETE FROM grocery_items WHERE is_checked = 1")
    suspend fun deleteCheckedItems()
    
    /**
     * Delete all grocery items
     */
    @Query("DELETE FROM grocery_items")
    suspend fun deleteAllItems()
    
    /**
     * Get distinct categories
     */
    @Query("SELECT DISTINCT category FROM grocery_items ORDER BY category")
    fun getCategories(): LiveData<List<String>>
}