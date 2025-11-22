package com.example.assignment3

import android.content.Intent
import android.os.Bundle
import android.graphics.Color
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.assignment3.repository.WorkoutRepository
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var cardWorkouts: CardView
    private lateinit var cardExcercises: CardView
    private lateinit var cardMealPlan: CardView

    private lateinit var icDumbell: ImageView
    private lateinit var icFoodExcercises: ImageView
    private lateinit var icFoodMealPlan: ImageView

    private lateinit var tvStreak: TextView
    private lateinit var tvWorkTime: TextView

    // ✅ ROOM REPOSITORY
    private lateinit var repository: WorkoutRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.hide()

        android.util.Log.e("MainActivity", "🟢 ========== onCreate started ==========")

        // ✅ GET REPOSITORY
        repository = (application as MyApplication).repository

        initViews()
        loadStats()
        setupClickListeners()
        setCardsDefault()
    }

    private fun initViews() {
        // Card views
        cardWorkouts = findViewById(R.id.cardWorkouts)
        cardExcercises = findViewById(R.id.cardExcercises)
        cardMealPlan = findViewById(R.id.cardMealPlan)

        // Icons
        icDumbell = findViewById(R.id.icDumbell)
        icFoodExcercises = findViewById(R.id.icFoodExcercises)
        icFoodMealPlan = findViewById(R.id.icFoodMealPlan)

        // Stats
        tvStreak = findViewById(R.id.tvStreak)
        tvWorkTime = findViewById(R.id.tvWorkTime)
    }

    private fun loadStats() {
        // ✅ LOAD STATS FROM DATABASE
        lifecycleScope.launch {
            try {
                val workouts = repository.getAllWorkouts()
                val completedWorkouts = workouts.count { it.isCompleted }

                // Calculate streak (consecutive completed days)
                val streak = calculateStreak(workouts)

                tvStreak.text = "$streak days"
                tvWorkTime.text = "${completedWorkouts}h"  // Placeholder

                android.util.Log.e("MainActivity", "✅ Stats loaded: Streak=$streak, Completed=$completedWorkouts")

            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "❌ Error loading stats: ${e.message}")
                tvStreak.text = "0 days"
                tvWorkTime.text = "0h"
            }
        }
    }

    private fun calculateStreak(workouts: List<com.example.assignment3.models.WorkoutEntity>): Int {
        // Simple streak calculation: count completed workouts
        return workouts.count { it.isCompleted }
    }

    private fun setupClickListeners() {
        // ✅ WORKOUTS CARD → WorkoutPlannerActivity
        cardWorkouts.setOnClickListener {
            setCardsDefault()
            cardWorkouts.setCardBackgroundColor(ContextCompat.getColor(this, R.color.accent_orange))
            icDumbell.setColorFilter(Color.BLACK)

            val intent = Intent(this, WorkoutPlannerActivity::class.java)
            startActivity(intent)
        }

        // ✅ EXERCISES CARD → ExerciseLibraryActivity (NOT ExercisesActivity)
        cardExcercises.setOnClickListener {
            setCardsDefault()
            cardExcercises.setCardBackgroundColor(ContextCompat.getColor(this, R.color.accent_orange))
            icFoodExcercises.setColorFilter(Color.BLACK)

            // ✅ MỞ EXERCISE LIBRARY (có instructions)
            val intent = Intent(this, ExerciseLibraryActivity::class.java)
            startActivity(intent)
        }

        // ✅ MEAL PLAN CARD → Coming Soon
        cardMealPlan.setOnClickListener {
            setCardsDefault()
            cardMealPlan.setCardBackgroundColor(ContextCompat.getColor(this, R.color.accent_orange))
            icFoodMealPlan.setColorFilter(Color.BLACK)

            Toast.makeText(this, "Meal Plan feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        android.util.Log.e("MainActivity", "🔄 onResume called")

        // Reset cards to default
        setCardsDefault()

        // Reload stats (in case workouts were completed)
        loadStats()
    }

    private fun setCardsDefault() {
        cardWorkouts.setCardBackgroundColor(ContextCompat.getColor(this, R.color.secondary_gray))
        cardExcercises.setCardBackgroundColor(ContextCompat.getColor(this, R.color.secondary_gray))
        cardMealPlan.setCardBackgroundColor(ContextCompat.getColor(this, R.color.secondary_gray))

        icDumbell.setColorFilter(Color.WHITE)
        icFoodExcercises.setColorFilter(Color.WHITE)
        icFoodMealPlan.setColorFilter(Color.WHITE)
    }
}