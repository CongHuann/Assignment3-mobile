package com.example.assignment3

import android.app.Application
import androidx.lifecycle.lifecycleScope
import com.example.assignment3.repository.FirebaseRepository
import kotlinx.coroutines.launch

class MyApplication : Application() {

    //FIREBASE REPOSITORY
    val repository by lazy { FirebaseRepository() }

    override fun onCreate() {
        super.onCreate()

        //Initialize Firebase exercises (first time only)
        // Will be called from MainActivity
    }
}