package com.example.assignment3

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.assignment3.models.Exercise
import com.example.assignment3.models.WorkoutEntity
import com.example.assignment3.repository.WorkoutRepository
import kotlinx.coroutines.launch

class WorkoutPlannerActivity : AppCompatActivity() {


    private lateinit var repository: WorkoutRepository

    //  ActivityResultLauncher for Workout Session
    private val workoutSessionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        android.util.Log.e("WorkoutPlanner", "🔵 ========== Workout Session Result ==========")
        android.util.Log.e("WorkoutPlanner", "resultCode: ${result.resultCode} (expected: $RESULT_OK = $RESULT_OK)")

        if (result.resultCode == RESULT_OK && result.data != null) {
            val data = result.data!!

            val isCompleted = data.getBooleanExtra("WORKOUT_COMPLETED", false)
            val dayIndex = data.getIntExtra("DAY_INDEX", -1)
            val totalVolume = data.getDoubleExtra("TOTAL_VOLUME", 0.0)
            val totalDuration = data.getLongExtra("TOTAL_DURATION", 0)
            val completedExercises = data.getIntExtra("COMPLETED_EXERCISES", 0)
            val totalExercises = data.getIntExtra("TOTAL_EXERCISES", 0)

            android.util.Log.e("WorkoutPlanner", "  isCompleted: $isCompleted")
            android.util.Log.e("WorkoutPlanner", "  dayIndex: $dayIndex")
            android.util.Log.e("WorkoutPlanner", "  totalVolume: $totalVolume kg")
            android.util.Log.e("WorkoutPlanner", "  totalDuration: ${totalDuration}s")
            android.util.Log.e("WorkoutPlanner", "  completedExercises: $completedExercises/$totalExercises")

            if (isCompleted && dayIndex >= 0) {
                android.util.Log.e("WorkoutPlanner", "✅ Marking day $dayIndex as completed")

                val workout = workoutData[dayIndex]
                android.util.Log.e("WorkoutPlanner", "  Before: $workout")

                if (workout != null) {
                    //  MARK AS COMPLETED
                    workoutData[dayIndex] = WorkoutDay(workout.workoutType, true)

                    //  SAVE TO DATABASE
                    saveWorkoutToDatabase(dayIndex)

                    android.util.Log.e("WorkoutPlanner", "  After: ${workoutData[dayIndex]}")

                    // UPDATE UI
                    updateWeekdaysUI()

                    if (dayIndex == selectedDayIndex) {
                        loadWorkoutForDay(dayIndex)
                    }

                    // SHOW SUCCESS MESSAGE
                    val minutes = totalDuration / 60
                    val seconds = totalDuration % 60
                    val message = """
                        Workout Completed!
                        
                        Time: ${minutes}m ${seconds}s
                        Volume: ${String.format("%.1f", totalVolume)} kg
                        Exercises: $completedExercises/$totalExercises
                    """.trimIndent()

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                } else {
                    android.util.Log.e("WorkoutPlanner", "❌ workout is NULL!")
                }
            } else {
                android.util.Log.e("WorkoutPlanner", "❌ Conditions not met (isCompleted=$isCompleted, dayIndex=$dayIndex)")
            }
        } else {
            android.util.Log.e("WorkoutPlanner", "❌ Result not OK or data is null")
        }

        android.util.Log.e("WorkoutPlanner", "========================================")
    }

    // ActivityResultLauncher for Add Exercise
    private val addExerciseLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        android.util.Log.e("WorkoutPlanner", "🔵 ========== Add Exercise Result ==========")

        if (result.resultCode == RESULT_OK && result.data != null) {
            val data = result.data!!

            val ids = data.getIntArrayExtra("EXERCISE_IDS")
            val names = data.getStringArrayExtra("EXERCISE_NAMES")
            val setsArray = data.getIntArrayExtra("EXERCISE_SETS")
            val repsArray = data.getStringArrayExtra("EXERCISE_REPS")
            val musclesArray = data.getStringArrayExtra("EXERCISE_MUSCLES")

            if (ids == null || names == null || setsArray == null || repsArray == null || musclesArray == null) {
                android.util.Log.e("WorkoutPlanner", "❌ One or more arrays is NULL!")
                return@registerForActivityResult
            }

            android.util.Log.e("WorkoutPlanner", "Received ${ids.size} exercises")

            if (exercisesMap[selectedDayIndex] == null) {
                exercisesMap[selectedDayIndex] = mutableListOf()
            }

            for (i in ids.indices) {
                val newExercise = Exercise(
                    id = ids[i],
                    name = names[i],
                    sets = setsArray[i],
                    reps = repsArray[i],
                    targetMuscle = musclesArray[i]
                )

                android.util.Log.e("WorkoutPlanner", "  Adding: ${newExercise.name}")
                exercisesMap[selectedDayIndex]?.add(newExercise)
            }

            //  SAVE TO DATABASE
            lifecycleScope.launch {
                try {
                    for (i in ids.indices) {
                        repository.addExerciseToDay(selectedDayIndex, ids[i])
                    }
                    android.util.Log.e("WorkoutPlanner", "✅ Saved ${ids.size} exercises to DB")
                } catch (e: Exception) {
                    android.util.Log.e("WorkoutPlanner", "❌ Error saving exercises: ${e.message}")
                }
            }

            loadExercisesForDay(selectedDayIndex)

            val count = ids.size
            val message = if (count == 1) "1 exercise added" else "$count exercises added"
            Toast.makeText(this, "$message to ${weekdays[selectedDayIndex]}", Toast.LENGTH_SHORT).show()
        } else {
            android.util.Log.e("WorkoutPlanner", "❌ Result not OK or data is null")
        }

        android.util.Log.e("WorkoutPlanner", "========================================")
    }

    // Views
    private lateinit var btnBack: ImageButton
    private lateinit var tvPlannerTitle: TextView
    private lateinit var llWeekdays: LinearLayout
    private lateinit var tvDayTitle: TextView
    private lateinit var tvDayFocus: TextView
    private lateinit var btnAddExercise: Button
    private lateinit var btnStartWorkout: Button
    private lateinit var llExerciseList: LinearLayout

    private val weekdays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private var selectedDayIndex = 0

    private val workoutTypes = listOf("Push", "Pull", "Legs", "Chest", "Back", "Arm", "Rest")

    private val workoutFocus = mapOf(
        "Push" to "Focus: Chest, Shoulders, Triceps",
        "Pull" to "Focus: Back, Biceps",
        "Legs" to "Focus: Quads, Hamstrings, Glutes",
        "Chest" to "Focus: Pectorals, Front Delts",
        "Back" to "Focus: Lats, Traps, Rhomboids",
        "Arm" to "Focus: Biceps, Triceps, Forearms",
        "Rest" to "Recovery day"
    )

    // DATA (in-memory cache)
    private val workoutData = mutableMapOf(
        0 to WorkoutDay("", false),
        1 to WorkoutDay("", false),
        2 to WorkoutDay("", false),
        3 to WorkoutDay("", false),
        4 to WorkoutDay("", false),
        5 to WorkoutDay("", false),
        6 to WorkoutDay("", false)
    )

    private val exercisesMap = mutableMapOf<Int, MutableList<Exercise>>(
        0 to mutableListOf(),
        1 to mutableListOf(),
        2 to mutableListOf(),
        3 to mutableListOf(),
        4 to mutableListOf(),
        5 to mutableListOf(),
        6 to mutableListOf()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)
        supportActionBar?.hide()

        android.util.Log.e("WorkoutPlanner", "🟢 ========== onCreate started ==========")

        //  GET REPOSITORY
        repository = (application as MyApplication).repository

        initViews()
        setupClickListeners()
        setupWeekdaysSelector()

        //  LOAD DATA FROM DATABASE
        loadWorkoutsFromDatabase()
    }

    private fun loadWorkoutsFromDatabase() {
        lifecycleScope.launch {
            try {

                // Load workouts
                val workouts = repository.getAllWorkouts()
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


                updateWeekdaysUI()
                loadWorkoutForDay(0)

            } catch (e: Exception) {
                android.util.Log.e("WorkoutPlanner", "❌ Error loading: ${e.message}")
                e.printStackTrace()

                // Fallback: use default empty data
                updateWeekdaysUI()
                loadWorkoutForDay(0)
            }
        }
    }

    private fun saveWorkoutToDatabase(dayIndex: Int) {
        lifecycleScope.launch {
            try {
                val workout = workoutData[dayIndex]
                if (workout != null) {
                    repository.updateWorkout(
                        WorkoutEntity(
                            dayIndex = dayIndex,
                            workoutType = workout.workoutType,
                            isCompleted = workout.isCompleted
                        )
                    )
                }
            } catch (e: Exception) {
            }
        }
    }

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

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        tvDayTitle.setOnClickListener {
            showWorkoutTypeDialog()
        }

        btnAddExercise.setOnClickListener {

            val intent = Intent(this, ExercisesActivity::class.java)
            intent.putExtra("SELECTION_MODE", true)
            intent.putExtra("DAY_INDEX", selectedDayIndex)  // PASS DAY_INDEX
            addExerciseLauncher.launch(intent)
        }

        btnStartWorkout.setOnClickListener {

            val selectedDay = workoutData[selectedDayIndex]

            if (selectedDay?.workoutType.isNullOrEmpty()) {
                android.util.Log.e("WorkoutPlanner", "❌ No workout type selected")
                Toast.makeText(this, "Please select workout type first", Toast.LENGTH_SHORT).show()
                showWorkoutTypeDialog()
                return@setOnClickListener
            }

            if (selectedDay?.workoutType == "Rest") {
                android.util.Log.e("WorkoutPlanner", "❌ Rest day")
                Toast.makeText(this, "This is a rest day!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val exercises = exercisesMap[selectedDayIndex]
            if (exercises.isNullOrEmpty()) {
                android.util.Log.e("WorkoutPlanner", "❌ No exercises")
                Toast.makeText(this, "Please add exercises first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            android.util.Log.e("WorkoutPlanner", "✅ All checks passed, launching WorkoutSessionActivity")

            val intent = Intent(this, WorkoutSessionActivity::class.java)
            intent.putExtra("DAY_INDEX", selectedDayIndex)
            intent.putExtra("DAY_NAME", weekdays[selectedDayIndex])
            intent.putExtra("WORKOUT_TYPE", selectedDay.workoutType)

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

    private fun setupWeekdaysSelector() {
        for (i in 0 until llWeekdays.childCount) {
            val dayLayout = llWeekdays.getChildAt(i) as LinearLayout

            dayLayout.setOnClickListener {
                selectedDayIndex = i
                updateWeekdaysUI()
                loadWorkoutForDay(i)
            }

            dayLayout.setOnLongClickListener {
                showEditDayDialog(i)
                true
            }
        }
        updateWeekdaysUI()
    }

    private fun updateWeekdaysUI() {

        for (i in 0 until llWeekdays.childCount) {
            val dayLayout = llWeekdays.getChildAt(i) as LinearLayout
            val circleView = dayLayout.getChildAt(0) as View
            val iconView = dayLayout.getChildAt(1) as ImageView
            val textView = dayLayout.getChildAt(2) as TextView

            val workout = workoutData[i]

            when {
                workout?.isCompleted == true -> {
                    circleView.background = ContextCompat.getDrawable(this, R.drawable.bg_green)
                    iconView.setImageResource(R.drawable.check)
                    iconView.setColorFilter(Color.WHITE)
                    textView.setTextColor(Color.WHITE)
                }

                i == selectedDayIndex -> {
                    circleView.background = ContextCompat.getDrawable(this, R.drawable.bg_square_light)
                    when (workout?.workoutType) {
                        "Rest" -> iconView.setImageResource(R.drawable.rest)
                        else -> iconView.setImageResource(R.drawable.dumbbell)
                    }
                    iconView.setColorFilter(Color.WHITE)
                    textView.setTextColor(Color.WHITE)
                }

                workout?.workoutType == "Rest" -> {
                    circleView.background = ContextCompat.getDrawable(this, R.drawable.bg_circle_gray)
                    iconView.setImageResource(R.drawable.rest)
                    iconView.setColorFilter(Color.GRAY)
                    textView.setTextColor(Color.GRAY)
                }

                else -> {
                    circleView.background = ContextCompat.getDrawable(this, R.drawable.bg_circle_gray)
                    iconView.setImageResource(R.drawable.dumbbell)
                    iconView.setColorFilter(Color.GRAY)
                    textView.setTextColor(Color.GRAY)
                }
            }
        }
    }

    private fun loadWorkoutForDay(dayIndex: Int) {
        val workout = workoutData[dayIndex]
        val dayName = weekdays[dayIndex]

        if (workout?.workoutType.isNullOrEmpty()) {
            tvDayTitle.text = "$dayName - Day"
            tvDayFocus.text = "Tap title to select workout type"
        } else {
            tvDayTitle.text = "$dayName - ${workout?.workoutType} Day"
            tvDayFocus.text = workoutFocus[workout?.workoutType] ?: ""
        }

        loadExercisesForDay(dayIndex)
    }

    private fun loadExercisesForDay(dayIndex: Int) {
        llExerciseList.removeAllViews()

        val exercises = exercisesMap[dayIndex] ?: mutableListOf()

        for ((index, exercise) in exercises.withIndex()) {
            val exerciseView = layoutInflater.inflate(R.layout.item_exercise, llExerciseList, false)

            val tvExerciseName = exerciseView.findViewById<TextView>(R.id.tvExerciseName)
            val tvExerciseDetails = exerciseView.findViewById<TextView>(R.id.tvExerciseDetails)
            val btnDelete = exerciseView.findViewById<ImageButton>(R.id.btnDelete)

            tvExerciseName.text = exercise.name
            tvExerciseDetails.text = "${exercise.sets} × ${exercise.reps} • ${exercise.targetMuscle}"

            btnDelete.setOnClickListener {
                showDeleteConfirmDialog(exercise, dayIndex, index)
            }

            llExerciseList.addView(exerciseView)
        }
    }

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

    private fun showWorkoutTypeDialogForDay(dayIndex: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Workout Type for ${weekdays[dayIndex]}")

        builder.setItems(workoutTypes.toTypedArray()) { dialog, which ->
            val selectedType = workoutTypes[which]
            val currentCompleted = workoutData[dayIndex]?.isCompleted ?: false

            workoutData[dayIndex] = WorkoutDay(selectedType, currentCompleted)

            // ✅ SAVE TO DATABASE
            saveWorkoutToDatabase(dayIndex)

            if (dayIndex == selectedDayIndex) {
                loadWorkoutForDay(dayIndex)
            }

            updateWeekdaysUI()

        }

        builder.show()
    }

    private fun toggleDayCompletion(dayIndex: Int) {
        val workout = workoutData[dayIndex] ?: return

        val newCompleted = !workout.isCompleted
        workoutData[dayIndex] = WorkoutDay(workout.workoutType, newCompleted)

        // ✅ SAVE TO DATABASE
        saveWorkoutToDatabase(dayIndex)

        updateWeekdaysUI()

        val status = if (newCompleted) "completed ✅" else "not completed"
        Toast.makeText(this, "${weekdays[dayIndex]} marked as $status", Toast.LENGTH_SHORT).show()
    }

    private fun showWorkoutTypeDialog() {
        showWorkoutTypeDialogForDay(selectedDayIndex)
    }

    private fun showDeleteConfirmDialog(exercise: Exercise, dayIndex: Int, exerciseIndex: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Exercise")
        builder.setMessage("Are you sure you want to delete ${exercise.name}?")

        builder.setPositiveButton("Delete") { dialog, _ ->
            exercisesMap[dayIndex]?.removeAt(exerciseIndex)

            // ✅ DELETE FROM DATABASE
            lifecycleScope.launch {
                repository.removeExerciseFromDay(dayIndex, exercise.id)
            }

            loadExercisesForDay(dayIndex)
            Toast.makeText(this, "${exercise.name} deleted", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    data class WorkoutDay(
        val workoutType: String,
        val isCompleted: Boolean = false
    )
}