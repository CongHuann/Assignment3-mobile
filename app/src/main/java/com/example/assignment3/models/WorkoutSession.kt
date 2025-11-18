package com.example.assignment3.models

import java.util.UUID

/**
 * WorkoutSession - Represents a complete workout session
 * Tracks all exercises, timing, and progress for one workout
 */
data class WorkoutSession(
    val id: String = UUID.randomUUID().toString(),
    val dayIndex: Int,                              // 0=Mon, 1=Tue, etc.
    val dayName: String,                            // "Monday", "Tuesday", etc.
    val workoutType: String,                        // "Push", "Pull", "Legs", etc.
    val startTime: Long = System.currentTimeMillis(),
    var endTime: Long? = null,
    var totalDuration: Long = 0,                    // Total workout time in seconds
    val exercises: MutableList<ExerciseSession> = mutableListOf(),
    var isCompleted: Boolean = false,
    var isPaused: Boolean = false
) {
    // Calculate total weight lifted (kg)
    val totalVolume: Double
        get() = exercises.sumOf { it.totalVolume }

    // Count how many exercises are done
    val completedExercises: Int
        get() = exercises.count { it.isCompleted }

    // Total number of exercises
    val totalExercises: Int
        get() = exercises.size

    // Count completed sets across all exercises
    val completedSets: Int
        get() = exercises.sumOf { it.completedSets }

    // Total sets across all exercises
    val totalSets: Int
        get() = exercises.sumOf { it.exercise.sets }

    // Progress as percentage (0-100)
    val progressPercentage: Int
        get() = if (totalSets > 0) (completedSets * 100 / totalSets) else 0
}

/**
 * ExerciseSession - Tracks progress for one exercise in a workout
 * Contains all sets performed for this exercise
 */
data class ExerciseSession(
    val exercise: Exercise,
    val sets: MutableList<SetData?> = mutableListOf(),  // null = locked/not started
    var currentSet: Int = 0,
    var isCompleted: Boolean = false,
    var isSkipped: Boolean = false,
    var notes: String = ""
) {
    // Total weight lifted for this exercise (kg)
    val totalVolume: Double
        get() = sets.filterNotNull().sumOf { it.volume }

    // Count completed sets
    val completedSets: Int
        get() = sets.count { it?.isCompleted == true }

    // Rest time between sets (seconds)
    // Heavy exercises: 120s, Light exercises: 60s
    val restTime: Int
        get() = if (isHeavyExercise()) 120 else 60

    // Check if this is a heavy compound exercise
    private fun isHeavyExercise(): Boolean {
        val heavyCompounds = listOf("Bench Press", "Squat", "Deadlift", "Overhead Press", "Barbell Row")
        val targetReps = exercise.reps.split("-").firstOrNull()?.toIntOrNull() ?: 10
        return heavyCompounds.contains(exercise.name) || targetReps <= 8
    }
}

/**
 * SetData - Represents one set of an exercise
 * Example: 50kg × 10 reps = 500kg volume
 */
data class SetData(
    val setNumber: Int,                             // 1, 2, 3, 4...
    var weight: Double = 0.0,                       // Weight in kg
    var reps: Int = 0,                              // Number of repetitions
    var isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    // Calculate volume: weight × reps
    val volume: Double get() = weight * reps
}