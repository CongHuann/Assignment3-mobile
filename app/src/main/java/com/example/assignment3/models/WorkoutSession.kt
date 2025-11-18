package com.example.assignment3.models

import java.util.UUID

data class WorkoutSession(
    val id: String = UUID.randomUUID().toString(),
    val dayIndex: Int,
    val dayName: String,
    val workoutType: String,
    val startTime: Long = System.currentTimeMillis(),
    var endTime: Long? = null,
    var totalDuration: Long = 0, // seconds
    val exercises: MutableList<ExerciseSession> = mutableListOf(),
    var isCompleted: Boolean = false,
    var isPaused: Boolean = false
) {
    val totalVolume: Double
        get() = exercises.sumOf { it.totalVolume }

    val completedExercises: Int
        get() = exercises.count { it.isCompleted }

    val totalExercises: Int
        get() = exercises.size

    val completedSets: Int
        get() = exercises.sumOf { it.completedSets }

    val totalSets: Int
        get() = exercises.sumOf { it.exercise.sets }

    val progressPercentage: Int
        get() = if (totalSets > 0) (completedSets * 100 / totalSets) else 0
}

data class ExerciseSession(
    val exercise: Exercise,
    val sets: MutableList<SetData?> = mutableListOf(),
    var currentSet: Int = 0,
    var isCompleted: Boolean = false,
    var isSkipped: Boolean = false,
    var notes: String = ""
) {
    val totalVolume: Double
        get() = sets.filterNotNull().sumOf { it.volume }

    val completedSets: Int
        get() = sets.count { it?.isCompleted == true }

    val restTime: Int
        get() = if (isHeavyExercise()) 120 else 60 // 120s for heavy, 60s for light

    // Compound: sets <= 8 reps
    private fun isHeavyExercise(): Boolean {
        val heavyCompounds = listOf("Bench Press", "Squat", "Deadlift", "Overhead Press", "Barbell Row")
        val targetReps = exercise.reps.split("-").firstOrNull()?.toIntOrNull() ?: 10
        return heavyCompounds.contains(exercise.name) || targetReps <= 8
    }
}

data class SetData(
    val setNumber: Int,
    var weight: Double = 0.0, // kg
    var reps: Int = 0,
    var isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    val volume: Double get() = weight * reps
}