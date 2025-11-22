package com.example.assignment3.repository

import com.example.assignment3.data.ExerciseDatabase
import com.example.assignment3.models.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * FirebaseRepository
 */
class FirebaseRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val exercisesCollection = firestore.collection("exercises")
    private val workoutsCollection = firestore.collection("workouts")

    // ==================== INITIALIZATION ====================

    suspend fun initializeDatabase() = withContext(Dispatchers.IO) {
        try {
            initializeExercises()
            initializeWorkouts()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    private suspend fun initializeExercises() {
        try {
            val snapshot = exercisesCollection.get().await()
            val currentCount = snapshot.size()

            if (currentCount >= 28) return

            snapshot.documents.forEach { doc ->
                doc.reference.delete().await()
            }

            val exercises = ExerciseDatabase.getAllExercises()

            if (exercises.size != 28) {
                throw Exception("ExerciseDatabase should have 28 exercises")
            }

            exercises.forEach { exercise ->
                val firebaseExercise = FirebaseExercise(
                    id = exercise.id,
                    name = exercise.name,
                    sets = exercise.sets,
                    reps = exercise.reps,
                    targetMuscle = exercise.targetMuscle,
                    description = exercise.description,
                    instructions = exercise.instructions,
                    tips = exercise.tips,
                    difficulty = exercise.difficulty,
                    equipment = exercise.equipment,
                    secondaryMuscles = exercise.secondaryMuscles,
                    imageUrl = exercise.imageUrl
                )

                exercisesCollection
                    .document(exercise.id.toString())
                    .set(firebaseExercise)
                    .await()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    private suspend fun initializeWorkouts() {
        try {
            val snapshot = workoutsCollection.get().await()
            if (snapshot.size() >= 7) return

            snapshot.documents.forEach { doc ->
                doc.reference.delete().await()
            }

            for (i in 0..6) {
                val workout = FirebaseWorkout(
                    dayIndex = i,
                    workoutType = "",
                    isCompleted = false,
                    exerciseIds = emptyList()
                )

                workoutsCollection
                    .document("day_$i")
                    .set(workout)
                    .await()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    // ==================== EXERCISES ====================

    suspend fun getAllExercises(): List<Exercise> = withContext(Dispatchers.IO) {
        try {
            val snapshot = exercisesCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(FirebaseExercise::class.java)?.toExercise()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getExerciseById(id: Int): Exercise? = withContext(Dispatchers.IO) {
        try {
            val doc = exercisesCollection.document(id.toString()).get().await()
            doc.toObject(FirebaseExercise::class.java)?.toExercise()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getExercisesByMuscle(muscle: String): List<Exercise> = withContext(Dispatchers.IO) {
        try {
            if (muscle == "All") {
                return@withContext getAllExercises()
            }

            val snapshot = exercisesCollection
                .whereEqualTo("targetMuscle", muscle)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(FirebaseExercise::class.java)?.toExercise()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // ==================== WORKOUTS ====================

    suspend fun getAllWorkouts(): List<FirebaseWorkout> = withContext(Dispatchers.IO) {
        try {
            val snapshot = workoutsCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(FirebaseWorkout::class.java)
            }.sortedBy { it.dayIndex }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getWorkoutByDay(dayIndex: Int): FirebaseWorkout? = withContext(Dispatchers.IO) {
        try {
            val doc = workoutsCollection.document("day_$dayIndex").get().await()
            doc.toObject(FirebaseWorkout::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Update workout metadata (type and completion status)
     */
    suspend fun updateWorkout(
        dayIndex: Int,
        workoutType: String,
        isCompleted: Boolean
    ) = withContext(Dispatchers.IO) {
        try {
            val updates = hashMapOf<String, Any>(
                "workoutType" to workoutType,
                "isCompleted" to isCompleted
            )

            workoutsCollection
                .document("day_$dayIndex")
                .update(updates)
                .await()

        } catch (e: Exception) {
            e.printStackTrace()

            try {
                val workout = FirebaseWorkout(
                    dayIndex = dayIndex,
                    workoutType = workoutType,
                    isCompleted = isCompleted,
                    exerciseIds = emptyList()
                )

                workoutsCollection
                    .document("day_$dayIndex")
                    .set(workout)
                    .await()

            } catch (e2: Exception) {
                e2.printStackTrace()
                throw e2
            }
        }
    }

    suspend fun getExercisesForDay(dayIndex: Int): List<Exercise> = withContext(Dispatchers.IO) {
        try {
            val workout = getWorkoutByDay(dayIndex)
            val exerciseIds = workout?.exerciseIds ?: emptyList()

            if (exerciseIds.isEmpty()) {
                return@withContext emptyList()
            }

            exerciseIds.mapNotNull { id ->
                getExerciseById(id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Add exercise to specific day
     */
    suspend fun addExerciseToDay(dayIndex: Int, exerciseId: Int) = withContext(Dispatchers.IO) {
        try {
            val existingWorkout = getWorkoutByDay(dayIndex) ?: FirebaseWorkout(
                dayIndex = dayIndex,
                workoutType = "",
                isCompleted = false,
                exerciseIds = emptyList()
            )

            val updatedIds = existingWorkout.exerciseIds.toMutableList()
            if (!updatedIds.contains(exerciseId)) {
                updatedIds.add(exerciseId)
            }

            // ✅ PRESERVE ALL FIELDS
            val updated = existingWorkout.copy(
                exerciseIds = updatedIds
            )

            workoutsCollection
                .document("day_$dayIndex")
                .set(updated)
                .await()

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    /**
     * Remove exercise from specific day
     *
     * ✅ PRESERVES workoutType and isCompleted
     */
    suspend fun removeExerciseFromDay(dayIndex: Int, exerciseId: Int) = withContext(Dispatchers.IO) {
        try {
            val existingWorkout = getWorkoutByDay(dayIndex) ?: return@withContext

            val updatedIds = existingWorkout.exerciseIds.toMutableList()
            updatedIds.remove(exerciseId)

            // ✅ PRESERVE ALL FIELDS
            val updated = existingWorkout.copy(
                exerciseIds = updatedIds
            )

            workoutsCollection
                .document("day_$dayIndex")
                .set(updated)
                .await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}