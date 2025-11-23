package com.example.assignment3.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.R
import com.example.assignment3.models.SetData

class SetHistoryAdapter(
    private val sets: List<SetData?>
) : RecyclerView.Adapter<SetHistoryAdapter.SetViewHolder>() {

    // Holds views for each set row
    class SetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSetNumber: TextView = view.findViewById(R.id.tvSetNumber)
        val tvSetData: TextView = view.findViewById(R.id.tvSetData)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    // Create ViewHolder for a set
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_set_history, parent, false)
        return SetViewHolder(view)
    }

    // Bind data for each set
    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        val set = sets[position]
        holder.tvSetNumber.text = "Set ${position + 1}"

        // If not completed, show locked
        if (set == null || !set.isCompleted) {
            holder.tvSetData.text = "🔒 Locked"
            holder.tvStatus.text = ""
            holder.itemView.alpha = 0.5f
        } else {
            // Show weight and reps
            holder.tvSetData.text = "${set.weight} kg × ${set.reps} reps"
            holder.tvStatus.text = "✅"
            holder.itemView.alpha = 1.0f
        }
    }

    // How many sets in total
    override fun getItemCount() = sets.size
}