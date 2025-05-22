package com.walid.abahri.mealplanner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.walid.abahri.mealplanner.DB.GroceryItem

class GroceryAdapter(
    private val onItemCheckedChange: (GroceryItem, Boolean) -> Unit
) : RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder>() {

    private var groceryList = listOf<GroceryItem>()

    fun submitList(list: List<GroceryItem>) {
        groceryList = list
        notifyDataSetChanged()
    }

    inner class GroceryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox = itemView.findViewById<CheckBox>(R.id.checkGrocery)
        val label = itemView.findViewById<TextView>(R.id.textGroceryItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grocery, parent, false)
        return GroceryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        val item = groceryList[position]
        holder.label.text = "${item.quantity} ${item.name}"
        holder.checkbox.setOnCheckedChangeListener(null)
        holder.checkbox.isChecked = item.acquired
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            onItemCheckedChange(item, isChecked)
        }
    }

    override fun getItemCount(): Int = groceryList.size
}
