package com.example.assignment3

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.assignment3.models.Exercise
import com.example.assignment3.repository.FirebaseRepository
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch

class ExerciseFormActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var tvTitle: TextView
    private lateinit var etExerciseName: EditText
    private lateinit var etDescription: EditText
    private lateinit var etSets: EditText
    private lateinit var etReps: EditText
    private lateinit var etEquipment: EditText
    private lateinit var chipGroupMuscle: ChipGroup
    private lateinit var chipGroupDifficulty: ChipGroup
    private lateinit var etInstructions: EditText
    private lateinit var etTips: EditText
    private lateinit var btnSave: Button

    private lateinit var repository: FirebaseRepository
    private var exerciseId: Int = -1
    private var isEditMode = false

    private val muscleGroups = listOf("Chest", "Back", "Legs", "Arms", "Shoulders", "Core")
    private val difficulties = listOf("Beginner", "Intermediate", "Advanced")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_form)
        supportActionBar?.hide()

        repository = (application as MyApplication).repository

        // Check if edit mode
        exerciseId = intent.getIntExtra("EXERCISE_ID", -1)
        isEditMode = exerciseId != -1

        initViews()
        setupChipGroups()
        setupClickListeners()

        if (isEditMode) {
            loadExerciseData()
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvTitle = findViewById(R.id.tvTitle)
        etExerciseName = findViewById(R.id.etExerciseName)
        etDescription = findViewById(R.id.etDescription)
        etSets = findViewById(R.id.etSets)
        etReps = findViewById(R.id.etReps)
        etEquipment = findViewById(R.id.etEquipment)
        chipGroupMuscle = findViewById(R.id.chipGroupMuscle)
        chipGroupDifficulty = findViewById(R.id.chipGroupDifficulty)
        etInstructions = findViewById(R.id.etInstructions)
        etTips = findViewById(R.id.etTips)
        btnSave = findViewById(R.id.btnSave)

        tvTitle.text = if (isEditMode) "Edit Exercise" else "Add New Exercise"
        btnSave.text = if (isEditMode) "Update Exercise" else "Add Exercise"
    }

    private fun setupChipGroups() {
        // Muscle group chips
        muscleGroups.forEach { muscle ->
            val chip = Chip(this)
            chip.text = muscle
            chip.isCheckable = true
            chipGroupMuscle.addView(chip)
        }

        // Difficulty chips
        difficulties.forEach { difficulty ->
            val chip = Chip(this)
            chip.text = difficulty
            chip.isCheckable = true
            chipGroupDifficulty.addView(chip)
        }
    }

    private fun loadExerciseData() {
        lifecycleScope.launch {
            try {
                val exercise = repository.getExerciseById(exerciseId)
                if (exercise != null) {
                    etExerciseName.setText(exercise.name)
                    etDescription.setText(exercise.description)
                    etSets.setText(exercise.sets.toString())
                    etReps.setText(exercise.reps)
                    etEquipment.setText(exercise.equipment)

                    // Select muscle chip
                    for (i in 0 until chipGroupMuscle.childCount) {
                        val chip = chipGroupMuscle.getChildAt(i) as Chip
                        if (chip.text == exercise.targetMuscle) {
                            chip.isChecked = true
                            break
                        }
                    }

                    // Select difficulty chip
                    for (i in 0 until chipGroupDifficulty.childCount) {
                        val chip = chipGroupDifficulty.getChildAt(i) as Chip
                        if (chip.text == exercise.difficulty) {
                            chip.isChecked = true
                            break
                        }
                    }

                    etInstructions.setText(exercise.instructions.joinToString("\n"))
                    etTips.setText(exercise.tips.joinToString("\n"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@ExerciseFormActivity, "Error loading exercise", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            if (validateForm()) {
                saveExercise()
            }
        }
    }

    private fun validateForm(): Boolean {
        if (etExerciseName.text.isBlank()) {
            Toast.makeText(this, "Please enter exercise name", Toast.LENGTH_SHORT).show()
            return false
        }

        if (chipGroupMuscle.checkedChipId == -1) {
            Toast.makeText(this, "Please select target muscle", Toast.LENGTH_SHORT).show()
            return false
        }

        if (chipGroupDifficulty.checkedChipId == -1) {
            Toast.makeText(this, "Please select difficulty", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun saveExercise() {
        val name = etExerciseName.text.toString()
        val description = etDescription.text.toString()
        val sets = etSets.text.toString().toIntOrNull() ?: 3
        val reps = etReps.text.toString().ifBlank { "10-12" }
        val equipment = etEquipment.text.toString().ifBlank { "None" }

        val selectedMuscleChip = findViewById<Chip>(chipGroupMuscle.checkedChipId)
        val targetMuscle = selectedMuscleChip.text.toString()

        val selectedDifficultyChip = findViewById<Chip>(chipGroupDifficulty.checkedChipId)
        val difficulty = selectedDifficultyChip.text.toString()

        val instructions = etInstructions.text.toString()
            .split("\n")
            .filter { it.isNotBlank() }

        val tips = etTips.text.toString()
            .split("\n")
            .filter { it.isNotBlank() }

        val exercise = Exercise(
            id = if (isEditMode) exerciseId else generateNewId(),
            name = name,
            sets = sets,
            reps = reps,
            targetMuscle = targetMuscle,
            description = description,
            instructions = instructions,
            tips = tips,
            difficulty = difficulty,
            equipment = equipment,
            imageUrl = "loader"
        )

        lifecycleScope.launch {
            try {
                if (isEditMode) {
                    repository.updateExercise(exercise)
                    Toast.makeText(this@ExerciseFormActivity, "Exercise updated!", Toast.LENGTH_SHORT).show()
                } else {
                    repository.addExercise(exercise)
                    Toast.makeText(this@ExerciseFormActivity, "Exercise added!", Toast.LENGTH_SHORT).show()
                }

                kotlinx.coroutines.delay(500)

                setResult(RESULT_OK)
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@ExerciseFormActivity, "Error saving exercise", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateNewId(): Int {
        // Generate ID based on timestamp
        return System.currentTimeMillis().toInt()
    }
}