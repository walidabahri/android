package com.walid.abahri.mealplanner.ui


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.walid.abahri.mealplanner.RecipeAdapter
import com.walid.abahri.mealplanner.ViewModel.RecipeViewModel
import com.walid.abahri.mealplanner.databinding.FragmentRecipeListBinding

class RecipeListFragment : Fragment() {
    private var _binding: FragmentRecipeListBinding? = null
    private val binding get() = _binding!!
    private val recipeViewModel: RecipeViewModel by viewModels()
    // Assume we have a RecipeAdapter (not shown) for the RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        recipeAdapter = RecipeAdapter()  // you'd implement this adapter separately
        binding.recipeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recipeRecyclerView.adapter = recipeAdapter

        // Observe the LiveData list of recipes
        recipeViewModel.allRecipes.observe(viewLifecycleOwner) { recipes ->
            // Update the RecyclerView's adapter with new data
            recipeAdapter.submitList(recipes)
        }

        // (Optional) If adding new recipes via a menu or FAB:
        // e.g., listen for a FAB click to add a dummy recipe or navigate to an add screen.
        // Not implemented here for brevity.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
