package com.walid.abahri.mealplanner.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.walid.abahri.mealplanner.GroceryAdapter
import com.walid.abahri.mealplanner.ViewModel.GroceryViewModel
import com.walid.abahri.mealplanner.databinding.FragmentGroceryListBinding

class GroceryListFragment : Fragment() {
    private var _binding: FragmentGroceryListBinding? = null
    private val binding get() = _binding!!
    private val groceryViewModel: GroceryViewModel by viewModels()
    private lateinit var groceryAdapter: GroceryAdapter  // assumed implemented

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGroceryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        groceryAdapter = GroceryAdapter(
            onItemCheckedChange = { item, isChecked ->
                // Callback from adapter when an item's checkbox is toggled
                val updatedItem = item.copy(acquired = isChecked)
                groceryViewModel.updateItem(updatedItem)
            }
        )
        binding.groceryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.groceryRecyclerView.adapter = groceryAdapter

        // Observe grocery list LiveData
        groceryViewModel.allItems.observe(viewLifecycleOwner) { items ->
            groceryAdapter.submitList(items)
        }

        // (Optional) Could add UI to insert a new item (e.g., a FAB or text field & button)
        // For example, a FAB click could show a dialog to enter item name and quantity,
        // then call groceryViewModel.addItem(new GroceryItem(...))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
