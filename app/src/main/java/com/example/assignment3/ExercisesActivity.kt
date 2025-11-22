package com.example.assignment3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.adapters.ExerciseAdapter
import com.example.assignment3.models.Exercise
import com.example.assignment3.repository.WorkoutRepository
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch

class ExercisesActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var tvTitle: TextView
    private lateinit var chipGroup: ChipGroup
    private lateinit var rvExercises: RecyclerView
    private lateinit var exerciseAdapter: ExerciseAdapter

    // BOTTOM BAR
    private lateinit var llBottomBar: LinearLayout
    private lateinit var tvSelectedCount: TextView
    private lateinit var btnAddSelected: Button

    // ✅ ROOM REPOSITORY
    private lateinit var repository: WorkoutRepository

    private var allExercises = listOf<Exercise>()
    private var filteredExercises = mutableListOf<Exercise>()
    private var selectedMuscleGroup = "All"
    private var isSelectionMode = false

    // SAVE EXERCISES
    private val selectedExercises = mutableSetOf<Exercise>()

    private val muscleGroups = listOf(
        "All", "Chest", "Back", "Legs", "Arms", "Shoulders", "Core"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercises)
        supportActionBar?.hide()

        android.util.Log.e("ExercisesActivity", "🟢 ========== onCreate started ==========")

        isSelectionMode = intent.getBooleanExtra("SELECTION_MODE", false)

        // ✅ GET REPOSITORY FROM APPLICATION
        repository = (application as MyApplication).repository

        initViews()
        setupChipGroup()
        setupRecyclerView()
        loadExercises()  // ✅ LOAD FROM DATABASE
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvTitle = findViewById(R.id.tvTitle)
        chipGroup = findViewById(R.id.chipGroup)
        rvExercises = findViewById(R.id.rvExercises)

        // BOTTOM BAR
        llBottomBar = findViewById(R.id.llBottomBar)
        tvSelectedCount = findViewById(R.id.tvSelectedCount)
        btnAddSelected = findViewById(R.id.btnAddSelected)

        btnBack.setOnClickListener { finish() }

        // CLICK ADD button SELECTED
        btnAddSelected.setOnClickListener {
            if (selectedExercises.isNotEmpty()) {
                returnSelectedExercises()
            }
        }
    }

    private fun loadExercises() {
        // ✅ LOAD FROM DATABASE USING COROUTINE
        lifecycleScope.launch {
            try {
                allExercises = repository.getAllExercises()
                android.util.Log.e("ExercisesActivity", "✅ Loaded ${allExercises.size} exercises from DB")

                filterExercises("All")
                updateBottomBar()

            } catch (e: Exception) {
                android.util.Log.e("ExercisesActivity", "❌ Error loading exercises: ${e.message}")
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
                for (i in 0 until chipGroup.childCount) {
                    val otherChip = chipGroup.getChildAt(i) as Chip
                    otherChip.isChecked = false
                }
                chip.isChecked = true

                selectedMuscleGroup = muscleGroup
                filterExercises(muscleGroup)
            }

            chipGroup.addView(chip)
        }
    }

    private fun setupRecyclerView() {
        // NEW ADAPTER and SELECTION MODE
        exerciseAdapter = ExerciseAdapter(
            filteredExercises,
            selectedExercises,
            { updateBottomBar() },
            true  // isSelectionMode = true
        )

        rvExercises.layoutManager = LinearLayoutManager(this)
        rvExercises.adapter = exerciseAdapter
    }

    private fun filterExercises(muscleGroup: String) {
        android.util.Log.e("ExercisesActivity", "Filtering by: $muscleGroup")

        filteredExercises.clear()

        if (muscleGroup == "All") {
            filteredExercises.addAll(allExercises)
        } else {
            filteredExercises.addAll(allExercises.filter { it.targetMuscle == muscleGroup })
        }

        android.util.Log.e("ExercisesActivity", "Filtered: ${filteredExercises.size} exercises")

        exerciseAdapter.notifyDataSetChanged()
    }

    // UPDATE BOTTOM BAR
    private fun updateBottomBar() {
        val count = selectedExercises.size

        if (count > 0) {
            llBottomBar.visibility = View.VISIBLE
            val text = if (count == 1) "1 exercise selected" else "$count exercises selected"
            tvSelectedCount.text = text
            btnAddSelected.text = "Add $count Exercise${if (count > 1) "s" else ""}"
        } else {
            llBottomBar.visibility = View.GONE
        }
    }

    // LIST OF SELECTED EXERCISES
    private fun returnSelectedExercises() {
        android.util.Log.e("ExercisesActivity", "Returning ${selectedExercises.size} selected exercises")

        val resultIntent = Intent()
        val selectedList = ArrayList(selectedExercises.toList())

        val ids = selectedList.map { it.id }.toIntArray()
        val names = selectedList.map { it.name }.toTypedArray()
        val setsArray = selectedList.map { it.sets }.toIntArray()
        val repsArray = selectedList.map { it.reps }.toTypedArray()
        val musclesArray = selectedList.map { it.targetMuscle }.toTypedArray()

        resultIntent.putExtra("EXERCISE_IDS", ids)
        resultIntent.putExtra("EXERCISE_NAMES", names)
        resultIntent.putExtra("EXERCISE_SETS", setsArray)
        resultIntent.putExtra("EXERCISE_REPS", repsArray)
        resultIntent.putExtra("EXERCISE_MUSCLES", musclesArray)

        // ✅ OPTIONAL: Save to database (if called from WorkoutPlannerActivity)
        val dayIndex = intent.getIntExtra("DAY_INDEX", -1)
        if (dayIndex != -1) {
            lifecycleScope.launch {
                try {
                    selectedList.forEach { exercise ->
                        repository.addExerciseToDay(dayIndex, exercise.id)
                    }
                    android.util.Log.e("ExercisesActivity", "✅ Added ${selectedList.size} exercises to day $dayIndex in DB")
                } catch (e: Exception) {
                    android.util.Log.e("ExercisesActivity", "❌ Error saving: ${e.message}")
                }
            }
        }

        setResult(RESULT_OK, resultIntent)
        finish()
    }
}