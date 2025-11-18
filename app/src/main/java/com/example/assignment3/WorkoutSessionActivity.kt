package com.example.assignment3

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.adapters.SetHistoryAdapter
import com.example.assignment3.models.Exercise
import com.example.assignment3.models.ExerciseSession
import com.example.assignment3.models.SetData
import com.example.assignment3.models.WorkoutSession
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class WorkoutSessionActivity : AppCompatActivity() {

    // Views
    private lateinit var btnBack: ImageButton
    private lateinit var btnPause: ImageButton
    private lateinit var tvWorkoutTitle: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvExerciseProgress: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgressPercentage: TextView
    private lateinit var tvExerciseName: TextView
    private lateinit var tvExerciseTarget: TextView
    private lateinit var llWeightInput: LinearLayout
    private lateinit var btnWeightMinus: ImageButton
    private lateinit var etWeight: EditText
    private lateinit var btnWeightPlus: ImageButton
    private lateinit var llRepsInput: LinearLayout
    private lateinit var btnRepsMinus: ImageButton
    private lateinit var etReps: EditText
    private lateinit var btnRepsPlus: ImageButton
    private lateinit var llRestTimer: LinearLayout
    private lateinit var tvRestTimer: TextView
    private lateinit var progressBarRest: ProgressBar
    private lateinit var btnSkipRest: Button
    private lateinit var llActionButtons: LinearLayout
    private lateinit var btnSkipSet: Button
    private lateinit var btnCompleteSet: Button
    private lateinit var rvSetHistory: RecyclerView
    private lateinit var tvNextExercise: TextView
    private lateinit var btnSkipExercise: Button
    private lateinit var tvTotalVolume: TextView

    // Data
    private lateinit var workoutSession: WorkoutSession
    private var currentExerciseIndex = 0
    private var currentSetIndex = 0
    private var exercises = mutableListOf<Exercise>()

    // Timers
    private var workoutTimer: Timer? = null
    private var restCountDownTimer: CountDownTimer? = null
    private var workoutStartTime = 0L
    private var elapsedSeconds = 0L

    // Adapters
    private lateinit var setHistoryAdapter: SetHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            android.util.Log.e("WorkoutSession", "🟢 ========== onCreate START ==========")

            setContentView(R.layout.activity_workout_session)
            android.util.Log.e("WorkoutSession", "✅ setContentView success")

            initViews()
            android.util.Log.e("WorkoutSession", "✅ initViews success")

            loadWorkoutData()
            android.util.Log.e("WorkoutSession", "✅ loadWorkoutData success")

            setupClickListeners()
            android.util.Log.e("WorkoutSession", "✅ setupClickListeners success")

            startWorkoutTimer()
            android.util.Log.e("WorkoutSession", "✅ startWorkoutTimer success")

            loadCurrentExercise()
            android.util.Log.e("WorkoutSession", "✅ loadCurrentExercise success")

            android.util.Log.e("WorkoutSession", "🟢 ========== onCreate END ==========")
        } catch (e: Exception) {
            android.util.Log.e("WorkoutSession", "❌ ERROR in onCreate: ${e.message}")
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnPause = findViewById(R.id.btnPause)
        tvWorkoutTitle = findViewById(R.id.tvWorkoutTitle)
        tvTimer = findViewById(R.id.tvTimer)
        tvExerciseProgress = findViewById(R.id.tvExerciseProgress)
        progressBar = findViewById(R.id.progressBar)
        tvProgressPercentage = findViewById(R.id.tvProgressPercentage)
        tvExerciseName = findViewById(R.id.tvExerciseName)
        tvExerciseTarget = findViewById(R.id.tvExerciseTarget)
        llWeightInput = findViewById(R.id.llWeightInput)
        btnWeightMinus = findViewById(R.id.btnWeightMinus)
        etWeight = findViewById(R.id.etWeight)
        btnWeightPlus = findViewById(R.id.btnWeightPlus)
        llRepsInput = findViewById(R.id.llRepsInput)
        btnRepsMinus = findViewById(R.id.btnRepsMinus)
        etReps = findViewById(R.id.etReps)
        btnRepsPlus = findViewById(R.id.btnRepsPlus)
        llRestTimer = findViewById(R.id.llRestTimer)
        tvRestTimer = findViewById(R.id.tvRestTimer)
        progressBarRest = findViewById(R.id.progressBarRest)
        btnSkipRest = findViewById(R.id.btnSkipRest)
        llActionButtons = findViewById(R.id.llActionButtons)
        btnSkipSet = findViewById(R.id.btnSkipSet)
        btnCompleteSet = findViewById(R.id.btnCompleteSet)
        rvSetHistory = findViewById(R.id.rvSetHistory)
        tvNextExercise = findViewById(R.id.tvNextExercise)
        btnSkipExercise = findViewById(R.id.btnSkipExercise)
        tvTotalVolume = findViewById(R.id.tvTotalVolume)
    }

    private fun loadWorkoutData() {
        try {
            val dayIndex = intent.getIntExtra("DAY_INDEX", 0)
            val dayName = intent.getStringExtra("DAY_NAME") ?: "Monday"
            val workoutType = intent.getStringExtra("WORKOUT_TYPE") ?: "Push"

            android.util.Log.e("WorkoutSession", "dayIndex=$dayIndex, dayName=$dayName, workoutType=$workoutType")

            // ✅ NHẬN EXERCISES TỪ ARRAYS
            val ids = intent.getIntArrayExtra("EXERCISE_IDS")
            val names = intent.getStringArrayExtra("EXERCISE_NAMES")
            val setsArray = intent.getIntArrayExtra("EXERCISE_SETS")
            val repsArray = intent.getStringArrayExtra("EXERCISE_REPS")
            val musclesArray = intent.getStringArrayExtra("EXERCISE_MUSCLES")
            val count = intent.getIntExtra("EXERCISE_COUNT", 0)

            if (ids == null || names == null || setsArray == null || repsArray == null || musclesArray == null) {
                android.util.Log.e("WorkoutSession", "❌ One or more arrays is NULL!")
                Toast.makeText(this, "No exercises data received", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            android.util.Log.e("WorkoutSession", "Received ${count} exercises")

            // Rebuild exercises từ arrays
            exercises = mutableListOf()
            for (i in ids.indices) {
                val exercise = Exercise(
                    id = ids[i],
                    name = names[i],
                    sets = setsArray[i],
                    reps = repsArray[i],
                    targetMuscle = musclesArray[i]
                )
                exercises.add(exercise)
                android.util.Log.e("WorkoutSession", "  Exercise[$i]: ${exercise.name}, ${exercise.sets}×${exercise.reps}")
            }

            if (exercises.isEmpty()) {
                Toast.makeText(this, "No exercises found!", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            workoutSession = WorkoutSession(
                dayIndex = dayIndex,
                dayName = dayName,
                workoutType = workoutType
            )

            // Convert exercises to ExerciseSessions
            exercises.forEach { exercise ->
                val exerciseSession = ExerciseSession(exercise)
                // Initialize sets as null (locked)
                for (i in 0 until exercise.sets) {
                    exerciseSession.sets.add(null)
                }
                workoutSession.exercises.add(exerciseSession)
            }

            tvWorkoutTitle.text = "$dayName - $workoutType Day"

        } catch (e: Exception) {
            android.util.Log.e("WorkoutSession", "❌ ERROR in loadWorkoutData: ${e.message}")
            e.printStackTrace()
            Toast.makeText(this, "Failed to load workout data", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            showExitConfirmation()
        }

        btnPause.setOnClickListener {
            togglePause()
        }

        btnWeightMinus.setOnClickListener {
            adjustWeight(-2.5)
        }

        btnWeightPlus.setOnClickListener {
            adjustWeight(2.5)
        }

        btnRepsMinus.setOnClickListener {
            adjustReps(-1)
        }

        btnRepsPlus.setOnClickListener {
            adjustReps(1)
        }

        btnCompleteSet.setOnClickListener {
            completeCurrentSet()
        }

        btnSkipSet.setOnClickListener {
            skipCurrentSet()
        }

        btnSkipRest.setOnClickListener {
            skipRest()
        }

        btnSkipExercise.setOnClickListener {
            showSkipExerciseConfirmation()
        }
    }

    private fun startWorkoutTimer() {
        workoutStartTime = System.currentTimeMillis()
        workoutTimer = Timer()
        workoutTimer?.scheduleAtFixedRate(0, 1000) {
            if (!workoutSession.isPaused) {
                elapsedSeconds++
                runOnUiThread {
                    updateTimerDisplay()
                }
            }
        }
    }

    private fun updateTimerDisplay() {
        val minutes = elapsedSeconds / 60
        val seconds = elapsedSeconds % 60
        tvTimer.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun loadCurrentExercise() {
        if (currentExerciseIndex >= workoutSession.exercises.size) {
            finishWorkout()
            return
        }

        val exerciseSession = workoutSession.exercises[currentExerciseIndex]
        val exercise = exerciseSession.exercise

        android.util.Log.e("WorkoutSession", "Loading exercise: ${exercise.name}, set ${currentSetIndex + 1}/${exercise.sets}")

        tvExerciseName.text = exercise.name.uppercase()
        tvExerciseTarget.text = "Target: ${exercise.reps} reps • ${exercise.targetMuscle}"

        updateProgress()
        setupSetHistory(exerciseSession)
        updateNextExercisePreview()

        if (currentSetIndex > 0 && exerciseSession.sets[currentSetIndex - 1] != null) {
            val previousSet = exerciseSession.sets[currentSetIndex - 1]
            etWeight.setText(previousSet!!.weight.toString())
            etReps.setText(previousSet.reps.toString())
        } else {
            etWeight.setText("50")
            etReps.setText(exercise.reps.split("-").firstOrNull() ?: "10")
        }

        showInputMode()
    }

    private fun setupSetHistory(exerciseSession: ExerciseSession) {
        val sets = exerciseSession.sets.toMutableList()
        setHistoryAdapter = SetHistoryAdapter(sets)
        rvSetHistory.layoutManager = LinearLayoutManager(this)
        rvSetHistory.adapter = setHistoryAdapter
    }

    private fun updateProgress() {
        val exercise = workoutSession.exercises[currentExerciseIndex].exercise
        val totalExercises = workoutSession.exercises.size
        val completedSets = workoutSession.completedSets
        val totalSets = workoutSession.totalSets

        tvExerciseProgress.text = "Exercise ${currentExerciseIndex + 1} of $totalExercises • Set ${currentSetIndex + 1} of ${exercise.sets}"

        val percentage = workoutSession.progressPercentage
        progressBar.progress = percentage
        tvProgressPercentage.text = "$percentage% ($completedSets/$totalSets sets)"

        tvTotalVolume.text = "Total Volume: ${String.format("%.1f", workoutSession.totalVolume)} kg"
    }

    private fun updateNextExercisePreview() {
        if (currentExerciseIndex + 1 < workoutSession.exercises.size) {
            val nextExercise = workoutSession.exercises[currentExerciseIndex + 1].exercise
            tvNextExercise.text = "Next: ${nextExercise.name} (${nextExercise.sets}×${nextExercise.reps} • ${nextExercise.targetMuscle})"
            tvNextExercise.visibility = TextView.VISIBLE
        } else {
            tvNextExercise.text = "Last exercise! 💪"
            tvNextExercise.visibility = TextView.VISIBLE
        }
    }

    private fun adjustWeight(delta: Double) {
        val current = etWeight.text.toString().toDoubleOrNull() ?: 0.0
        val new = (current + delta).coerceAtLeast(0.0)
        etWeight.setText(String.format("%.1f", new))
    }

    private fun adjustReps(delta: Int) {
        val current = etReps.text.toString().toIntOrNull() ?: 0
        val new = (current + delta).coerceAtLeast(0)
        etReps.setText(new.toString())
    }

    private fun completeCurrentSet() {
        val weight = etWeight.text.toString().toDoubleOrNull() ?: 0.0
        val reps = etReps.text.toString().toIntOrNull() ?: 0

        if (weight <= 0 || reps <= 0) {
            Toast.makeText(this, "Please enter valid weight and reps", Toast.LENGTH_SHORT).show()
            return
        }

        val exerciseSession = workoutSession.exercises[currentExerciseIndex]
        val setData = SetData(
            setNumber = currentSetIndex + 1,
            weight = weight,
            reps = reps,
            isCompleted = true
        )

        exerciseSession.sets[currentSetIndex] = setData

        android.util.Log.e("WorkoutSession", "Set completed: ${setData.weight} kg × ${setData.reps} reps")

        Toast.makeText(this, "Set ${currentSetIndex + 1} completed! ${setData.volume} kg", Toast.LENGTH_SHORT).show()

        currentSetIndex++

        if (currentSetIndex >= exerciseSession.exercise.sets) {
            exerciseSession.isCompleted = true
            currentExerciseIndex++
            currentSetIndex = 0

            if (currentExerciseIndex >= workoutSession.exercises.size) {
                finishWorkout()
            } else {
                loadCurrentExercise()
            }
        } else {
            startRestTimer(exerciseSession.restTime)
        }

        updateProgress()
        setHistoryAdapter.notifyDataSetChanged()
    }

    private fun skipCurrentSet() {
        val exerciseSession = workoutSession.exercises[currentExerciseIndex]
        currentSetIndex++

        if (currentSetIndex >= exerciseSession.exercise.sets) {
            exerciseSession.isCompleted = true
            currentExerciseIndex++
            currentSetIndex = 0

            if (currentExerciseIndex >= workoutSession.exercises.size) {
                finishWorkout()
            } else {
                loadCurrentExercise()
            }
        } else {
            loadCurrentExercise()
        }

        updateProgress()
    }

    private fun startRestTimer(seconds: Int) {
        android.util.Log.e("WorkoutSession", "Starting rest timer: ${seconds}s")

        showRestMode()

        val totalMillis = seconds * 1000L
        progressBarRest.max = seconds

        restCountDownTimer = object : CountDownTimer(totalMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                tvRestTimer.text = String.format("00:%02d", secondsRemaining)
                progressBarRest.progress = secondsRemaining

                if (secondsRemaining <= 10) {
                    tvRestTimer.setTextColor(getColor(android.R.color.holo_orange_light))
                }
            }

            override fun onFinish() {
                tvRestTimer.text = "00:00"
                Toast.makeText(this@WorkoutSessionActivity, "Rest complete! 💪", Toast.LENGTH_SHORT).show()
                skipRest()
            }
        }.start()
    }

    private fun skipRest() {
        restCountDownTimer?.cancel()
        showInputMode()
        loadCurrentExercise()
    }

    private fun showInputMode() {
        llWeightInput.visibility = LinearLayout.VISIBLE
        llRepsInput.visibility = LinearLayout.VISIBLE
        llActionButtons.visibility = LinearLayout.VISIBLE
        llRestTimer.visibility = LinearLayout.GONE
    }

    private fun showRestMode() {
        llWeightInput.visibility = LinearLayout.GONE
        llRepsInput.visibility = LinearLayout.GONE
        llActionButtons.visibility = LinearLayout.GONE
        llRestTimer.visibility = LinearLayout.VISIBLE
        tvRestTimer.setTextColor(getColor(R.color.accent_orange))
    }

    private fun togglePause() {
        workoutSession.isPaused = !workoutSession.isPaused

        if (workoutSession.isPaused) {
            // Change icon to play (you need ic_play.xml)
            restCountDownTimer?.cancel()
            Toast.makeText(this, "Workout paused", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Workout resumed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSkipExerciseConfirmation() {
        val exerciseSession = workoutSession.exercises[currentExerciseIndex]

        AlertDialog.Builder(this)
            .setTitle("Skip ${exerciseSession.exercise.name}?")
            .setMessage("You will skip all ${exerciseSession.exercise.sets} sets.")
            .setPositiveButton("Skip") { _, _ ->
                exerciseSession.isSkipped = true
                currentExerciseIndex++
                currentSetIndex = 0

                if (currentExerciseIndex >= workoutSession.exercises.size) {
                    finishWorkout()
                } else {
                    loadCurrentExercise()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showExitConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Exit Workout?")
            .setMessage("Your progress will be lost.")
            .setPositiveButton("Exit") { _, _ ->
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun finishWorkout() {
        workoutTimer?.cancel()
        restCountDownTimer?.cancel()

        workoutSession.isCompleted = true
        workoutSession.endTime = System.currentTimeMillis()
        workoutSession.totalDuration = elapsedSeconds

        android.util.Log.e("WorkoutSession", "🎉 Workout completed!")
        android.util.Log.e("WorkoutSession", "Total time: ${elapsedSeconds}s")
        android.util.Log.e("WorkoutSession", "Total volume: ${workoutSession.totalVolume} kg")
        android.util.Log.e("WorkoutSession", "Completed exercises: ${workoutSession.completedExercises}/${workoutSession.totalExercises}")

        // TODO: Save to database

        // ✅ TRẢ KẾT QUẢ VỀ WORKOUTPLANNERACTIVITY
        val resultIntent = intent
        resultIntent.putExtra("WORKOUT_COMPLETED", true)
        resultIntent.putExtra("DAY_INDEX", workoutSession.dayIndex)
        resultIntent.putExtra("TOTAL_VOLUME", workoutSession.totalVolume)
        resultIntent.putExtra("TOTAL_DURATION", workoutSession.totalDuration)
        resultIntent.putExtra("COMPLETED_EXERCISES", workoutSession.completedExercises)
        resultIntent.putExtra("TOTAL_EXERCISES", workoutSession.totalExercises)

        setResult(RESULT_OK, resultIntent)

        // Show success message
        Toast.makeText(this, "🎉 Workout completed! no pain no gain! 💪", Toast.LENGTH_LONG).show()

        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        workoutTimer?.cancel()
        restCountDownTimer?.cancel()
    }
}