package com.example.assignment3

import android.os.Bundle
import android.graphics.Color
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var cardWorkouts: CardView
    private lateinit var cardExcercises: CardView
    private lateinit var cardMealPlan: CardView

    private lateinit var icDumbell: ImageView
    private lateinit var icFoodExcercises: ImageView
    private lateinit var icFoodMealPlan: ImageView

    private lateinit var tvStreak: TextView
    private lateinit var tvWorkTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //card views
        cardWorkouts = findViewById(R.id.cardWorkouts)
        cardExcercises = findViewById(R.id.cardExcercises)
        cardMealPlan = findViewById(R.id.cardMealPlan)

        icDumbell = findViewById(R.id.icDumbell)
        icFoodExcercises = findViewById(R.id.icFoodExcercises)
        icFoodMealPlan = findViewById(R.id.icFoodMealPlan)

        tvStreak = findViewById(R.id.tvStreak)
        tvWorkTime = findViewById(R.id.tvWorkTime)

        // Stats demo-------------------
        tvStreak.text = "4 days"
        tvWorkTime.text = "5h"

        setCardsDefault()

        cardWorkouts.setOnClickListener {
            setCardsDefault()
            // Highlight selected
            cardWorkouts.setCardBackgroundColor(ContextCompat.getColor(this, R.color.accent_orange))
            icDumbell.setColorFilter(Color.BLACK)
            // TODO: Chuyển qua màn hình Workouts
            Toast.makeText(this, "Go to Workouts", Toast.LENGTH_SHORT).show()
        }

        cardExcercises.setOnClickListener {
            setCardsDefault()
            cardExcercises.setCardBackgroundColor(ContextCompat.getColor(this, R.color.accent_orange))
            icFoodExcercises.setColorFilter(Color.BLACK)
            // TODO: Chuyển qua màn hình Excercises
            Toast.makeText(this, "Go to Exercises", Toast.LENGTH_SHORT).show()
        }

        cardMealPlan.setOnClickListener {
            setCardsDefault()
            cardMealPlan.setCardBackgroundColor(ContextCompat.getColor(this, R.color.accent_orange))
            icFoodMealPlan.setColorFilter(Color.BLACK)
            // TODO: Chuyển qua màn hình Meal Plan
            Toast.makeText(this, "Go to Meal Plan", Toast.LENGTH_SHORT).show()
        }
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