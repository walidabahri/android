package com.walid.abahri.mealplanner.repository

import androidx.lifecycle.LiveData
import com.walid.abahri.mealplanner.DB.GroceryItem
import com.walid.abahri.mealplanner.DB.GroceryItemDao

class GroceryRepository(private val groceryDao: GroceryItemDao) {
    val allItems: LiveData<List<GroceryItem>> = groceryDao.getAllItems()

    suspend fun insertItem(item: GroceryItem) = groceryDao.insertItem(item)
    suspend fun updateItem(item: GroceryItem) = groceryDao.updateItem(item)
    suspend fun deleteItem(item: GroceryItem) = groceryDao.deleteItem(item)
}
