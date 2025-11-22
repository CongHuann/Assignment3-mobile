package com.example.assignment3

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.adapters.SetHistoryAdapter
import com.example.assignment3.models.Exercise
import com.example.assignment3.models.ExerciseSession
import com.example.assignment3.models.SetData
import com.example.assignment3.models.WorkoutSession
import com.example.assignment3.repository.FirebaseRepository
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class WorkoutSessionActivity : AppCompatActivity() {
    private lateinit var repository: FirebaseRepository

    // ==================== WORKOUT METADATA ====================

    private var dayIndex: Int = -1
    private var workoutType: String = ""
    private var dayName: String = ""

    // ==================== UI COMPONENTS ====================

    // Top bar
    private lateinit var btnBack: ImageButton
    private lateinit var btnPause: ImageButton
    private lateinit var tvWorkoutTitle: TextView
    private lateinit var tvTimer: TextView

    // Progress section
    private lateinit var tvExerciseProgress: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgressPercentage: TextView

    // Exercise info
    private lateinit var tvExerciseName: TextView
    private lateinit var tvExerciseTarget: TextView

    // Weight input
    private lateinit var llWeightInput: LinearLayout
    private lateinit var btnWeightMinus: ImageButton
    private lateinit var etWeight: EditText
    private lateinit var btnWeightPlus: ImageButton

    // Reps input
    private lateinit var llRepsInput: LinearLayout
    private lateinit var btnRepsMinus: ImageButton
    private lateinit var etReps: EditText
    private lateinit var btnRepsPlus: ImageButton

    // Rest timer
    private lateinit var llRestTimer: LinearLayout
    private lateinit var tvRestTimer: TextView
    private lateinit var progressBarRest: ProgressBar
    private lateinit var btnSkipRest: Button

    // Action buttons
    private lateinit var llActionButtons: LinearLayout
    private lateinit var btnSkipSet: Button
    private lateinit var btnCompleteSet: Button

    // Set history and stats
    private lateinit var rvSetHistory: RecyclerView
    private lateinit var tvNextExercise: TextView
    private lateinit var btnSkipExercise: Button
    private lateinit var tvTotalVolume: TextView

    // ==================== DATA ====================

    private lateinit var workoutSession: WorkoutSession

    // Current position in workout
    private var currentExerciseIndex = 0
    private var currentSetIndex = 0

    // Exercise list for this workout
    private var exercises = mutableListOf<Exercise>()


    //Timer
    private var workoutTimer: Timer? = null

    // Rest timer
    private var restCountDownTimer: CountDownTimer? = null

    // Timing tracking
    private var workoutStartTime = 0L
    private var elapsedSeconds = 0L


    // RecyclerView
    private lateinit var setHistoryAdapter: SetHistoryAdapter

    // ==================== LIFECYCLE ====================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_session)

        //Firebase
        repository = (application as MyApplication).repository

        initViews()
        loadWorkoutData()
        setupClickListeners()
        startWorkoutTimer()
        loadCurrentExercise()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel timers
        workoutTimer?.cancel()
        restCountDownTimer?.cancel()
    }

    // ==================== INITIALIZATION ====================

    private fun initViews() {
        // Top bar
        btnBack = findViewById(R.id.btnBack)
        btnPause = findViewById(R.id.btnPause)
        tvWorkoutTitle = findViewById(R.id.tvWorkoutTitle)
        tvTimer = findViewById(R.id.tvTimer)

        // Progress
        tvExerciseProgress = findViewById(R.id.tvExerciseProgress)
        progressBar = findViewById(R.id.progressBar)
        tvProgressPercentage = findViewById(R.id.tvProgressPercentage)

        // Exercise info
        tvExerciseName = findViewById(R.id.tvExerciseName)
        tvExerciseTarget = findViewById(R.id.tvExerciseTarget)

        // Weight input
        llWeightInput = findViewById(R.id.llWeightInput)
        btnWeightMinus = findViewById(R.id.btnWeightMinus)
        etWeight = findViewById(R.id.etWeight)
        btnWeightPlus = findViewById(R.id.btnWeightPlus)

        // Reps input
        llRepsInput = findViewById(R.id.llRepsInput)
        btnRepsMinus = findViewById(R.id.btnRepsMinus)
        etReps = findViewById(R.id.etReps)
        btnRepsPlus = findViewById(R.id.btnRepsPlus)

        // Rest timer
        llRestTimer = findViewById(R.id.llRestTimer)
        tvRestTimer = findViewById(R.id.tvRestTimer)
        progressBarRest = findViewById(R.id.progressBarRest)
        btnSkipRest = findViewById(R.id.btnSkipRest)

        // Actions
        llActionButtons = findViewById(R.id.llActionButtons)
        btnSkipSet = findViewById(R.id.btnSkipSet)
        btnCompleteSet = findViewById(R.id.btnCompleteSet)

        // History and stats
        rvSetHistory = findViewById(R.id.rvSetHistory)
        tvNextExercise = findViewById(R.id.tvNextExercise)
        btnSkipExercise = findViewById(R.id.btnSkipExercise)
        tvTotalVolume = findViewById(R.id.tvTotalVolume)
    }

    /**
     * Load workout data from Intent extras
     */
    private fun loadWorkoutData() {
        // Get workout metadata
        dayIndex = intent.getIntExtra("DAY_INDEX", 0)
        dayName = intent.getStringExtra("DAY_NAME") ?: "Monday"
        workoutType = intent.getStringExtra("WORKOUT_TYPE") ?: "Push"

        // Get exercise arrays
        val ids = intent.getIntArrayExtra("EXERCISE_IDS") ?: return
        val names = intent.getStringArrayExtra("EXERCISE_NAMES") ?: return
        val setsArray = intent.getIntArrayExtra("EXERCISE_SETS") ?: return
        val repsArray = intent.getStringArrayExtra("EXERCISE_REPS") ?: return
        val musclesArray = intent.getStringArrayExtra("EXERCISE_MUSCLES") ?: return

        // Rebuild exercise list
        exercises.clear()
        for (i in ids.indices) {
            exercises.add(Exercise(
                id = ids[i],
                name = names[i],
                sets = setsArray[i],
                reps = repsArray[i],
                targetMuscle = musclesArray[i]
            ))
        }

        if (exercises.isEmpty()) {
            Toast.makeText(this, "No exercises found!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Create workout session object
        workoutSession = WorkoutSession(
            dayIndex = dayIndex,
            dayName = dayName,
            workoutType = workoutType
        )

        // Initialize empty sets for each exercise
        exercises.forEach { exercise ->
            val exerciseSession = ExerciseSession(exercise)
            repeat(exercise.sets) {
                exerciseSession.sets.add(null)
            }
            workoutSession.exercises.add(exerciseSession)
        }

        // Update title
        tvWorkoutTitle.text = "$dayName - $workoutType Day"
    }

    /**
     * Setup click listeners for all interactive elements
     */
    private fun setupClickListeners() {
        btnBack.setOnClickListener { showExitConfirmation() }
        btnPause.setOnClickListener { togglePause() }

        // Weight adjustment
        btnWeightMinus.setOnClickListener { adjustWeight(-2.5) }
        btnWeightPlus.setOnClickListener { adjustWeight(2.5) }

        // Reps adjustment
        btnRepsMinus.setOnClickListener { adjustReps(-1) }
        btnRepsPlus.setOnClickListener { adjustReps(1) }

        // Set actions
        btnCompleteSet.setOnClickListener { completeCurrentSet() }
        btnSkipSet.setOnClickListener { skipCurrentSet() }

        // Rest and navigation
        btnSkipRest.setOnClickListener { skipRest() }
        btnSkipExercise.setOnClickListener { showSkipExerciseConfirmation() }
    }

    // ==================== TIMER MANAGEMENT ====================

    /**
     * Start main workout timer (counts up from 00:00)
     * Updates every second unless paused
     */
    private fun startWorkoutTimer() {
        workoutStartTime = System.currentTimeMillis()
        workoutTimer = Timer()
        workoutTimer?.scheduleAtFixedRate(0, 1000) {
            if (!workoutSession.isPaused) {
                elapsedSeconds++
                runOnUiThread { updateTimerDisplay() }
            }
        }
    }

    /**
     * Update timer display in format MM:SS
     */
    private fun updateTimerDisplay() {
        val minutes = elapsedSeconds / 60
        val seconds = elapsedSeconds % 60
        tvTimer.text = String.format("%02d:%02d", minutes, seconds)
    }

    /**
     * Start rest timer between sets (countdown)
     */
    private fun startRestTimer(seconds: Int) {
        showRestMode()

        val totalMillis = seconds * 1000L
        progressBarRest.max = seconds

        restCountDownTimer = object : CountDownTimer(totalMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                tvRestTimer.text = String.format("00:%02d", secondsRemaining)
                progressBarRest.progress = secondsRemaining

                // Warning
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

    /**
     * Skip rest timer and move to next set
     */
    private fun skipRest() {
        restCountDownTimer?.cancel()
        showInputMode()
        loadCurrentExercise()
    }

    /**
     * Toggle pause state (pause/resume workout timer)
     */
    private fun togglePause() {
        workoutSession.isPaused = !workoutSession.isPaused

        if (workoutSession.isPaused) {
            restCountDownTimer?.cancel()
            Toast.makeText(this, "Workout paused", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Workout resumed", Toast.LENGTH_SHORT).show()
        }
    }

    // ==================== EXERCISE MANAGEMENT ====================

    private fun loadCurrentExercise() {
        // Check if all exercises completed
        if (currentExerciseIndex >= workoutSession.exercises.size) {
            finishWorkout()
            return
        }

        val exerciseSession = workoutSession.exercises[currentExerciseIndex]
        val exercise = exerciseSession.exercise

        // Update exercise info
        tvExerciseName.text = exercise.name.uppercase()
        tvExerciseTarget.text = "Target: ${exercise.reps} reps • ${exercise.targetMuscle}"

        updateProgress()
        setupSetHistory(exerciseSession)
        updateNextExercisePreview()

        // Pre-fill input with previous set values or defaults
        if (currentSetIndex > 0 && exerciseSession.sets[currentSetIndex - 1] != null) {
            val previousSet = exerciseSession.sets[currentSetIndex - 1]!!
            etWeight.setText(previousSet.weight.toString())
            etReps.setText(previousSet.reps.toString())
        } else {
            etWeight.setText("50")
            etReps.setText(exercise.reps.split("-").firstOrNull() ?: "10")
        }

        showInputMode()
    }

    /**
     * Setup RecyclerView showing completed sets for current exercise
     */
    private fun setupSetHistory(exerciseSession: ExerciseSession) {
        setHistoryAdapter = SetHistoryAdapter(exerciseSession.sets.toMutableList())
        rvSetHistory.layoutManager = LinearLayoutManager(this)
        rvSetHistory.adapter = setHistoryAdapter
    }

    /**
     * Update progress indicators
     */
    private fun updateProgress() {
        val exercise = workoutSession.exercises[currentExerciseIndex].exercise
        val totalExercises = workoutSession.exercises.size

        // Exercise and set progress text
        tvExerciseProgress.text = "Exercise ${currentExerciseIndex + 1} of $totalExercises • Set ${currentSetIndex + 1} of ${exercise.sets}"

        // Progress bar
        val percentage = workoutSession.progressPercentage
        progressBar.progress = percentage
        tvProgressPercentage.text = "$percentage% (${workoutSession.completedSets}/${workoutSession.totalSets} sets)"

        // Total volume
        tvTotalVolume.text = "Total Volume: ${String.format("%.1f", workoutSession.totalVolume)} kg"
    }

    /**
     * Update next exercise preview text
     */
    private fun updateNextExercisePreview() {
        if (currentExerciseIndex + 1 < workoutSession.exercises.size) {
            val nextExercise = workoutSession.exercises[currentExerciseIndex + 1].exercise
            tvNextExercise.text = "Next: ${nextExercise.name} (${nextExercise.sets}×${nextExercise.reps} • ${nextExercise.targetMuscle})"
        } else {
            tvNextExercise.text = "Last exercise! 💪"
        }
        tvNextExercise.visibility = TextView.VISIBLE
    }

    // ==================== INPUT ADJUSTMENT ====================

    /**
     * Adjust weight by delta (usually ±2.5 kg)
     */
    private fun adjustWeight(delta: Double) {
        val current = etWeight.text.toString().toDoubleOrNull() ?: 0.0
        val new = (current + delta).coerceAtLeast(0.0)
        etWeight.setText(String.format("%.1f", new))
    }

    /**
     * Adjust reps by delta (usually ±1)
     */
    private fun adjustReps(delta: Int) {
        val current = etReps.text.toString().toIntOrNull() ?: 0
        val new = (current + delta).coerceAtLeast(0)
        etReps.setText(new.toString())
    }

    // ==================== SET COMPLETION ====================

    /**
     * Complete current set with entered weight and reps
     */
    private fun completeCurrentSet() {
        val weight = etWeight.text.toString().toDoubleOrNull() ?: 0.0
        val reps = etReps.text.toString().toIntOrNull() ?: 0

        // Validate input
        if (weight <= 0 || reps <= 0) {
            Toast.makeText(this, "Please enter valid weight and reps", Toast.LENGTH_SHORT).show()
            return
        }

        val exerciseSession = workoutSession.exercises[currentExerciseIndex]

        // Create set data
        val setData = SetData(
            setNumber = currentSetIndex + 1,
            weight = weight,
            reps = reps,
            isCompleted = true
        )
        exerciseSession.sets[currentSetIndex] = setData

        Toast.makeText(this, "Set ${currentSetIndex + 1} completed! ${setData.volume} kg", Toast.LENGTH_SHORT).show()

        // Move to next set
        currentSetIndex++

        // Check if exercise completed
        if (currentSetIndex >= exerciseSession.exercise.sets) {
            exerciseSession.isCompleted = true
            currentExerciseIndex++
            currentSetIndex = 0

            // Check if all exercises completed
            if (currentExerciseIndex >= workoutSession.exercises.size) {
                finishWorkout()
                return
            } else {
                loadCurrentExercise()
            }
        } else {
            // Start rest timer before next set
            startRestTimer(exerciseSession.restTime)
        }

        // Update UI
        updateProgress()
        setHistoryAdapter.notifyDataSetChanged()
    }

    /**
     * Skip current set without recording data
     * Moves to next set or exercise
     */
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

    // ==================== UI MODE SWITCHING ====================

    /**
     * Show input mode (weight, reps, complete button)
     * Hides rest timer
     */
    private fun showInputMode() {
        llWeightInput.visibility = LinearLayout.VISIBLE
        llRepsInput.visibility = LinearLayout.VISIBLE
        llActionButtons.visibility = LinearLayout.VISIBLE
        llRestTimer.visibility = LinearLayout.GONE
    }

    /**
     * Show rest mode (countdown timer, skip button)
     * Hides input fields
     */
    private fun showRestMode() {
        llWeightInput.visibility = LinearLayout.GONE
        llRepsInput.visibility = LinearLayout.GONE
        llActionButtons.visibility = LinearLayout.GONE
        llRestTimer.visibility = LinearLayout.VISIBLE
        tvRestTimer.setTextColor(getColor(R.color.accent_orange))
    }

    // ==================== DIALOGS ====================

    /**
     * Show confirmation dialog for skipping entire exercise
     * Warns user they will skip all remaining sets
     */
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

    /**
     * Show confirmation dialog for exiting workout
     * Warns that progress will be lost
     */
    private fun showExitConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Exit Workout?")
            .setMessage("Your progress will be lost.")
            .setPositiveButton("Exit") { _, _ -> finish() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ==================== WORKOUT COMPLETION ====================

    /**
     * Finish workout and save to Firebase
     */
    /**
     * Finish workout and save to Firebase
     */
    private fun finishWorkout() {
        // Cancel timers
        workoutTimer?.cancel()
        restCountDownTimer?.cancel()

        // Mark as completed
        workoutSession.isCompleted = true
        workoutSession.endTime = System.currentTimeMillis()
        workoutSession.totalDuration = elapsedSeconds

        // ✅ SAVE TO FIREBASE - MUST WAIT FOR COMPLETION
        lifecycleScope.launch {
            try {
                // Save workout completion to Firebase
                repository.updateWorkout(
                    dayIndex = dayIndex,
                    workoutType = workoutType,
                    isCompleted = true
                )

                // Return result to WorkoutPlannerActivity
                val resultIntent = Intent().apply {
                    putExtra("WORKOUT_COMPLETED", true)
                    putExtra("DAY_INDEX", dayIndex)  // ✅ IMPORTANT!
                    putExtra("TOTAL_VOLUME", workoutSession.totalVolume)
                    putExtra("TOTAL_DURATION", workoutSession.totalDuration)
                    putExtra("COMPLETED_EXERCISES", workoutSession.completedExercises)
                    putExtra("TOTAL_EXERCISES", workoutSession.totalExercises)
                }

                setResult(RESULT_OK, resultIntent)

                Toast.makeText(
                    this@WorkoutSessionActivity,
                    "🎉 Workout completed! Great job! 💪",
                    Toast.LENGTH_LONG
                ).show()

                finish()

            } catch (e: Exception) {
                e.printStackTrace()

                // Still return result even if Firebase fails
                val resultIntent = Intent().apply {
                    putExtra("WORKOUT_COMPLETED", true)
                    putExtra("DAY_INDEX", dayIndex)
                }
                setResult(RESULT_OK, resultIntent)

                Toast.makeText(
                    this@WorkoutSessionActivity,
                    "⚠️ Workout completed but save failed",
                    Toast.LENGTH_LONG
                ).show()

                finish()
            }
        }
    }
}