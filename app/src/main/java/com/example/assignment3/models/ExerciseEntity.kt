package com.example.assignment3.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.assignment3.database.Converters

@Entity(tableName = "exercises")
@TypeConverters(Converters::class)
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
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
) {
    // Convert to Exercise model for UI
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
        imageUrl = imageUrl
    )
}

// Extension function to convert Exercise to ExerciseEntity
fun Exercise.toEntity() = ExerciseEntity(
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