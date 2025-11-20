package com.example.assignment3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.adapters.ExerciseAdapter
import com.example.assignment3.models.Exercise
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

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

        isSelectionMode = intent.getBooleanExtra("SELECTION_MODE", false)

        initViews()
        loadExercises()
        setupChipGroup()
        setupRecyclerView()
        filterExercises("All")
        updateBottomBar()
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

        //  CLICK ADD button SELECTED
        btnAddSelected.setOnClickListener {
            if (selectedExercises.isNotEmpty()) {
                returnSelectedExercises()
            }
        }
    }

    private fun loadExercises() {
        allExercises = listOf(
            // CHEST
            Exercise(1, "Bench Press", 4, "8-10", "Chest"),
            Exercise(2, "Incline Bench Press", 4, "8-10", "Chest"),
            Exercise(3, "Dumbbell Flyes", 3, "10-12", "Chest"),
            Exercise(4, "Push-ups", 3, "12-15", "Chest"),
            Exercise(5, "Cable Crossover", 3, "12-15", "Chest"),

            // BACK
            Exercise(6, "Deadlift", 4, "6-8", "Back"),
            Exercise(7, "Pull-ups", 4, "8-10", "Back"),
            Exercise(8, "Barbell Row", 4, "8-10", "Back"),
            Exercise(9, "Lat Pulldown", 3, "10-12", "Back"),
            Exercise(10, "Cable Row", 3, "10-12", "Back"),

            // LEGS
            Exercise(11, "Squat", 4, "8-10", "Legs"),
            Exercise(12, "Leg Press", 4, "10-12", "Legs"),
            Exercise(13, "Romanian Deadlift", 3, "8-10", "Legs"),
            Exercise(14, "Leg Curl", 3, "10-12", "Legs"),
            Exercise(15, "Leg Extension", 3, "10-12", "Legs"),
            Exercise(16, "Calf Raises", 4, "12-15", "Legs"),

            // ARMS
            Exercise(17, "Barbell Curl", 3, "8-10", "Arms"),
            Exercise(18, "Hammer Curl", 3, "10-12", "Arms"),
            Exercise(19, "Tricep Dips", 3, "8-10", "Arms"),
            Exercise(20, "Skull Crushers", 3, "10-12", "Arms"),

            // SHOULDERS
            Exercise(21, "Overhead Press", 4, "8-10", "Shoulders"),
            Exercise(22, "Lateral Raises", 3, "12-15", "Shoulders"),
            Exercise(23, "Front Raises", 3, "12-15", "Shoulders"),
            Exercise(24, "Rear Delt Flyes", 3, "12-15", "Shoulders"),

            // CORE
            Exercise(25, "Plank", 3, "60s", "Core"),
            Exercise(26, "Crunches", 3, "15-20", "Core"),
            Exercise(27, "Russian Twists", 3, "20-30", "Core"),
            Exercise(28, "Leg Raises", 3, "12-15", "Core")
        )
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
            true
        )


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

    //  UPDATE BOTTOM BAR
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

        setResult(RESULT_OK, resultIntent)
        finish()
    }
}