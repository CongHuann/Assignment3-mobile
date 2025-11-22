package com.example.assignment3.models

data class FirebaseExercise(
    val id: Int = 0,
    val name: String = "",
    val sets: Int = 0,
    val reps: String = "",
    val targetMuscle: String = "",
    val description: String = "",
    val instructions: List<String> = listOf(),
    val tips: List<String> = listOf(),
    val difficulty: String = "Intermediate",
    val equipment: String = "",
    val secondaryMuscles: List<String> = listOf(),
    val imageUrl: String? = ""
) {
    // Convert to Exercise model
    fun toExercise() = Exercise(
        id = id,
        name = name,
        sets = sets,
        reps = reps,
        targetMuscle = targetMuscle,
        description = description,
        instructions = instructions,
        tips = tips,
        difficulty = difficulty,
        equipment = equipment,
        secondaryMuscles = secondaryMuscles,
        imageUrl = imageUrl ?: ""
    )
}

// Extension function
fun Exercise.toFirebase() = FirebaseExercise(
    id = id,
    name = name,
    sets = sets,
    reps = reps,
    targetMuscle = targetMuscle,
    description = description,
    instructions = instructions,
    tips = tips,
    difficulty = difficulty,
    equipment = equipment,
    secondaryMuscles = secondaryMuscles,
    imageUrl = imageUrl
)