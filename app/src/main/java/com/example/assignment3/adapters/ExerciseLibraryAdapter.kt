package com.example.assignment3.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.R
import com.example.assignment3.models.Exercise

class ExerciseLibraryAdapter(
    private val exercises: List<Exercise>,
    private val onExerciseClick: (Exercise) -> Unit
) : RecyclerView.Adapter<ExerciseLibraryAdapter.ExerciseViewHolder>() {

    // ViewHolder holds views for each list item
    class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvExerciseName: TextView = view.findViewById(R.id.tvExerciseName)
        val tvExerciseDetails: TextView = view.findViewById(R.id.tvExerciseDetails)
        val tvDifficulty: TextView = view.findViewById(R.id.tvDifficulty)
    }

    // Create a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise_library, parent, false)
        return ExerciseViewHolder(view)
    }

    // Fill data into item views
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]

        // Set exercise info
        holder.tvExerciseName.text = exercise.name
        holder.tvExerciseDetails.text = "${exercise.sets} × ${exercise.reps} • ${exercise.targetMuscle}"
        holder.tvDifficulty.text = exercise.difficulty

        // Change color by difficulty
        val difficultyColor = when (exercise.difficulty) {
            "Beginner" -> android.R.color.holo_green_light
            "Intermediate" -> android.R.color.holo_orange_light
            "Advanced" -> android.R.color.holo_red_light
            else -> android.R.color.white
        }
        holder.tvDifficulty.setTextColor(holder.itemView.context.getColor(difficultyColor))

        // Click to see exercise details
        holder.itemView.setOnClickListener {
            onExerciseClick(exercise)
        }
    }

    // Return number of exercises
    override fun getItemCount() = exercises.size
}