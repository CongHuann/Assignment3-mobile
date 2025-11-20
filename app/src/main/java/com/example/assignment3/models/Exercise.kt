package com.example.assignment3.models

data class Exercise(
    val id: Int,
    val name: String,
    val sets: Int,
    val reps: String,
    val targetMuscle: String,

    val description: String = "",
    val instructions: List<String> = listOf(),
    val tips: List<String> = listOf(),
    val difficulty: String = "Intermediate",
    val equipment: String = "Barbell",
    val secondaryMuscles: List<String> = listOf(),
    val imageUrl: String = ""
)