package com.example.assignment3

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.adapters.ExerciseLibraryAdapter
import com.example.assignment3.models.Exercise
import com.example.assignment3.repository.FirebaseRepository
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch

class ExerciseLibraryActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var tvTitle: TextView
    private lateinit var chipGroup: ChipGroup
    private lateinit var rvExercises: RecyclerView
    private lateinit var exerciseAdapter: ExerciseLibraryAdapter
    private lateinit var repository: FirebaseRepository
    private var allExercises = listOf<Exercise>()
    private var filteredExercises = mutableListOf<Exercise>()

    private val muscleGroups = listOf(
        "All", "Chest", "Back", "Legs", "Arms", "Shoulders", "Core"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_library)
        supportActionBar?.hide()

        repository = (application as MyApplication).repository

        initViews()
        setupChipGroup()
        setupRecyclerView()
        loadExercises()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvTitle = findViewById(R.id.tvTitle)
        chipGroup = findViewById(R.id.chipGroup)
        rvExercises = findViewById(R.id.rvExercises)

        btnBack.setOnClickListener { finish() }
    }

    private fun loadExercises() {
        // LOAD FROM DATABASE
        lifecycleScope.launch {
            try {
                allExercises = repository.getAllExercises()

                filterExercises("All")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupChipGroup() {
        for (muscleGroup in muscleGroups) {
            val chip = Chip(this)
            chip.text = muscleGroup
            chip.isCheckable = true
            chip.isChecked = muscleGroup == "All"

            chip.setOnClickListener {
                // Uncheck all chips
                for (i in 0 until chipGroup.childCount) {
                    (chipGroup.getChildAt(i) as Chip).isChecked = false
                }
                chip.isChecked = true
                filterExercises(muscleGroup)
            }

            chipGroup.addView(chip)
        }
    }

    private fun setupRecyclerView() {
        exerciseAdapter = ExerciseLibraryAdapter(filteredExercises) { exercise ->

            // OPEN EXERCISE DETAL
            val intent = Intent(this, ExerciseDetailActivity::class.java)
            intent.putExtra("EXERCISE_ID", exercise.id)
            startActivity(intent)
        }

        rvExercises.layoutManager = LinearLayoutManager(this)
        rvExercises.adapter = exerciseAdapter
    }

    private fun filterExercises(muscleGroup: String) {

        filteredExercises.clear()

        if (muscleGroup == "All") {
            filteredExercises.addAll(allExercises)
        } else {
            filteredExercises.addAll(allExercises.filter { it.targetMuscle == muscleGroup })
        }

        exerciseAdapter.notifyDataSetChanged()
    }
}