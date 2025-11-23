package com.example.assignment3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.assignment3.models.Exercise
import com.example.assignment3.repository.FirebaseRepository
import kotlinx.coroutines.launch

class ExerciseDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnEdit: ImageButton
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

    // ✅ MOVE THIS HERE - Before onCreate()
    private val editExerciseLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            loadExercise() // Reload exercise data
            Toast.makeText(this, "Exercise updated", Toast.LENGTH_SHORT).show()
        }
    }

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
        btnEdit = findViewById(R.id.btnEdit)
        ivExerciseImage = findViewById(R.id.ivExerciseImage)
        tvExerciseName = findViewById(R.id.tvExerciseName)
        tvDescription = findViewById(R.id.tvDescription)
        tvDifficulty = findViewById(R.id.tvDifficulty)
        tvEquipment = findViewById(R.id.tvEquipment)
        tvTargetMuscle = findViewById(R.id.tvTargetMuscle)
        tvSecondaryMuscles = findViewById(R.id.tvSecondaryMuscles)
        tvInstructions = findViewById(R.id.tvInstructions)
        tvTips = findViewById(R.id.tvTips)
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

    private fun loadExerciseImage(exercise: Exercise) {
        val drawableId = if (exercise.imageUrl.isNotEmpty()) {
            val resId = resources.getIdentifier(
                exercise.imageUrl,
                "drawable",
                packageName
            )

            if (resId != 0) {
                resId
            } else {
                R.drawable.loader
            }
        } else {
            R.drawable.loader
        }

        ivExerciseImage.setImageResource(drawableId)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnEdit.setOnClickListener {
            exercise?.let {
                val intent = Intent(this, ExerciseFormActivity::class.java)
                intent.putExtra("EXERCISE_ID", it.id)
                editExerciseLauncher.launch(intent)
            }
        }
    }
}