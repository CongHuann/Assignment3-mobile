package com.example.assignment3.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.assignment3.data.ExerciseDatabase
import com.example.assignment3.models.ExerciseEntity
import com.example.assignment3.models.WorkoutEntity
import com.example.assignment3.models.WorkoutExerciseEntity
import com.example.assignment3.models.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ExerciseEntity::class,
        WorkoutEntity::class,
        WorkoutExerciseEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "assignment3_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                // Populate database on first create
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database)
                    }
                }
            }
        }

        private suspend fun populateDatabase(database: AppDatabase) {
            val exerciseDao = database.exerciseDao()
            val workoutDao = database.workoutDao()

            // Insert all exercises from ExerciseDatabase
            val exercises = ExerciseDatabase.getAllExercises().map { it.toEntity() }
            exerciseDao.insertAll(exercises)

            // Initialize workouts for 7 days
            for (i in 0..6) {
                workoutDao.insertWorkout(
                    WorkoutEntity(
                        dayIndex = i,
                        workoutType = "",
                        isCompleted = false
                    )
                )
            }

            android.util.Log.e("AppDatabase", "✅ Database populated with ${exercises.size} exercises")
        }
    }
}