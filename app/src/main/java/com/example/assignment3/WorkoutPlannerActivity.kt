package com.example.assignment3

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.assignment3.models.Exercise
import com.example.assignment3.models.FirebaseWorkout
import com.example.assignment3.repository.FirebaseRepository
import kotlinx.coroutines.launch

class WorkoutPlannerActivity : AppCompatActivity() {

    // Firebase repository for data operations
    private lateinit var repository: FirebaseRepository

    /**
     * Activity Result Launcher for Workout Session
     */
    private val workoutSessionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val data = result.data!!

            // Extract workout completion data
            val isCompleted = data.getBooleanExtra("WORKOUT_COMPLETED", false)
            val dayIndex = data.getIntExtra("DAY_INDEX", -1)
            val totalVolume = data.getDoubleExtra("TOTAL_VOLUME", 0.0)
            val totalDuration = data.getLongExtra("TOTAL_DURATION", 0)
            val completedExercises = data.getIntExtra("COMPLETED_EXERCISES", 0)
            val totalExercises = data.getIntExtra("TOTAL_EXERCISES", 0)

            if (isCompleted && dayIndex >= 0) {
                val workout = workoutData[dayIndex]

                if (workout != null) {
                    // Mark day as completed
                    workoutData[dayIndex] = WorkoutDay(workout.workoutType, true)

                    // ✅ SAVE SYNC với await
                    lifecycleScope.launch {
                        try {
                            repository.updateWorkout(
                                dayIndex = dayIndex,
                                workoutType = workout.workoutType,
                                isCompleted = true
                            )
                            // Update UI sau save thành công
                            updateWeekdaysUI()
                            if (dayIndex == selectedDayIndex) {
                                loadWorkoutForDay(dayIndex)
                            }

                            // Show success message with statistics
                            val minutes = totalDuration / 60
                            val seconds = totalDuration % 60
                            val message = """
                            Workout Completed!
                            
                            Time: ${minutes}m ${seconds}s
                            Volume: ${String.format("%.1f", totalVolume)} kg
                            Exercises: $completedExercises/$totalExercises
                        """.trimIndent()

                            Toast.makeText(this@WorkoutPlannerActivity, message, Toast.LENGTH_LONG).show()

                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this@WorkoutPlannerActivity, "Save failed: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    /**
     * Activity Result Launcher for Adding Exercises
     */
    private val addExerciseLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val data = result.data!!

            // Extract exercise data from arrays
            val ids = data.getIntArrayExtra("EXERCISE_IDS")
            val names = data.getStringArrayExtra("EXERCISE_NAMES")
            val setsArray = data.getIntArrayExtra("EXERCISE_SETS")
            val repsArray = data.getStringArrayExtra("EXERCISE_REPS")
            val musclesArray = data.getStringArrayExtra("EXERCISE_MUSCLES")

            // Validate all data is present
            if (ids == null || names == null || setsArray == null || repsArray == null || musclesArray == null) {
                return@registerForActivityResult
            }

            // Initialize exercise list for day if needed
            if (exercisesMap[selectedDayIndex] == null) {
                exercisesMap[selectedDayIndex] = mutableListOf()
            }

            // Add each selected exercise
            for (i in ids.indices) {
                val newExercise = Exercise(
                    id = ids[i],
                    name = names[i],
                    sets = setsArray[i],
                    reps = repsArray[i],
                    targetMuscle = musclesArray[i]
                )
                exercisesMap[selectedDayIndex]?.add(newExercise)
            }

            //Firebase
            lifecycleScope.launch {
                try {
                    for (i in ids.indices) {
                        repository.addExerciseToDay(selectedDayIndex, ids[i])
                    }

                    // ✅ WAIT FOR FIREBASE TO COMPLETE
                    kotlinx.coroutines.delay(300)

                    // ✅ RELOAD FROM FIREBASE TO VERIFY
                    val exercises = repository.getExercisesForDay(selectedDayIndex)
                    exercisesMap[selectedDayIndex] = exercises.toMutableList()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Refresh UI
            loadExercisesForDay(selectedDayIndex)

            // Show confirmation message
            val count = ids.size
            val message = if (count == 1) "1 exercise added" else "$count exercises added"
            Toast.makeText(this, "$message to ${weekdays[selectedDayIndex]}", Toast.LENGTH_SHORT).show()
        }
    }

    // ==================== UI COMPONENTS ====================

    private lateinit var btnBack: ImageButton
    private lateinit var tvPlannerTitle: TextView
    private lateinit var llWeekdays: LinearLayout
    private lateinit var tvDayTitle: TextView
    private lateinit var tvDayFocus: TextView
    private lateinit var btnAddExercise: Button
    private lateinit var btnStartWorkout: Button
    private lateinit var llExerciseList: LinearLayout

    // ==================== DATA ====================

    // Day names for UI display
    private val weekdays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    // Currently selected day (0=Mon, 6=Sun)
    private var selectedDayIndex = 0

    // Available workout types
    private val workoutTypes = listOf("Push", "Pull", "Legs", "Chest", "Back", "Arm", "Rest")

    // Workout focus descriptions for each type
    private val workoutFocus = mapOf(
        "Push" to "Focus: Chest, Shoulders, Triceps",
        "Pull" to "Focus: Back, Biceps",
        "Legs" to "Focus: Quads, Hamstrings, Glutes",
        "Chest" to "Focus: Pectorals, Front Delts",
        "Back" to "Focus: Lats, Traps, Rhomboids",
        "Arm" to "Focus: Biceps, Triceps, Forearms",
        "Rest" to "Recovery day"
    )

    // In-memory cache of workout data for 7 days
    private val workoutData = mutableMapOf(
        0 to WorkoutDay("", false),
        1 to WorkoutDay("", false),
        2 to WorkoutDay("", false),
        3 to WorkoutDay("", false),
        4 to WorkoutDay("", false),
        5 to WorkoutDay("", false),
        6 to WorkoutDay("", false)
    )

    // In-memory cache of exercises for each day
    // Key: day index (0-6), Value: list of exercises
    private val exercisesMap = mutableMapOf<Int, MutableList<Exercise>>(
        0 to mutableListOf(),
        1 to mutableListOf(),
        2 to mutableListOf(),
        3 to mutableListOf(),
        4 to mutableListOf(),
        5 to mutableListOf(),
        6 to mutableListOf()
    )

    // ==================== LIFECYCLE ====================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)
        supportActionBar?.hide()

        // Initialize Firebase repository
        repository = (application as MyApplication).repository

        // Setup UI components
        initViews()
        setupClickListeners()
        setupWeekdaysSelector()

        // Load workout data from Firebase
        loadWorkoutsFromDatabase()
    }

    // ==================== DATA OPERATIONS ====================

    /**
     * Load workout data from Firebase Firestore
     */

    private fun loadWorkoutsFromDatabase() {
        lifecycleScope.launch {
            try {
                // Load workout metadata (type, completion) for all days
                val workouts = repository.getAllWorkouts()
                Log.d("WorkoutPlanner", "Loaded workouts count: ${workouts.size}, data: $workouts")  // Check ở Logcat
                workouts.forEach { workout ->
                    workoutData[workout.dayIndex] = WorkoutDay(
                        workoutType = workout.workoutType,
                        isCompleted = workout.isCompleted
                    )
                }

                // Load exercises for each day
                for (i in 0..6) {
                    val exercises = repository.getExercisesForDay(i)
                    exercisesMap[i] = exercises.toMutableList()
                }

                // Update UI with loaded data
                updateWeekdaysUI()
                loadWorkoutForDay(0)

            } catch (e: Exception) {
                Log.e("WorkoutPlanner", "Load failed", e)
                e.printStackTrace()

                // On error, use default empty data and update UI
                updateWeekdaysUI()
                loadWorkoutForDay(0)
            }
        }
    }

    /**
     * Save workout metadata to Firebase
     * Saves workout type and completion status
     */
    private fun saveWorkoutToDatabase(dayIndex: Int) {
        lifecycleScope.launch {
            try {
                val workout = workoutData[dayIndex]
                if (workout != null) {
                    repository.updateWorkout(
                        dayIndex = dayIndex,
                        workoutType = workout.workoutType,
                        isCompleted = workout.isCompleted
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ==================== UI SETUP ====================

    /**
     * Initialize all UI components
     */
    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvPlannerTitle = findViewById(R.id.tvPlannerTitle)
        llWeekdays = findViewById(R.id.llweekdays)
        tvDayTitle = findViewById(R.id.tvDayTitle)
        tvDayFocus = findViewById(R.id.tvDayFocus)
        btnAddExercise = findViewById(R.id.btnAddExercise)
        btnStartWorkout = findViewById(R.id.btnStartWorkout)
        llExerciseList = findViewById(R.id.llExerciseList)
    }

    /**
     * Setup click listeners for buttons
     */
    private fun setupClickListeners() {
        // Back button - return to home
        btnBack.setOnClickListener {
            finish()
        }

        // Day title - click to change workout type
        tvDayTitle.setOnClickListener {
            showWorkoutTypeDialog()
        }

        // Add Exercise button - open exercise library
        btnAddExercise.setOnClickListener {
            val intent = Intent(this, ExercisesActivity::class.java)
            intent.putExtra("SELECTION_MODE", true)
            intent.putExtra("DAY_INDEX", selectedDayIndex)
            addExerciseLauncher.launch(intent)
        }

        // Start Workout button
        btnStartWorkout.setOnClickListener {
            val selectedDay = workoutData[selectedDayIndex]

            //Check workout type is set
            if (selectedDay?.workoutType.isNullOrEmpty()) {
                Toast.makeText(this, "Please select workout type first", Toast.LENGTH_SHORT).show()
                showWorkoutTypeDialog()
                return@setOnClickListener
            }

            //Cannot start rest day
            if (selectedDay?.workoutType == "Rest") {
                Toast.makeText(this, "This is a rest day!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Check exercises exist
            val exercises = exercisesMap[selectedDayIndex]
            if (exercises.isNullOrEmpty()) {
                Toast.makeText(this, "Please add exercises first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Qualified -> Start workout session
            val intent = Intent(this, WorkoutSessionActivity::class.java)

            // Pass workout metadata
            intent.putExtra("DAY_INDEX", selectedDayIndex)
            intent.putExtra("DAY_NAME", weekdays[selectedDayIndex])
            intent.putExtra("WORKOUT_TYPE", selectedDay.workoutType)

            // Pass exercise data as arrays
            val ids = exercises.map { it.id }.toIntArray()
            val names = exercises.map { it.name }.toTypedArray()
            val setsArray = exercises.map { it.sets }.toIntArray()
            val repsArray = exercises.map { it.reps }.toTypedArray()
            val musclesArray = exercises.map { it.targetMuscle }.toTypedArray()

            intent.putExtra("EXERCISE_IDS", ids)
            intent.putExtra("EXERCISE_NAMES", names)
            intent.putExtra("EXERCISE_SETS", setsArray)
            intent.putExtra("EXERCISE_REPS", repsArray)
            intent.putExtra("EXERCISE_MUSCLES", musclesArray)
            intent.putExtra("EXERCISE_COUNT", exercises.size)

            workoutSessionLauncher.launch(intent)
        }
    }

    /**
     * Setup weekday selector (7-day bar at top)
     */
    private fun setupWeekdaysSelector() {
        for (i in 0 until llWeekdays.childCount) {
            val dayLayout = llWeekdays.getChildAt(i) as LinearLayout

            // Click: select day
            dayLayout.setOnClickListener {
                selectedDayIndex = i
                updateWeekdaysUI()
                loadWorkoutForDay(i)
            }

            // Hold: edit day
            dayLayout.setOnLongClickListener {
                showEditDayDialog(i)
                true
            }
        }

        updateWeekdaysUI()
    }

    /**
     * Update weekday selector UI based on current state
     */
    private fun updateWeekdaysUI() {
        for (i in 0 until llWeekdays.childCount) {
            val dayLayout = llWeekdays.getChildAt(i) as LinearLayout
            val circleView = dayLayout.getChildAt(0) as View
            val iconView = dayLayout.getChildAt(1) as ImageView
            val textView = dayLayout.getChildAt(2) as TextView

            val workout = workoutData[i]

            when {
                //Day is completed
                workout?.isCompleted == true -> {
                    circleView.background = ContextCompat.getDrawable(this, R.drawable.bg_green)
                    iconView.setImageResource(R.drawable.check)
                    iconView.setColorFilter(Color.WHITE)
                    textView.setTextColor(Color.WHITE)
                }

                //Day is currently selected
                i == selectedDayIndex -> {
                    circleView.background = ContextCompat.getDrawable(this, R.drawable.bg_square_light)
                    when (workout?.workoutType) {
                        "Rest" -> iconView.setImageResource(R.drawable.rest)
                        else -> iconView.setImageResource(R.drawable.dumbbell)
                    }
                    iconView.setColorFilter(Color.WHITE)
                    textView.setTextColor(Color.WHITE)
                }

                //Day is rest day
                workout?.workoutType == "Rest" -> {
                    circleView.background = ContextCompat.getDrawable(this, R.drawable.bg_circle_gray)
                    iconView.setImageResource(R.drawable.rest)
                    iconView.setColorFilter(Color.GRAY)
                    textView.setTextColor(Color.GRAY)
                }

                //Default state
                else -> {
                    circleView.background = ContextCompat.getDrawable(this, R.drawable.bg_circle_gray)
                    iconView.setImageResource(R.drawable.dumbbell)
                    iconView.setColorFilter(Color.GRAY)
                    textView.setTextColor(Color.GRAY)
                }
            }
        }
    }

    /**
     * Load and display workout details for specific day
     */
    private fun loadWorkoutForDay(dayIndex: Int) {
        val workout = workoutData[dayIndex]
        val dayName = weekdays[dayIndex]

        // Update title and focus text
        if (workout?.workoutType.isNullOrEmpty()) {
            tvDayTitle.text = "$dayName - Day"
            tvDayFocus.text = "Tap title to select workout type"
        } else {
            tvDayTitle.text = "$dayName - ${workout?.workoutType} Day"
            tvDayFocus.text = workoutFocus[workout?.workoutType] ?: ""
        }

        loadExercisesForDay(dayIndex)
    }

    /**
     * Load and display exercise list for specific day
     */
    private fun loadExercisesForDay(dayIndex: Int) {
        llExerciseList.removeAllViews()

        val exercises = exercisesMap[dayIndex] ?: mutableListOf()

        for ((index, exercise) in exercises.withIndex()) {
            // Inflate exercise item layout
            val exerciseView = layoutInflater.inflate(R.layout.item_exercise, llExerciseList, false)

            // Setup exercise details
            val tvExerciseName = exerciseView.findViewById<TextView>(R.id.tvExerciseName)
            val tvExerciseDetails = exerciseView.findViewById<TextView>(R.id.tvExerciseDetails)
            val btnDelete = exerciseView.findViewById<ImageButton>(R.id.btnDelete)

            tvExerciseName.text = exercise.name
            tvExerciseDetails.text = "${exercise.sets} × ${exercise.reps} • ${exercise.targetMuscle}"

            // Delete button handler
            btnDelete.setOnClickListener {
                showDeleteConfirmDialog(exercise, dayIndex, index)
            }

            llExerciseList.addView(exerciseView)
        }
    }

    // ==================== DIALOGS ====================

    /**
     * Show edit options for a day (long-click menu)
     */
    private fun showEditDayDialog(dayIndex: Int) {
        val workout = workoutData[dayIndex] ?: return
        val dayName = weekdays[dayIndex]

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit $dayName")

        val options = arrayOf(
            "Change Workout Type",
            if (workout.isCompleted) "Mark as Not Completed" else "Mark as Completed"
        )

        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> showWorkoutTypeDialogForDay(dayIndex)
                1 -> toggleDayCompletion(dayIndex)
            }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    /**
     * Show workout type selection dialog
     */
    private fun showWorkoutTypeDialogForDay(dayIndex: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Workout Type for ${weekdays[dayIndex]}")

        builder.setItems(workoutTypes.toTypedArray()) { dialog, which ->
            val selectedType = workoutTypes[which]
            val currentCompleted = workoutData[dayIndex]?.isCompleted ?: false

            // Update workout type (preserve completion status)
            workoutData[dayIndex] = WorkoutDay(selectedType, currentCompleted)

            //Firebase
            saveWorkoutToDatabase(dayIndex)

            // Update UI
            if (dayIndex == selectedDayIndex) {
                loadWorkoutForDay(dayIndex)
            }
            updateWeekdaysUI()
        }

        builder.show()
    }

    /**
     * Toggle day completion status
     */
    private fun toggleDayCompletion(dayIndex: Int) {
        val workout = workoutData[dayIndex] ?: return

        // Toggle completion status
        val newCompleted = !workout.isCompleted
        workoutData[dayIndex] = WorkoutDay(workout.workoutType, newCompleted)

        //Firebase
        saveWorkoutToDatabase(dayIndex)

        // Update UI
        updateWeekdaysUI()

        // Show confirmation
        val status = if (newCompleted) "completed ✅" else "not completed"
        Toast.makeText(this, "${weekdays[dayIndex]} marked as $status", Toast.LENGTH_SHORT).show()
    }

    /**
     * Show workout type dialog for currently selected day
     */
    private fun showWorkoutTypeDialog() {
        showWorkoutTypeDialogForDay(selectedDayIndex)
    }

    /**
     * Show confirmation dialog before deleting exercise
     */
    private fun showDeleteConfirmDialog(exercise: Exercise, dayIndex: Int, exerciseIndex: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Exercise")
        builder.setMessage("Are you sure you want to delete ${exercise.name}?")

        builder.setPositiveButton("Delete") { dialog, _ ->
            // Remove from memory
            exercisesMap[dayIndex]?.removeAt(exerciseIndex)

            // Delete from Firebase
            lifecycleScope.launch {
                repository.removeExerciseFromDay(dayIndex, exercise.id)
            }

            // Update UI
            loadExercisesForDay(dayIndex)
            Toast.makeText(this, "${exercise.name} deleted", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    // ==================== DATA MODELS ====================

    /**
     * Data class representing a workout day
     */
    data class WorkoutDay(
        val workoutType: String,
        val isCompleted: Boolean = false
    )
}