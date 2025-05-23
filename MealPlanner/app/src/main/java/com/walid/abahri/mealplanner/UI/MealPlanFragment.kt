package com.walid.abahri.mealplanner.UI

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.walid.abahri.mealplanner.DB.MealPlan
import com.walid.abahri.mealplanner.R
import com.walid.abahri.mealplanner.ViewModel.MealPlanViewModel
import com.walid.abahri.mealplanner.databinding.FragmentMealPlanBinding
import java.text.SimpleDateFormat
import java.util.*

class MealPlanFragment : Fragment() {
    private var _binding: FragmentMealPlanBinding? = null
    private val binding get() = _binding!!
    private val mealPlanViewModel: MealPlanViewModel by viewModels()

    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val weekDateFormatter = SimpleDateFormat("MMM d", Locale.getDefault())
    private val fullDateFormatter = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMealPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            setupCalendarNavigation()
            setupDayTabs()
            setupMealRecyclerViews()
            setupAddMealButtons()
            setupGroceryListButton()

            // Load initial data for current date
            loadMealPlanForDate(dateFormatter.format(calendar.time))
        } catch (e: Exception) {
            // Catch any exceptions to prevent crashes
            Toast.makeText(context, "Error setting up meal plan: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCalendarNavigation() {
        // Set current week display
        updateWeekDisplay()

        // Previous week button
        binding.buttonPreviousWeek.setOnClickListener {
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            updateWeekDisplay()
            updateDayTabs()
            loadMealPlanForCurrentTab()
        }

        // Next week button
        binding.buttonNextWeek.setOnClickListener {
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            updateWeekDisplay()
            updateDayTabs()
            loadMealPlanForCurrentTab()
        }
    }

    private fun updateWeekDisplay() {
        val startOfWeek = calendar.clone() as Calendar
        startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.firstDayOfWeek)

        val endOfWeek = startOfWeek.clone() as Calendar
        endOfWeek.add(Calendar.DAY_OF_YEAR, 6)

        val weekText = "${weekDateFormatter.format(startOfWeek.time)} - ${weekDateFormatter.format(endOfWeek.time)}, ${endOfWeek.get(Calendar.YEAR)}"
        binding.textViewCurrentWeek.text = weekText
    }

    private fun setupDayTabs() {
        binding.tabLayoutDays.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val date = tab.tag as String
                loadMealPlanForDate(date)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        updateDayTabs()
    }

    private fun updateDayTabs() {
        binding.tabLayoutDays.removeAllTabs()

        val startOfWeek = calendar.clone() as Calendar
        startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.firstDayOfWeek)

        for (i in 0..6) {
            val dayCalendar = startOfWeek.clone() as Calendar
            dayCalendar.add(Calendar.DAY_OF_YEAR, i)

            val dateString = dateFormatter.format(dayCalendar.time)
            val dayOfWeek = SimpleDateFormat("EEE", Locale.getDefault()).format(dayCalendar.time)
            val dayOfMonth = dayCalendar.get(Calendar.DAY_OF_MONTH).toString()

            val tab = binding.tabLayoutDays.newTab()
                .setText("$dayOfWeek\n$dayOfMonth")
                .setTag(dateString)

            binding.tabLayoutDays.addTab(tab)

            // Select today's tab if it's in the current week
            val today = Calendar.getInstance()
            if (dayCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                dayCalendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                tab.select()
            }
        }

        // If no tab is selected (today is not in the current week), select the first day
        if (binding.tabLayoutDays.selectedTabPosition == -1) {
            binding.tabLayoutDays.getTabAt(0)?.select()
        }
    }

    private fun setupMealRecyclerViews() {
        binding.recyclerViewBreakfast.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = MealAdapter { mealPlan ->
                // On click handler
                showMealOptionsDialog(mealPlan)
            }
        }

        binding.recyclerViewLunch.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = MealAdapter { mealPlan ->
                showMealOptionsDialog(mealPlan)
            }
        }

        binding.recyclerViewDinner.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = MealAdapter { mealPlan ->
                showMealOptionsDialog(mealPlan)
            }
        }

        binding.recyclerViewSnacks?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = MealAdapter { mealPlan ->
                showMealOptionsDialog(mealPlan)
            }
        }
    }

    private fun setupAddMealButtons() {
        val selectedDate = binding.tabLayoutDays.getTabAt(
            binding.tabLayoutDays.selectedTabPosition
        )?.tag as? String ?: dateFormatter.format(calendar.time)

        // Navigate to recipe selection with appropriate meal type
        binding.buttonAddBreakfast.setOnClickListener {
            navigateToRecipeSelection(selectedDate, "Breakfast")
        }

        binding.buttonAddLunch.setOnClickListener {
            navigateToRecipeSelection(selectedDate, "Lunch")
        }

        binding.buttonAddDinner.setOnClickListener {
            navigateToRecipeSelection(selectedDate, "Dinner")
        }

        binding.buttonAddSnack?.setOnClickListener {
            navigateToRecipeSelection(selectedDate, "Snack")
        }
    }

    private fun navigateToRecipeSelection(date: String, mealType: String) {
        try {
            val bundle = Bundle().apply {
                putString("date", date)
                putString("mealType", mealType)
            }
            findNavController().navigate(R.id.action_mealPlanFragment_to_recipeSelectionFragment, bundle)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Error navigating to recipe selection: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupGroceryListButton() {
        // Safely set up the grocery list button if it exists
        try {
            binding.fabGenerateGroceryList?.setOnClickListener {
                Toast.makeText(context, "Generate grocery list feature coming soon", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Button might not exist, ignore
        }
    }

    private fun loadMealPlanForCurrentTab() {
        val selectedDate = binding.tabLayoutDays.getTabAt(
            binding.tabLayoutDays.selectedTabPosition
        )?.tag as? String

        selectedDate?.let {
            loadMealPlanForDate(it)
        }
    }

    private fun loadMealPlanForDate(date: String) {
        mealPlanViewModel.getMealPlansForDate(date).observe(viewLifecycleOwner) { mealPlans ->
            // Group meal plans by meal type
            val breakfastPlans = mealPlans.filter { it.mealType.equals("breakfast", ignoreCase = true) }
            val lunchPlans = mealPlans.filter { it.mealType.equals("lunch", ignoreCase = true) }
            val dinnerPlans = mealPlans.filter { it.mealType.equals("dinner", ignoreCase = true) }
            val snackPlans = mealPlans.filter { it.mealType.equals("snack", ignoreCase = true) }

            // Update recycler views
            (binding.recyclerViewBreakfast.adapter as? MealAdapter)?.submitList(breakfastPlans)
            (binding.recyclerViewLunch.adapter as? MealAdapter)?.submitList(lunchPlans)
            (binding.recyclerViewDinner.adapter as? MealAdapter)?.submitList(dinnerPlans)
            (binding.recyclerViewSnacks?.adapter as? MealAdapter)?.submitList(snackPlans)

            // Update nutrition data
            updateNutritionSummary(mealPlans)
        }
    }

    private fun updateNutritionSummary(mealPlans: List<MealPlan>) {
        // Calculate total nutrition values
        var totalCalories = 0
        var totalProtein = 0
        var totalCarbs = 0
        var totalFat = 0

        // Sum up nutrition from all meals
        mealPlans.forEach { mealPlan ->
            // Since getRecipeById returns a Flow, we'll use placeholder values instead
            // In a real implementation, you would observe the Flow
            val caloriesPerServing = 250 // Default placeholder value
            totalCalories += caloriesPerServing * mealPlan.servings
            // Add other nutrition calculations as needed
        }

        // Update the UI with nutrition data if these views exist
        try {
            binding.progressBarCalories?.progress = (totalCalories * 100 / 2000).coerceIn(0, 100)
            binding.textViewCaloriesCount?.text = "$totalCalories/2000"

            binding.progressBarProtein?.progress = (totalProtein * 100 / 75).coerceIn(0, 100)
            binding.textViewProteinCount?.text = "$totalProtein/75g"

            binding.progressBarCarbs?.progress = (totalCarbs * 100 / 250).coerceIn(0, 100)
            binding.textViewCarbsCount?.text = "$totalCarbs/250g"

            binding.progressBarFat?.progress = (totalFat * 100 / 70).coerceIn(0, 100)
            binding.textViewFatCount?.text = "$totalFat/70g"
        } catch (e: Exception) {
            // Handle missing views gracefully
        }
    }

    private fun showMealOptionsDialog(mealPlan: MealPlan) {
        val options = arrayOf("View Recipe", "Edit Servings", "Remove from Plan")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Meal Options")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // View Recipe - Navigate to RecipeDetailFragment
                        try {
                            findNavController().navigate(
                                R.id.action_mealPlanFragment_to_recipeDetailFragment,
                                Bundle().apply {
                                    putInt("recipeId", mealPlan.recipeId)
                                }
                            )
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Error navigating to recipe details: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    1 -> {
                        // Edit Servings
                        showEditServingsDialog(mealPlan)
                    }
                    2 -> {
                        // Remove from Plan
                        showDeleteConfirmationDialog(mealPlan)
                    }
                }
            }
            .show()
    }

    private fun showEditServingsDialog(mealPlan: MealPlan) {
        val currentServings = mealPlan.servings
        var newServings = currentServings
        
        // Create a custom dialog with a number picker
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_servings_picker, null)
        val numberPicker = dialogView.findViewById<TextView>(R.id.text_servings_count)
        val decreaseButton = dialogView.findViewById<View>(R.id.button_decrease_servings)
        val increaseButton = dialogView.findViewById<View>(R.id.button_increase_servings)
        
        // Set initial value
        numberPicker.text = newServings.toString()
        
        // Setup decrease button
        decreaseButton.setOnClickListener {
            if (newServings > 1) {
                newServings--
                numberPicker.text = newServings.toString()
            }
        }
        
        // Setup increase button
        increaseButton.setOnClickListener {
            newServings++
            numberPicker.text = newServings.toString()
        }
        
        // Create and show the dialog
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Servings")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                if (newServings != currentServings) {
                    // Update meal plan servings
                    mealPlanViewModel.updateMealPlanServings(mealPlan.id, newServings)
                    Toast.makeText(context, "Servings updated", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(mealPlan: MealPlan) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Remove Meal")
            .setMessage("Are you sure you want to remove this meal from your plan?")
            .setPositiveButton("Remove") { _, _ ->
                mealPlanViewModel.deleteMealPlan(mealPlan)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Simple adapter for meal plans
    private inner class MealAdapter(
        private val onItemClick: (MealPlan) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

        private var mealPlans = listOf<MealPlan>()

        fun submitList(list: List<MealPlan>) {
            mealPlans = list
            notifyDataSetChanged()
        }

        inner class MealViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
            val titleTextView = itemView.findViewById<TextView>(R.id.text_meal_recipe_title)
            val servingsTextView = itemView.findViewById<TextView>(R.id.text_meal_servings)
            val deleteButton = itemView.findViewById<View?>(R.id.button_delete_meal)

            init {
                itemView.setOnClickListener {
                    if (adapterPosition != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                        onItemClick(mealPlans[adapterPosition])
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_meal, parent, false)
            return MealViewHolder(view)
        }

        override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
            val meal = mealPlans[position]
            
            // Load the recipe title from the database
            mealPlanViewModel.getRecipeById(meal.recipeId).observe(viewLifecycleOwner) { recipe ->
                if (recipe != null) {
                    holder.titleTextView.text = recipe.title
                } else {
                    holder.titleTextView.text = "Recipe #${meal.recipeId}"
                }
            }
            
            holder.servingsTextView.text = "${meal.servings} serving(s)"
            
            // Add delete button click listener
            holder.deleteButton?.setOnClickListener {
                showDeleteConfirmationDialog(meal)
            }
        }

        override fun getItemCount() = mealPlans.size
    }
}