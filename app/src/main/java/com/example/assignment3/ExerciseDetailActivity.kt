package com.example.assignment3

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.assignment3.models.Exercise
import com.example.assignment3.repository.FirebaseRepository
import kotlinx.coroutines.launch

class ExerciseDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var ivExerciseImage: ImageView
    private lateinit var tvExerciseName: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvDifficulty: TextView
    private lateinit var tvEquipment: TextView
    private lateinit var tvTargetMuscle: TextView
    private lateinit var tvSecondaryMuscles: TextView
    private lateinit var tvInstructions: TextView
    private lateinit var tvTips: TextView
    private lateinit var btnAddToWorkout: Button

    private lateinit var repository: FirebaseRepository
    private var exercise: Exercise? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_detail)
        supportActionBar?.hide()


        repository = (application as MyApplication).repository

        initViews()
        loadExercise()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        ivExerciseImage = findViewById(R.id.ivExerciseImage)
        tvExerciseName = findViewById(R.id.tvExerciseName)
        tvDescription = findViewById(R.id.tvDescription)
        tvDifficulty = findViewById(R.id.tvDifficulty)
        tvEquipment = findViewById(R.id.tvEquipment)
        tvTargetMuscle = findViewById(R.id.tvTargetMuscle)
        tvSecondaryMuscles = findViewById(R.id.tvSecondaryMuscles)
        tvInstructions = findViewById(R.id.tvInstructions)
        tvTips = findViewById(R.id.tvTips)
        btnAddToWorkout = findViewById(R.id.btnAddToWorkout)
    }

    private fun loadExercise() {
        val exerciseId = intent.getIntExtra("EXERCISE_ID", -1)


        if (exerciseId == -1) {
            Toast.makeText(this, "Exercise not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                exercise = repository.getExerciseById(exerciseId)

                if (exercise == null) {
                    Toast.makeText(this@ExerciseDetailActivity, "Exercise not found", Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }

                displayExercise(exercise!!)
            } catch (e: Exception) {
                android.util.Log.e("ExerciseDetail", "❌ Error: ${e.message}")
                Toast.makeText(this@ExerciseDetailActivity, "Error loading exercise", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun displayExercise(exercise: Exercise) {

        // LOAD IMAGE FROM DRAWABLE
        loadExerciseImage(exercise)

        tvExerciseName.text = exercise.name
        tvDescription.text = exercise.description

        tvDifficulty.text = exercise.difficulty
        val difficultyColor = when (exercise.difficulty) {
            "Beginner" -> android.R.color.holo_green_light
            "Intermediate" -> android.R.color.holo_orange_light
            "Advanced" -> android.R.color.holo_red_light
            else -> android.R.color.white
        }
        tvDifficulty.setTextColor(getColor(difficultyColor))

        tvEquipment.text = "Equipment: ${exercise.equipment}"
        tvTargetMuscle.text = "Primary: ${exercise.targetMuscle}"

        if (exercise.secondaryMuscles.isNotEmpty()) {
            tvSecondaryMuscles.text = "Secondary: ${exercise.secondaryMuscles.joinToString(", ")}"
        } else {
            tvSecondaryMuscles.text = "Secondary: None"
        }

        if (exercise.instructions.isNotEmpty()) {
            val instructionsText = exercise.instructions
                .mapIndexed { index, instruction -> "${index + 1}. $instruction" }
                .joinToString("\n\n")
            tvInstructions.text = instructionsText
        } else {
            tvInstructions.text = "No instructions available"
        }

        if (exercise.tips.isNotEmpty()) {
            val tipsText = exercise.tips
                .mapIndexed { index, tip -> "💡 $tip" }
                .joinToString("\n\n")
            tvTips.text = tipsText
        } else {
            tvTips.text = "No tips available"
        }
    }

    //LOAD FROM drawable
    private fun loadExerciseImage(exercise: Exercise) {
        val drawableId = if (exercise.imageUrl.isNotEmpty()) {
            // Try to find drawable by name from imageUrl
            val resId = resources.getIdentifier(
                exercise.imageUrl,  // image
                "drawable",         // resource type
                packageName         // com.example.assignment3
            )

            if (resId != 0) {
                resId
            } else {
                R.drawable.dumbbell
            }
        } else {
            R.drawable.dumbbell
        }

        ivExerciseImage.setImageResource(drawableId)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnAddToWorkout.setOnClickListener {
            Toast.makeText(this, "Feature coming soon! Go to Workout Planner to add exercises.", Toast.LENGTH_LONG).show()
        }
    }
}