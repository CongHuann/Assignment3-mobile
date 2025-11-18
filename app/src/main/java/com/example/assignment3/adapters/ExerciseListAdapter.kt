package com.example.assignment3.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.R
import com.example.assignment3.models.Exercise

/**
 * ExerciseListAdapter - RecyclerView adapter for displaying exercises
 * Supports 2 modes:
 * 1. VIEW MODE (isSelectable = false) - Just display exercises, no checkboxes
 * 2. SELECTION MODE (isSelectable = true) - Allow selecting multiple exercises with checkboxes
 *
 * @param exercises - List of all available exercises
 * @param selectedExercises - Set to track which exercises are selected (only used in selection mode)
 * @param onSelectionChanged - Callback triggered when selection changes (only used in selection mode)
 * @param isSelectable - Whether checkboxes should be shown (true = selection mode, false = view mode)
 */
class ExerciseListAdapter(
    private val exercises: List<Exercise>,
    private val selectedExercises: MutableSet<Exercise> = mutableSetOf(),
    private val onSelectionChanged: () -> Unit = {},
    private val isSelectable: Boolean = true  // ✅ NEW: Default = true for backward compatibility
) : RecyclerView.Adapter<ExerciseListAdapter.ExerciseViewHolder>() {

    /**
     * ViewHolder - Holds references to views in each list item
     */
    class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvExerciseName: TextView = view.findViewById(R.id.tvExerciseName)
        val tvExerciseDetails: TextView = view.findViewById(R.id.tvExerciseDetails)
        val cbSelected: CheckBox = view.findViewById(R.id.cbSelected)
    }

    /**
     * Create new view holder when RecyclerView needs one
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise_list, parent, false)
        return ExerciseViewHolder(view)
    }

    /**
     * Bind data to view holder at given position
     * Displays different UI based on mode (view-only vs selection)
     */
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]

        // Set exercise name (e.g., "Bench Press")
        holder.tvExerciseName.text = exercise.name

        // Set exercise details (e.g., "4 sets × 8-10 reps • Chest")
        holder.tvExerciseDetails.text = "${exercise.sets} sets × ${exercise.reps} reps • ${exercise.targetMuscle}"

        // ✅ HANDLE VIEW MODE vs SELECTION MODE
        if (isSelectable) {
            // SELECTION MODE - Show checkbox, allow selection
            holder.cbSelected.visibility = View.VISIBLE
            holder.cbSelected.isChecked = selectedExercises.contains(exercise)

            // Handle clicks on both item and checkbox
            val clickListener = View.OnClickListener {
                // Toggle selection: add if not selected, remove if already selected
                if (selectedExercises.contains(exercise)) {
                    selectedExercises.remove(exercise)
                } else {
                    selectedExercises.add(exercise)
                }

                // Update checkbox to match new state
                holder.cbSelected.isChecked = selectedExercises.contains(exercise)

                // Notify activity that selection changed (updates "Add X exercises" button)
                onSelectionChanged()
            }

            // Apply click listener to both the whole item and the checkbox
            holder.itemView.setOnClickListener(clickListener)
            holder.cbSelected.setOnClickListener(clickListener)

        } else {
            // VIEW MODE - Hide checkbox, no selection
            holder.cbSelected.visibility = View.GONE

            // Remove any click listeners
            holder.itemView.setOnClickListener(null)
            holder.cbSelected.setOnClickListener(null)

            // Optional: Add ripple effect for visual feedback
            holder.itemView.isClickable = true
            holder.itemView.isFocusable = true
        }
    }

    /**
     * Return total number of items
     */
    override fun getItemCount() = exercises.size
}