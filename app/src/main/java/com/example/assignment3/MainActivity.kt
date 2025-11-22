package com.example.assignment3

import android.content.Intent
import android.os.Bundle
import android.graphics.Color
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.assignment3.repository.FirebaseRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * MainActivity
 */
class MainActivity : AppCompatActivity() {

    // UI Components
    private lateinit var cardWorkouts: CardView
    private lateinit var cardExercises: CardView
    private lateinit var icDumbbell: ImageView
    private lateinit var icFoodExercises: ImageView
    private lateinit var tvStreak: TextView
    private lateinit var tvWorkTime: TextView

    // Firebase repository
    private lateinit var repository: FirebaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.hide()

        repository = (application as MyApplication).repository

        initViews()
        setupClickListeners()
        setCardsDefault()

        cleanFirestoreData()

        loadStats()
    }

    /**
     * TEMPORARY FUNCTION
     */
    private fun cleanFirestoreData() {
        lifecycleScope.launch {
            try {
                val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()

                // Step 1: Delete workouts
                val workoutsRef = firestore.collection("workouts")
                val workoutsSnapshot = workoutsRef.get().await()

                if (workoutsSnapshot.isEmpty) {
                    // Already clean, just initialize
                    repository.initializeDatabase()
                    return@launch
                }

                workoutsSnapshot.documents.forEach { doc ->
                    doc.reference.delete().await()
                }

                // Step 2: Delete exercises (clean slate)
                val exercisesRef = firestore.collection("exercises")
                val exercisesSnapshot = exercisesRef.get().await()

                exercisesSnapshot.documents.forEach { doc ->
                    doc.reference.delete().await()
                }

                // Step 3: Wait for Firestore to process
                kotlinx.coroutines.delay(2000)

                // Step 4: Re-initialize
                repository.initializeDatabase()

                Toast.makeText(
                    this@MainActivity,
                    "✅ Database cleaned and ready!",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initViews() {
        cardWorkouts = findViewById(R.id.cardWorkouts)
        cardExercises = findViewById(R.id.cardExcercises)
        icDumbbell = findViewById(R.id.icDumbell)
        icFoodExercises = findViewById(R.id.icFoodExcercises)
        tvStreak = findViewById(R.id.tvStreak)
        tvWorkTime = findViewById(R.id.tvWorkTime)
    }

    private fun setupClickListeners() {
        cardWorkouts.setOnClickListener {
            setCardsDefault()
            cardWorkouts.setCardBackgroundColor(
                ContextCompat.getColor(this, R.color.accent_orange)
            )
            icDumbbell.setColorFilter(Color.BLACK)

            val intent = Intent(this, WorkoutPlannerActivity::class.java)
            startActivity(intent)
        }

        cardExercises.setOnClickListener {
            setCardsDefault()
            cardExercises.setCardBackgroundColor(
                ContextCompat.getColor(this, R.color.accent_orange)
            )
            icFoodExercises.setColorFilter(Color.BLACK)

            val intent = Intent(this, ExerciseLibraryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadStats() {
        lifecycleScope.launch {
            try {
                val workouts = repository.getAllWorkouts()
                val completedWorkouts = workouts.count { it.isCompleted }
                val streak = calculateStreak(workouts)

                tvStreak.text = "$streak days"
                tvWorkTime.text = "${completedWorkouts}h"

            } catch (e: Exception) {
                e.printStackTrace()
                tvStreak.text = "0 days"
                tvWorkTime.text = "0h"
            }
        }
    }

    private fun calculateStreak(
        workouts: List<com.example.assignment3.models.FirebaseWorkout>
    ): Int {
        if (workouts.isEmpty()) return 0

        val sortedWorkouts = workouts.sortedBy { it.dayIndex }
        var streak = 0

        val calendar = java.util.Calendar.getInstance()
        val todayDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        val currentDayIndex = if (todayDayOfWeek == 1) 6 else todayDayOfWeek - 2

        var checkDay = currentDayIndex

        for (i in 0..6) {
            val workout = sortedWorkouts.find { it.dayIndex == checkDay }

            if (workout?.isCompleted == true) {
                streak++
            } else {
                break
            }

            checkDay = if (checkDay == 0) 6 else checkDay - 1
        }

        return streak
    }

    override fun onResume() {
        super.onResume()
        setCardsDefault()
        loadStats()
    }

    private fun setCardsDefault() {
        cardWorkouts.setCardBackgroundColor(
            ContextCompat.getColor(this, R.color.secondary_gray)
        )
        cardExercises.setCardBackgroundColor(
            ContextCompat.getColor(this, R.color.secondary_gray)
        )

        icDumbbell.setColorFilter(Color.WHITE)
        icFoodExercises.setColorFilter(Color.WHITE)
    }
}