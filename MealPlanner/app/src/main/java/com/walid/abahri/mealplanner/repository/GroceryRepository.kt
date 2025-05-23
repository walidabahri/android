package com.walid.abahri.mealplanner.repository

import androidx.lifecycle.LiveData
import com.walid.abahri.mealplanner.DB.GroceryItem
import com.walid.abahri.mealplanner.DB.GroceryItemDao

/**
 * Repository class that provides data operations for Grocery Items
 */
class GroceryRepository(private val groceryDao: GroceryItemDao) {
    // Get all grocery items as LiveData
    val allItems: LiveData<List<GroceryItem>> = groceryDao.getAllItems()
    
    /**
     * Insert a grocery item
     */
    suspend fun insertItem(item: GroceryItem) = groceryDao.insertItem(item)
    
    /**
     * Insert multiple grocery items at once
     */
    suspend fun insertItems(items: List<GroceryItem>) = groceryDao.insertItems(items)
    
    /**
     * Update a grocery item
     */
    suspend fun updateItem(item: GroceryItem) = groceryDao.updateItem(item)
    
    /**
     * Update just the checked status of an item
     */
    suspend fun updateItemCheckedStatus(itemId: Int, isChecked: Boolean) = 
        groceryDao.updateItemCheckedStatus(itemId, isChecked)
    
    /**
     * Delete a grocery item
     */
    suspend fun deleteItem(item: GroceryItem) = groceryDao.deleteItem(item)
    
    /**
     * Clear all completed (checked) grocery items
     */
    suspend fun clearCompletedItems() = groceryDao.deleteCheckedItems()
    
    /**
     * Clear all grocery items
     */
    suspend fun clearAllItems() = groceryDao.deleteAllItems()
}
