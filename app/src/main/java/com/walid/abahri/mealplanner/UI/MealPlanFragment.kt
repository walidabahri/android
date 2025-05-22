package com.walid.abahri.mealplanner.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.walid.abahri.mealplanner.MealPlanAdapter
import com.walid.abahri.mealplanner.ViewModel.MealPlanViewModel
import com.walid.abahri.mealplanner.databinding.FragmentMealPlanBinding

class MealPlanFragment : Fragment() {
    private var _binding: FragmentMealPlanBinding? = null
    private val binding get() = _binding!!
    private val mealPlanViewModel: MealPlanViewModel by viewModels()
    private lateinit var mealPlanAdapter: MealPlanAdapter  // assumed to be implemented

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMealPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mealPlanAdapter = MealPlanAdapter()
        binding.mealPlanRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.mealPlanRecyclerView.adapter = mealPlanAdapter

        // Observe LiveData of meal plans
        mealPlanViewModel.allMealPlans.observe(viewLifecycleOwner) { plans ->
            mealPlanAdapter.submitList(plans)
        }

        // (Optional) handle adding or removing meal plan entries via UI interactions.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
