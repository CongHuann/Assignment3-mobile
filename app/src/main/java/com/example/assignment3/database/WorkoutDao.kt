package com.example.assignment3.database

import androidx.room.*
import com.example.assignment3.models.WorkoutEntity
import com.example.assignment3.models.WorkoutExerciseEntity

@Dao
interface WorkoutDao {

    // Workout CRUD
    @Query("SELECT * FROM workouts")
    suspend fun getAllWorkouts(): List<WorkoutEntity>

    @Query("SELECT * FROM workouts WHERE dayIndex = :dayIndex")
    suspend fun getWorkoutByDay(dayIndex: Int): WorkoutEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity)

    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)

    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)

    // Workout Exercises CRUD
    @Query("SELECT * FROM workout_exercises WHERE dayIndex = :dayIndex ORDER BY orderIndex ASC")
    suspend fun getExercisesForDay(dayIndex: Int): List<WorkoutExerciseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExerciseEntity)

    @Query("DELETE FROM workout_exercises WHERE dayIndex = :dayIndex AND exerciseId = :exerciseId")
    suspend fun deleteWorkoutExercise(dayIndex: Int, exerciseId: Int)

    @Query("DELETE FROM workout_exercises WHERE dayIndex = :dayIndex")
    suspend fun deleteAllExercisesForDay(dayIndex: Int)
}