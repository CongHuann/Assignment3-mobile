package com.example.assignment3.models

import com.google.firebase.firestore.PropertyName

data class FirebaseWorkout(
    @get:PropertyName("dayIndex")
    @set:PropertyName("dayIndex")
    var dayIndex: Int = 0,

    @get:PropertyName("workoutType")
    @set:PropertyName("workoutType")
    var workoutType: String = "",

    @get:PropertyName("isCompleted")
    @set:PropertyName("isCompleted")
    var isCompleted: Boolean = false,

    @get:PropertyName("exerciseIds")
    @set:PropertyName("exerciseIds")
    var exerciseIds: List<Int> = emptyList()
)