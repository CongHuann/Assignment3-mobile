package com.example.assignment3.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.R
import com.example.assignment3.models.Exercise

class ExerciseListAdapter(
    private val exercises: List<Exercise>,
    private val selectedExercises: MutableSet<Exercise>,  // Save exercises
    private val onSelectionChanged: () -> Unit  // Callback when change selection
) : RecyclerView.Adapter<ExerciseListAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvExerciseName: TextView = view.findViewById(R.id.tvExerciseName)
        val tvExerciseDetails: TextView = view.findViewById(R.id.tvExerciseDetails)
        val cbSelected: CheckBox = view.findViewById(R.id.cbSelected)  //Checkbox
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise_list, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.tvExerciseName.text = exercise.name
        holder.tvExerciseDetails.text = "${exercise.sets} sets × ${exercise.reps} reps • ${exercise.targetMuscle}"

        //  Set checkbox state
        holder.cbSelected.isChecked = selectedExercises.contains(exercise)

        // clich in item or checkbox
        val clickListener = View.OnClickListener {
            if (selectedExercises.contains(exercise)) {
                selectedExercises.remove(exercise)
            } else {
                selectedExercises.add(exercise)
            }
            holder.cbSelected.isChecked = selectedExercises.contains(exercise)
            onSelectionChanged()
        }

        holder.itemView.setOnClickListener(clickListener)
        holder.cbSelected.setOnClickListener(clickListener)
    }

    override fun getItemCount() = exercises.size
}