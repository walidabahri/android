package com.walid.abahri.mealplanner.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.walid.abahri.mealplanner.DB.AppDatabase
import com.walid.abahri.mealplanner.DB.GroceryItem
import com.walid.abahri.mealplanner.repository.GroceryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroceryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GroceryRepository
    val allItems: LiveData<List<GroceryItem>>

    init {
        val groceryDao = AppDatabase.Companion.getDatabase(application).groceryItemDao()
        repository = GroceryRepository(groceryDao)
        allItems = repository.allItems    // LiveData list of grocery items
    }

    fun addItem(item: GroceryItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertItem(item)
        }
    }

    fun updateItem(item: GroceryItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateItem(item)
        }
    }

    fun deleteItem(item: GroceryItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(item)
        }
    }
}
