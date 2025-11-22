package com.example.assignment3.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.assignment3.database.Converters

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey
    val dayIndex: Int,  // 0-6 (Mon-Sun)
    val workoutType: String = "",  // Push, Pull, Legs, etc.
    val isCompleted: Boolean = false
)

@Entity(tableName = "workout_exercises")
data class WorkoutExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dayIndex: Int,  // Foreign key to WorkoutEntity
    val exerciseId: Int,  // Foreign key to ExerciseEntity
    val orderIndex: Int  // Order in the workout
)