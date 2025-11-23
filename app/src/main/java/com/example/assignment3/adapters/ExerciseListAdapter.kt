package com.example.assignment3.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.R
import com.example.assignment3.models.Exercise

class ExerciseListAdapter(
    private val exercises: List<Exercise>,
    private val onExerciseClick: (Exercise) -> Unit
) : RecyclerView.Adapter<ExerciseListAdapter.ExerciseViewHolder>() {

    // Holds references to views for each item
    class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvExerciseName: TextView = view.findViewById(R.id.tvExerciseName)
        val tvExerciseDetails: TextView = view.findViewById(R.id.tvExerciseDetails)
        val tvDifficulty: TextView = view.findViewById(R.id.tvDifficulty)
    }

    // Create ViewHolder for a new item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    // Bind exercise data to the item
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]

        // Set exercise info text
        holder.tvExerciseName.text = exercise.name
        holder.tvExerciseDetails.text = "${exercise.sets} × ${exercise.reps} • ${exercise.targetMuscle}"
        holder.tvDifficulty.text = exercise.difficulty

        // Change difficulty text color
        val difficultyColor = when (exercise.difficulty) {
            "Beginner" -> android.R.color.holo_green_light
            "Intermediate" -> android.R.color.holo_orange_light
            "Advanced" -> android.R.color.holo_red_light
            else -> android.R.color.white
        }
        holder.tvDifficulty.setTextColor(holder.itemView.context.getColor(difficultyColor))

        // Handle item click
        holder.itemView.setOnClickListener {
            onExerciseClick(exercise)
        }
    }

    // Return total number of exercises
    override fun getItemCount() = exercises.size
}