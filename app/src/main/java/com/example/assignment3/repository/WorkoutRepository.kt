package com.example.assignment3.repository

import com.example.assignment3.database.AppDatabase
import com.example.assignment3.models.*

class WorkoutRepository(private val database: AppDatabase) {

    private val exerciseDao = database.exerciseDao()
    private val workoutDao = database.workoutDao()

    // ==================== EXERCISES ====================
    suspend fun getAllExercises(): List<Exercise> {
        return exerciseDao.getAllExercises().map { it.toExercise() }
    }

    suspend fun getExercisesByMuscle(muscle: String): List<Exercise> {
        return exerciseDao.getExercisesByMuscle(muscle).map { it.toExercise() }
    }

    suspend fun getExerciseById(id: Int): Exercise? {
        return exerciseDao.getExerciseById(id)?.toExercise()
    }

    // ==================== WORKOUTS ====================
    suspend fun getAllWorkouts(): List<WorkoutEntity> {
        return workoutDao.getAllWorkouts()
    }

    suspend fun getWorkoutByDay(dayIndex: Int): WorkoutEntity? {
        return workoutDao.getWorkoutByDay(dayIndex)
    }

    suspend fun updateWorkout(workout: WorkoutEntity) {
        workoutDao.updateWorkout(workout)
    }

    // ==================== WORKOUT EXERCISES ====================
    suspend fun getExercisesForDay(dayIndex: Int): List<Exercise> {
        val workoutExercises = workoutDao.getExercisesForDay(dayIndex)
        return workoutExercises.mapNotNull { we ->
            exerciseDao.getExerciseById(we.exerciseId)?.toExercise()
        }
    }

    suspend fun addExerciseToDay(dayIndex: Int, exerciseId: Int) {
        val existingExercises = workoutDao.getExercisesForDay(dayIndex)
        val nextOrder = existingExercises.size

        workoutDao.insertWorkoutExercise(
            WorkoutExerciseEntity(
                dayIndex = dayIndex,
                exerciseId = exerciseId,
                orderIndex = nextOrder
            )
        )
    }

    suspend fun removeExerciseFromDay(dayIndex: Int, exerciseId: Int) {
        workoutDao.deleteWorkoutExercise(dayIndex, exerciseId)
    }
}