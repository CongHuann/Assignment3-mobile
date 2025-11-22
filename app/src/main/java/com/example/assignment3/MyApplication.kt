package com.example.assignment3

import android.app.Application
import com.example.assignment3.database.AppDatabase
import com.example.assignment3.repository.WorkoutRepository

class MyApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { WorkoutRepository(database) }

    override fun onCreate() {
        super.onCreate()
        android.util.Log.e("MyApplication", "🟢 Application started")
    }
}