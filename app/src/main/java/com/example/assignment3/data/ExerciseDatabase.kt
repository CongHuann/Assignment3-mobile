package com.example.assignment3.data

import com.example.assignment3.models.Exercise

object ExerciseDatabase {

    fun getAllExercises(): List<Exercise> = listOf(

        // ==================== CHEST (5 exercises) ====================
        Exercise(
            id = 1,
            name = "Bench Press",
            sets = 4,
            reps = "8-10",
            targetMuscle = "Chest",
            description = "The king of chest exercises. Builds overall chest mass and strength.",
            instructions = listOf(
                "Lie flat on bench with feet firmly on ground",
                "Grip barbell slightly wider than shoulder-width",
                "Unrack bar and hold above chest with arms extended",
                "Lower bar slowly to mid-chest, elbows at 45°",
                "Press back up explosively to starting position",
                "Exhale on the way up, inhale on the way down"
            ),
            tips = listOf(
                "Keep shoulder blades retracted",
                "Don't bounce bar off chest",
                "Maintain slight arch in lower back",
                "Touch chest with every rep"
            ),
            difficulty = "Intermediate",
            equipment = "Barbell, Bench",
            secondaryMuscles = listOf("Anterior Deltoids", "Triceps"),
            imageUrl = "ex_bench_press"
        ),

        Exercise(
            id = 2,
            name = "Incline Bench Press",
            sets = 4,
            reps = "8-10",
            targetMuscle = "Chest",
            description = "Targets upper chest for a fuller, more aesthetic look.",
            instructions = listOf(
                "Set bench to 30-45 degree incline",
                "Lie back with feet flat on floor",
                "Grip bar slightly wider than shoulders",
                "Lower bar to upper chest area",
                "Press back up to starting position",
                "Keep core engaged throughout"
            ),
            tips = listOf(
                "Don't set incline too steep (max 45°)",
                "Bar should touch upper chest, not neck",
                "Drive feet into ground for stability",
                "Don't arch back excessively"
            ),
            difficulty = "Intermediate",
            equipment = "Barbell, Incline Bench",
            secondaryMuscles = listOf("Anterior Deltoids", "Triceps"),
            imageUrl = "ex_incline_bench"
        ),

        Exercise(
            id = 3,
            name = "Dumbbell Flyes",
            sets = 3,
            reps = "10-12",
            targetMuscle = "Chest",
            description = "Isolation exercise for chest stretch and definition.",
            instructions = listOf(
                "Lie flat holding dumbbells above chest",
                "Keep slight bend in elbows",
                "Lower weights in arc motion to sides",
                "Stop when you feel deep stretch in chest",
                "Bring weights back together above chest",
                "Squeeze chest hard at top"
            ),
            tips = listOf(
                "Don't go too heavy - focus on stretch",
                "Keep same elbow angle throughout",
                "Don't let dumbbells touch at top",
                "Control the negative portion"
            ),
            difficulty = "Beginner",
            equipment = "Dumbbells, Bench",
            secondaryMuscles = listOf("Anterior Deltoids"),
            imageUrl = "ex_dumbbell_flyes"
        ),

        Exercise(
            id = 4,
            name = "Push-ups",
            sets = 3,
            reps = "12-15",
            targetMuscle = "Chest",
            description = "Classic bodyweight exercise for chest, shoulders, and triceps.",
            instructions = listOf(
                "Start in plank position, hands shoulder-width",
                "Keep body straight from head to heels",
                "Lower until chest nearly touches floor",
                "Keep elbows at 45° to body",
                "Push back up to starting position",
                "Engage core throughout"
            ),
            tips = listOf(
                "Don't let hips sag",
                "Keep neck neutral",
                "Go as low as possible",
                "Start on knees if too difficult"
            ),
            difficulty = "Beginner",
            equipment = "Bodyweight",
            secondaryMuscles = listOf("Shoulders", "Triceps", "Core"),
            imageUrl = "ex_push_ups"
        ),

        Exercise(
            id = 5,
            name = "Cable Crossover",
            sets = 3,
            reps = "12-15",
            targetMuscle = "Chest",
            description = "Cable exercise for chest isolation and constant tension.",
            instructions = listOf(
                "Set cables to high position",
                "Stand in middle with one foot forward",
                "Grab handles with palms facing down",
                "Bring handles together in front of chest",
                "Squeeze chest at the end position",
                "Return slowly to starting position"
            ),
            tips = listOf(
                "Keep slight bend in elbows",
                "Focus on chest contraction",
                "Don't let hands go behind shoulders",
                "Maintain slight forward lean"
            ),
            difficulty = "Intermediate",
            equipment = "Cable Machine",
            secondaryMuscles = listOf("Anterior Deltoids"),
            imageUrl = "ex_cable_crossover"
        ),

        // ==================== BACK (5 exercises) ====================
        Exercise(
            id = 6,
            name = "Deadlift",
            sets = 4,
            reps = "6-8",
            targetMuscle = "Back",
            description = "The king of all exercises. Builds total body strength.",
            instructions = listOf(
                "Stand with feet hip-width, bar over mid-foot",
                "Bend down and grip bar outside legs",
                "Keep chest up, back straight, shoulders back",
                "Drive through heels to stand up",
                "Keep bar close to body throughout",
                "At top, squeeze glutes and stand tall",
                "Lower with control by pushing hips back"
            ),
            tips = listOf(
                "Keep bar close to shins",
                "Never round your back",
                "Engage lats by 'bending the bar'",
                "This is a PULL not a squat"
            ),
            difficulty = "Advanced",
            equipment = "Barbell",
            secondaryMuscles = listOf("Glutes", "Hamstrings", "Traps", "Forearms"),
            imageUrl = "ex_deadlift"
        ),

        Exercise(
            id = 7,
            name = "Pull-ups",
            sets = 4,
            reps = "8-10",
            targetMuscle = "Back",
            description = "Best bodyweight exercise for building a wide, strong back.",
            instructions = listOf(
                "Hang from bar with overhand grip",
                "Hands slightly wider than shoulders",
                "Start from dead hang, arms fully extended",
                "Pull yourself up by driving elbows down",
                "Continue until chin is above bar",
                "Lower with control to dead hang"
            ),
            tips = listOf(
                "Don't swing or use momentum",
                "Pull with back, not just arms",
                "Fully extend arms at bottom",
                "Use assisted machine if needed"
            ),
            difficulty = "Advanced",
            equipment = "Pull-up Bar",
            secondaryMuscles = listOf("Biceps", "Rear Deltoids"),
            imageUrl = "ex_pull_up"
        ),

        Exercise(
            id = 8,
            name = "Barbell Row",
            sets = 4,
            reps = "8-10",
            targetMuscle = "Back",
            description = "Builds thickness in the middle back and overall back strength.",
            instructions = listOf(
                "Stand with feet hip-width apart",
                "Bend forward at hips, back straight",
                "Grip bar slightly wider than shoulders",
                "Pull bar to lower chest/upper abdomen",
                "Squeeze shoulder blades together at top",
                "Lower bar with control"
            ),
            tips = listOf(
                "Keep torso at 45° angle",
                "Don't stand too upright",
                "Lead with elbows, not hands",
                "Avoid using momentum"
            ),
            difficulty = "Intermediate",
            equipment = "Barbell",
            secondaryMuscles = listOf("Biceps", "Rear Deltoids", "Traps"),
            imageUrl = "ex_barbell_row"
        ),

        Exercise(
            id = 9,
            name = "Lat Pulldown",
            sets = 3,
            reps = "10-12",
            targetMuscle = "Back",
            description = "Machine exercise for building lat width and V-taper.",
            instructions = listOf(
                "Sit at lat pulldown machine",
                "Grip bar wider than shoulder-width",
                "Pull bar down to upper chest",
                "Keep chest up and lean back slightly",
                "Squeeze lats at bottom",
                "Control weight on the way up"
            ),
            tips = listOf(
                "Don't pull behind neck",
                "Focus on pulling with elbows",
                "Don't use too much body swing",
                "Squeeze shoulder blades together"
            ),
            difficulty = "Beginner",
            equipment = "Lat Pulldown Machine",
            secondaryMuscles = listOf("Biceps", "Rear Deltoids"),
            imageUrl = "ex_lat_pulldown"
        ),

        Exercise(
            id = 10,
            name = "Cable Row",
            sets = 3,
            reps = "10-12",
            targetMuscle = "Back",
            description = "Cable exercise for mid-back thickness and detail.",
            instructions = listOf(
                "Sit at cable row machine",
                "Grab V-bar handle with both hands",
                "Keep back straight and chest up",
                "Pull handle to lower chest/abdomen",
                "Squeeze shoulder blades together",
                "Extend arms fully on return"
            ),
            tips = listOf(
                "Don't rock back and forth",
                "Keep torso upright",
                "Pull with elbows, not hands",
                "Full stretch at the start"
            ),
            difficulty = "Beginner",
            equipment = "Cable Machine",
            secondaryMuscles = listOf("Biceps", "Rear Deltoids"),
            imageUrl = "ex_cable_row"
        ),

        // ==================== LEGS (6 exercises) ====================
        Exercise(
            id = 11,
            name = "Squat",
            sets = 4,
            reps = "8-10",
            targetMuscle = "Legs",
            description = "The king of leg exercises. Builds mass and strength.",
            instructions = listOf(
                "Place bar on upper back (not neck)",
                "Stand with feet shoulder-width, toes out",
                "Brace core and look straight ahead",
                "Push hips back and bend knees",
                "Lower until thighs parallel to ground",
                "Drive through heels to stand up",
                "Squeeze glutes at top"
            ),
            tips = listOf(
                "Keep chest up throughout",
                "Don't let knees cave inward",
                "Drive knees out as you squat",
                "Keep weight on heels"
            ),
            difficulty = "Intermediate",
            equipment = "Barbell, Squat Rack",
            secondaryMuscles = listOf("Glutes", "Hamstrings", "Core"),
            imageUrl = "ex_squat"
        ),

        Exercise(
            id = 12,
            name = "Leg Press",
            sets = 4,
            reps = "10-12",
            targetMuscle = "Legs",
            description = "Machine exercise for building quad mass safely.",
            instructions = listOf(
                "Sit with back flat against pad",
                "Place feet shoulder-width on platform",
                "Release safety and lower platform",
                "Lower until knees at 90° angle",
                "Press through heels to extend legs",
                "Don't lock knees at top"
            ),
            tips = listOf(
                "Keep lower back pressed to pad",
                "Don't let knees cave inward",
                "Control the negative",
                "Breathe out as you press"
            ),
            difficulty = "Beginner",
            equipment = "Leg Press Machine",
            secondaryMuscles = listOf("Glutes", "Hamstrings"),
            imageUrl = "ex_leg_press"
        ),

        Exercise(
            id = 13,
            name = "Romanian Deadlift",
            sets = 3,
            reps = "8-10",
            targetMuscle = "Legs",
            description = "Targets hamstrings and glutes with emphasis on the stretch.",
            instructions = listOf(
                "Stand holding barbell at thigh level",
                "Keep slight bend in knees",
                "Push hips back and lower bar down legs",
                "Feel stretch in hamstrings",
                "Drive hips forward to return to start",
                "Squeeze glutes at top"
            ),
            tips = listOf(
                "Keep back straight, not rounded",
                "Bar should travel close to legs",
                "Focus on hip hinge movement",
                "Don't turn it into a squat"
            ),
            difficulty = "Intermediate",
            equipment = "Barbell",
            secondaryMuscles = listOf("Glutes", "Lower Back"),
            imageUrl = "ex_romanian_deadlift"
        ),

        Exercise(
            id = 14,
            name = "Leg Curl",
            sets = 3,
            reps = "10-12",
            targetMuscle = "Legs",
            description = "Isolation exercise for hamstring development.",
            instructions = listOf(
                "Lie face down on leg curl machine",
                "Position ankles under pad",
                "Curl legs up toward glutes",
                "Squeeze hamstrings at top",
                "Lower with control",
                "Don't let weight stack touch between reps"
            ),
            tips = listOf(
                "Keep hips pressed to bench",
                "Don't lift hips during curl",
                "Control the negative portion",
                "Full range of motion"
            ),
            difficulty = "Beginner",
            equipment = "Leg Curl Machine",
            secondaryMuscles = listOf("Calves"),
            imageUrl = "ex_leg_curl"
        ),

        Exercise(
            id = 15,
            name = "Leg Extension",
            sets = 3,
            reps = "10-12",
            targetMuscle = "Legs",
            description = "Isolation exercise for quad definition and development.",
            instructions = listOf(
                "Sit in leg extension machine",
                "Position ankles under pad",
                "Extend legs until fully straight",
                "Squeeze quads at top",
                "Lower with control",
                "Don't let weight stack touch"
            ),
            tips = listOf(
                "Keep back against pad",
                "Point toes up or slightly out",
                "Don't use momentum",
                "Pause at top for peak contraction"
            ),
            difficulty = "Beginner",
            equipment = "Leg Extension Machine",
            secondaryMuscles = listOf(),
            imageUrl = "ex_leg_extension"
        ),

        Exercise(
            id = 16,
            name = "Calf Raises",
            sets = 4,
            reps = "12-15",
            targetMuscle = "Legs",
            description = "Builds strong, defined calf muscles.",
            instructions = listOf(
                "Stand on calf raise machine or platform",
                "Position balls of feet on edge",
                "Lower heels below platform level",
                "Rise up on toes as high as possible",
                "Squeeze calves at top",
                "Lower slowly to stretch"
            ),
            tips = listOf(
                "Full range of motion is key",
                "Pause at top and bottom",
                "Don't bounce at bottom",
                "Try different foot positions"
            ),
            difficulty = "Beginner",
            equipment = "Calf Raise Machine or Platform",
            secondaryMuscles = listOf(),
            imageUrl = "ex_calf_raises"
        ),

        // ==================== ARMS (4 exercises) ====================
        Exercise(
            id = 17,
            name = "Barbell Curl",
            sets = 3,
            reps = "8-10",
            targetMuscle = "Arms",
            description = "Classic bicep builder for overall arm mass.",
            instructions = listOf(
                "Stand with feet shoulder-width",
                "Hold bar with underhand grip",
                "Keep elbows close to sides",
                "Curl bar up toward shoulders",
                "Squeeze biceps at top",
                "Lower with control"
            ),
            tips = listOf(
                "Don't swing the weight",
                "Keep elbows stationary",
                "Control the negative",
                "Don't lean back"
            ),
            difficulty = "Beginner",
            equipment = "Barbell",
            secondaryMuscles = listOf("Forearms"),
            imageUrl = "ex_barbell_curl"
        ),

        Exercise(
            id = 18,
            name = "Hammer Curl",
            sets = 3,
            reps = "10-12",
            targetMuscle = "Arms",
            description = "Targets brachialis and brachioradialis for thicker arms.",
            instructions = listOf(
                "Stand holding dumbbells at sides",
                "Keep palms facing each other (neutral grip)",
                "Curl dumbbells up together",
                "Keep elbows close to body",
                "Lower with control",
                "Alternate or do both at once"
            ),
            tips = listOf(
                "Don't rotate wrists",
                "Keep thumbs up throughout",
                "Don't swing dumbbells",
                "Squeeze at the top"
            ),
            difficulty = "Beginner",
            equipment = "Dumbbells",
            secondaryMuscles = listOf("Brachialis", "Forearms"),
            imageUrl = "ex_hammer_curl"
        ),

        Exercise(
            id = 19,
            name = "Tricep Dips",
            sets = 3,
            reps = "8-10",
            targetMuscle = "Arms",
            description = "Compound movement for powerful tricep development.",
            instructions = listOf(
                "Grip parallel bars with arms extended",
                "Lower body by bending elbows",
                "Go down until upper arms parallel to ground",
                "Press back up to starting position",
                "Keep body upright for tricep focus"
            ),
            tips = listOf(
                "Lean forward for more chest",
                "Stay upright for more triceps",
                "Don't go too deep if shoulder hurts",
                "Add weight when bodyweight is easy"
            ),
            difficulty = "Intermediate",
            equipment = "Dip Bars",
            secondaryMuscles = listOf("Chest", "Shoulders"),
            imageUrl = "ex_tricep_dips"
        ),

        Exercise(
            id = 20,
            name = "Skull Crushers",
            sets = 3,
            reps = "10-12",
            targetMuscle = "Arms",
            description = "Isolation exercise for tricep mass and definition.",
            instructions = listOf(
                "Lie on bench holding bar above chest",
                "Keep upper arms vertical and stationary",
                "Lower bar toward forehead by bending elbows",
                "Stop just above forehead",
                "Extend arms back to starting position",
                "Keep upper arms still throughout"
            ),
            tips = listOf(
                "Don't move upper arms",
                "Lower behind head for more stretch",
                "Use EZ bar to reduce wrist strain",
                "Control the weight carefully"
            ),
            difficulty = "Intermediate",
            equipment = "Barbell or EZ Bar, Bench",
            secondaryMuscles = listOf(),
            imageUrl = "ex_skull_crushers"
        ),

        // ==================== SHOULDERS (4 exercises) ====================
        Exercise(
            id = 21,
            name = "Overhead Press",
            sets = 4,
            reps = "8-10",
            targetMuscle = "Shoulders",
            description = "Best exercise for building strong, powerful shoulders.",
            instructions = listOf(
                "Stand with feet shoulder-width",
                "Hold bar at shoulder height",
                "Brace core and squeeze glutes",
                "Press bar straight up overhead",
                "Move head back slightly to let bar pass",
                "Lock out arms at top",
                "Lower with control to shoulders"
            ),
            tips = listOf(
                "Don't arch back excessively",
                "Keep core tight throughout",
                "Press slightly back, not straight up",
                "Tuck chin as bar passes"
            ),
            difficulty = "Intermediate",
            equipment = "Barbell",
            secondaryMuscles = listOf("Triceps", "Upper Chest", "Core"),
            imageUrl = "ex_overhead_press"
        ),

        Exercise(
            id = 22,
            name = "Lateral Raises",
            sets = 3,
            reps = "12-15",
            targetMuscle = "Shoulders",
            description = "Isolation exercise for building wide, round shoulders.",
            instructions = listOf(
                "Stand holding dumbbells at sides",
                "Keep slight bend in elbows",
                "Raise arms out to sides",
                "Stop when dumbbells reach shoulder height",
                "Lower slowly to starting position",
                "Lead with elbows, not hands"
            ),
            tips = listOf(
                "Don't use too much weight",
                "Keep elbows slightly bent",
                "Don't raise above shoulder level",
                "Control the negative portion"
            ),
            difficulty = "Beginner",
            equipment = "Dumbbells",
            secondaryMuscles = listOf("Traps"),
            imageUrl = "ex_lateral_raises"
        ),

        Exercise(
            id = 23,
            name = "Front Raises",
            sets = 3,
            reps = "12-15",
            targetMuscle = "Shoulders",
            description = "Targets front deltoids for fuller shoulder development.",
            instructions = listOf(
                "Stand holding dumbbells in front of thighs",
                "Keep slight bend in elbows",
                "Raise arms straight forward",
                "Stop when dumbbells reach eye level",
                "Lower slowly to starting position",
                "Alternate arms or do both together"
            ),
            tips = listOf(
                "Don't swing the weights",
                "Keep core engaged",
                "Don't raise above eye level",
                "Use lighter weight than lateral raises"
            ),
            difficulty = "Beginner",
            equipment = "Dumbbells",
            secondaryMuscles = listOf("Upper Chest"),
            imageUrl = "ex_front_raises"
        ),

        Exercise(
            id = 24,
            name = "Rear Delt Flyes",
            sets = 3,
            reps = "12-15",
            targetMuscle = "Shoulders",
            description = "Isolates rear deltoids for complete shoulder development.",
            instructions = listOf(
                "Bend forward at hips with back straight",
                "Hold dumbbells with arms hanging down",
                "Raise dumbbells out to sides",
                "Squeeze shoulder blades together",
                "Lower slowly to starting position",
                "Keep slight bend in elbows"
            ),
            tips = listOf(
                "Focus on rear delts, not back",
                "Don't use momentum",
                "Keep torso still",
                "Can also do on incline bench"
            ),
            difficulty = "Beginner",
            equipment = "Dumbbells",
            secondaryMuscles = listOf("Upper Back", "Traps"),
            imageUrl = "ex_rear_delt_flyes"
        ),

        // ==================== CORE (4 exercises) ====================
        Exercise(
            id = 25,
            name = "Plank",
            sets = 3,
            reps = "60s",
            targetMuscle = "Core",
            description = "Best exercise for core stability and overall strength.",
            instructions = listOf(
                "Start in push-up position on forearms",
                "Keep body straight from head to heels",
                "Engage core and squeeze glutes",
                "Hold position for time",
                "Don't let hips sag or pike up",
                "Breathe normally throughout"
            ),
            tips = listOf(
                "Focus on quality over time",
                "Keep elbows under shoulders",
                "Engage entire core",
                "Start with shorter holds if needed"
            ),
            difficulty = "Beginner",
            equipment = "Bodyweight",
            secondaryMuscles = listOf("Shoulders", "Glutes"),
            imageUrl = "ex_plank"
        ),

        Exercise(
            id = 26,
            name = "Crunches",
            sets = 3,
            reps = "15-20",
            targetMuscle = "Core",
            description = "Classic ab exercise for upper ab development.",
            instructions = listOf(
                "Lie on back with knees bent",
                "Place hands behind head or across chest",
                "Curl shoulders off ground toward hips",
                "Focus on contracting abs",
                "Lower slowly back down",
                "Don't pull on neck"
            ),
            tips = listOf(
                "Don't pull on your neck",
                "Lead with chest, not head",
                "Focus on ab contraction",
                "Exhale as you crunch up"
            ),
            difficulty = "Beginner",
            equipment = "Bodyweight",
            secondaryMuscles = listOf(),
            imageUrl = "ex_crunches"
        ),

        Exercise(
            id = 27,
            name = "Russian Twists",
            sets = 3,
            reps = "20-30",
            targetMuscle = "Core",
            description = "Targets obliques for a defined, athletic midsection.",
            instructions = listOf(
                "Sit on floor with knees bent",
                "Lean back slightly and lift feet",
                "Hold weight or medicine ball",
                "Rotate torso side to side",
                "Touch weight to ground each side",
                "Keep core engaged throughout"
            ),
            tips = listOf(
                "Move from the core, not arms",
                "Keep chest up",
                "Control the rotation",
                "Feet on ground for easier version"
            ),
            difficulty = "Intermediate",
            equipment = "Medicine Ball or Weight Plate",
            secondaryMuscles = listOf("Hip Flexors"),
            imageUrl = "ex_russian_twists"
        ),

        Exercise(
            id = 28,
            name = "Leg Raises",
            sets = 3,
            reps = "12-15",
            targetMuscle = "Core",
            description = "Targets lower abs for complete core development.",
            instructions = listOf(
                "Lie flat on back with legs extended",
                "Place hands under glutes for support",
                "Raise legs up to 90 degrees",
                "Keep legs straight or slightly bent",
                "Lower slowly without touching floor",
                "Keep lower back pressed to ground"
            ),
            tips = listOf(
                "Don't let lower back arch",
                "Control the lowering phase",
                "Stop before legs touch ground",
                "Bend knees for easier version"
            ),
            difficulty = "Intermediate",
            equipment = "Bodyweight",
            secondaryMuscles = listOf("Hip Flexors"),
            imageUrl = "ex_leg_raises"
        )
    )

    fun getExercisesByMuscle(muscle: String): List<Exercise> {
        return if (muscle == "All") {
            getAllExercises()
        } else {
            getAllExercises().filter { it.targetMuscle == muscle }
        }
    }

    fun getExerciseById(id: Int): Exercise? {
        return getAllExercises().find { it.id == id }
    }
}