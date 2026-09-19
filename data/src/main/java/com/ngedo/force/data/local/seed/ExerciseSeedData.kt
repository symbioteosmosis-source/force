package com.ngedo.force.data.local.seed

import com.ngedo.force.data.local.entity.ExerciseEntity

object ExerciseSeedData {

    val exercises =
        listOf(

            // CHEST

            ExerciseEntity(
                id = "barbell_bench_press",
                name = "Barbell Bench Press",
                primaryMuscle = "Chest",
                secondaryMuscles = "Triceps, Front Delts",
                equipment = "Barbell",
                movementType = "Push",
                difficulty = "Intermediate",
                instructions = "Lower the bar under control to the chest, then press upward.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "incline_barbell_bench_press",
                name = "Incline Barbell Bench Press",
                primaryMuscle = "Chest",
                secondaryMuscles = "Triceps, Front Delts",
                equipment = "Barbell",
                movementType = "Push",
                difficulty = "Intermediate",
                instructions = "Press the bar from an inclined bench position while keeping the upper back stable.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "dumbbell_bench_press",
                name = "Dumbbell Bench Press",
                primaryMuscle = "Chest",
                secondaryMuscles = "Triceps, Front Delts",
                equipment = "Dumbbell",
                movementType = "Push",
                difficulty = "Beginner",
                instructions = "Lower the dumbbells beside the chest and press them upward under control.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "incline_dumbbell_press",
                name = "Incline Dumbbell Press",
                primaryMuscle = "Chest",
                secondaryMuscles = "Triceps, Front Delts",
                equipment = "Dumbbell",
                movementType = "Push",
                difficulty = "Intermediate",
                instructions = "Press the dumbbells upward from an inclined bench while keeping the shoulders controlled.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "chest_press_machine",
                name = "Chest Press Machine",
                primaryMuscle = "Chest",
                secondaryMuscles = "Triceps, Front Delts",
                equipment = "Machine",
                movementType = "Push",
                difficulty = "Beginner",
                instructions = "Press the handles forward while keeping the back supported against the pad.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "cable_fly",
                name = "Cable Fly",
                primaryMuscle = "Chest",
                secondaryMuscles = "Front Delts",
                equipment = "Cable",
                movementType = "Isolation",
                difficulty = "Intermediate",
                instructions = "Bring the handles together in front of the chest with a controlled arc.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "push_up",
                name = "Push-Up",
                primaryMuscle = "Chest",
                secondaryMuscles = "Triceps, Front Delts, Core",
                equipment = "Bodyweight",
                movementType = "Push",
                difficulty = "Beginner",
                instructions = "Lower the body toward the floor while maintaining a straight torso, then press upward.",
                mediaPath = null,
                isCustom = false
            ),


            // BACK

            ExerciseEntity(
                id = "barbell_row",
                name = "Barbell Row",
                primaryMuscle = "Back",
                secondaryMuscles = "Biceps, Rear Delts",
                equipment = "Barbell",
                movementType = "Pull",
                difficulty = "Intermediate",
                instructions = "Pull the bar toward the torso while keeping the spine stable and controlled.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "lat_pulldown",
                name = "Lat Pulldown",
                primaryMuscle = "Back",
                secondaryMuscles = "Biceps, Rear Delts",
                equipment = "Cable",
                movementType = "Pull",
                difficulty = "Beginner",
                instructions = "Pull the bar toward the upper chest while keeping the torso controlled.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "seated_cable_row",
                name = "Seated Cable Row",
                primaryMuscle = "Back",
                secondaryMuscles = "Biceps, Rear Delts",
                equipment = "Cable",
                movementType = "Pull",
                difficulty = "Beginner",
                instructions = "Pull the handle toward the torso while keeping the chest upright.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "one_arm_dumbbell_row",
                name = "One-Arm Dumbbell Row",
                primaryMuscle = "Back",
                secondaryMuscles = "Biceps, Rear Delts",
                equipment = "Dumbbell",
                movementType = "Pull",
                difficulty = "Beginner",
                instructions = "Pull the dumbbell toward the hip while keeping the torso stable.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "pull_up",
                name = "Pull-Up",
                primaryMuscle = "Back",
                secondaryMuscles = "Biceps, Core",
                equipment = "Bodyweight",
                movementType = "Pull",
                difficulty = "Intermediate",
                instructions = "Pull the body upward until the chin approaches the bar, then lower under control.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "deadlift",
                name = "Deadlift",
                primaryMuscle = "Back",
                secondaryMuscles = "Glutes, Hamstrings, Core",
                equipment = "Barbell",
                movementType = "Hinge",
                difficulty = "Advanced",
                instructions = "Lift the bar from the floor by extending the hips and knees while maintaining a stable spine.",
                mediaPath = null,
                isCustom = false
            ),


            // SHOULDERS

            ExerciseEntity(
                id = "overhead_press",
                name = "Overhead Press",
                primaryMuscle = "Shoulders",
                secondaryMuscles = "Triceps, Upper Chest",
                equipment = "Barbell",
                movementType = "Push",
                difficulty = "Intermediate",
                instructions = "Press the bar overhead while maintaining a stable torso.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "dumbbell_shoulder_press",
                name = "Dumbbell Shoulder Press",
                primaryMuscle = "Shoulders",
                secondaryMuscles = "Triceps",
                equipment = "Dumbbell",
                movementType = "Push",
                difficulty = "Beginner",
                instructions = "Press the dumbbells overhead from shoulder height under control.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "lateral_raise",
                name = "Lateral Raise",
                primaryMuscle = "Shoulders",
                secondaryMuscles = "",
                equipment = "Dumbbell",
                movementType = "Isolation",
                difficulty = "Beginner",
                instructions = "Raise the dumbbells outward until the arms approach shoulder height.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "rear_delt_fly",
                name = "Rear Delt Fly",
                primaryMuscle = "Shoulders",
                secondaryMuscles = "Upper Back",
                equipment = "Dumbbell",
                movementType = "Isolation",
                difficulty = "Beginner",
                instructions = "Move the arms outward while keeping a slight elbow bend and targeting the rear shoulders.",
                mediaPath = null,
                isCustom = false
            ),


            // BICEPS

            ExerciseEntity(
                id = "barbell_curl",
                name = "Barbell Curl",
                primaryMuscle = "Biceps",
                secondaryMuscles = "Forearms",
                equipment = "Barbell",
                movementType = "Isolation",
                difficulty = "Beginner",
                instructions = "Curl the bar toward the shoulders while keeping the upper arms relatively still.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "dumbbell_curl",
                name = "Dumbbell Curl",
                primaryMuscle = "Biceps",
                secondaryMuscles = "Forearms",
                equipment = "Dumbbell",
                movementType = "Isolation",
                difficulty = "Beginner",
                instructions = "Curl the dumbbells upward while keeping the elbows controlled.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "hammer_curl",
                name = "Hammer Curl",
                primaryMuscle = "Biceps",
                secondaryMuscles = "Forearms",
                equipment = "Dumbbell",
                movementType = "Isolation",
                difficulty = "Beginner",
                instructions = "Curl the dumbbells with the palms facing inward throughout the movement.",
                mediaPath = null,
                isCustom = false
            ),


            // TRICEPS

            ExerciseEntity(
                id = "triceps_pushdown",
                name = "Triceps Pushdown",
                primaryMuscle = "Triceps",
                secondaryMuscles = "",
                equipment = "Cable",
                movementType = "Isolation",
                difficulty = "Beginner",
                instructions = "Extend the elbows and press the attachment downward while keeping the upper arms still.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "overhead_triceps_extension",
                name = "Overhead Triceps Extension",
                primaryMuscle = "Triceps",
                secondaryMuscles = "",
                equipment = "Dumbbell",
                movementType = "Isolation",
                difficulty = "Beginner",
                instructions = "Lower the dumbbell behind the head and extend the elbows to return overhead.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "close_grip_bench_press",
                name = "Close-Grip Bench Press",
                primaryMuscle = "Triceps",
                secondaryMuscles = "Chest, Front Delts",
                equipment = "Barbell",
                movementType = "Push",
                difficulty = "Intermediate",
                instructions = "Press the bar using a narrower grip while keeping the elbows controlled.",
                mediaPath = null,
                isCustom = false
            ),


            // LEGS

            ExerciseEntity(
                id = "barbell_back_squat",
                name = "Barbell Back Squat",
                primaryMuscle = "Legs",
                secondaryMuscles = "Glutes, Hamstrings, Core",
                equipment = "Barbell",
                movementType = "Squat",
                difficulty = "Intermediate",
                instructions = "Squat downward with control, then stand by extending the knees and hips.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "leg_press",
                name = "Leg Press",
                primaryMuscle = "Legs",
                secondaryMuscles = "Glutes, Hamstrings",
                equipment = "Machine",
                movementType = "Squat",
                difficulty = "Beginner",
                instructions = "Lower the platform under control and press it away through the feet.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "leg_extension",
                name = "Leg Extension",
                primaryMuscle = "Legs",
                secondaryMuscles = "",
                equipment = "Machine",
                movementType = "Isolation",
                difficulty = "Beginner",
                instructions = "Extend the knees until the legs are nearly straight, then lower under control.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "leg_curl",
                name = "Leg Curl",
                primaryMuscle = "Hamstrings",
                secondaryMuscles = "",
                equipment = "Machine",
                movementType = "Isolation",
                difficulty = "Beginner",
                instructions = "Curl the lower legs toward the body by flexing the knees.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "romanian_deadlift",
                name = "Romanian Deadlift",
                primaryMuscle = "Hamstrings",
                secondaryMuscles = "Glutes, Back",
                equipment = "Barbell",
                movementType = "Hinge",
                difficulty = "Intermediate",
                instructions = "Push the hips backward while lowering the bar, then extend the hips to return upright.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "walking_lunge",
                name = "Walking Lunge",
                primaryMuscle = "Legs",
                secondaryMuscles = "Glutes, Hamstrings",
                equipment = "Bodyweight",
                movementType = "Lunge",
                difficulty = "Beginner",
                instructions = "Step forward into a lunge and alternate legs while moving forward.",
                mediaPath = null,
                isCustom = false
            ),


            // GLUTES

            ExerciseEntity(
                id = "hip_thrust",
                name = "Hip Thrust",
                primaryMuscle = "Glutes",
                secondaryMuscles = "Hamstrings",
                equipment = "Barbell",
                movementType = "Hinge",
                difficulty = "Intermediate",
                instructions = "Drive the hips upward until the torso and thighs form a straight line.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "glute_bridge",
                name = "Glute Bridge",
                primaryMuscle = "Glutes",
                secondaryMuscles = "Hamstrings",
                equipment = "Bodyweight",
                movementType = "Hinge",
                difficulty = "Beginner",
                instructions = "Lift the hips from the floor by contracting the glutes.",
                mediaPath = null,
                isCustom = false
            ),


            // CALVES

            ExerciseEntity(
                id = "standing_calf_raise",
                name = "Standing Calf Raise",
                primaryMuscle = "Calves",
                secondaryMuscles = "",
                equipment = "Machine",
                movementType = "Isolation",
                difficulty = "Beginner",
                instructions = "Raise the heels as high as comfortable, then lower under control.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "seated_calf_raise",
                name = "Seated Calf Raise",
                primaryMuscle = "Calves",
                secondaryMuscles = "",
                equipment = "Machine",
                movementType = "Isolation",
                difficulty = "Beginner",
                instructions = "Raise and lower the heels while seated and keeping the knees stable.",
                mediaPath = null,
                isCustom = false
            ),


            // CORE

            ExerciseEntity(
                id = "plank",
                name = "Plank",
                primaryMuscle = "Core",
                secondaryMuscles = "Shoulders, Glutes",
                equipment = "Bodyweight",
                movementType = "Isometric",
                difficulty = "Beginner",
                instructions = "Maintain a straight body position while bracing the core.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "cable_crunch",
                name = "Cable Crunch",
                primaryMuscle = "Core",
                secondaryMuscles = "",
                equipment = "Cable",
                movementType = "Isolation",
                difficulty = "Intermediate",
                instructions = "Flex the torso downward against cable resistance while keeping the hips controlled.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "hanging_leg_raise",
                name = "Hanging Leg Raise",
                primaryMuscle = "Core",
                secondaryMuscles = "Hip Flexors",
                equipment = "Bodyweight",
                movementType = "Core",
                difficulty = "Intermediate",
                instructions = "Raise the legs while hanging from a bar without excessive swinging.",
                mediaPath = null,
                isCustom = false
            ),


            // CARDIO / FULL BODY

            ExerciseEntity(
                id = "burpee",
                name = "Burpee",
                primaryMuscle = "Full Body",
                secondaryMuscles = "Chest, Legs, Core",
                equipment = "Bodyweight",
                movementType = "Conditioning",
                difficulty = "Intermediate",
                instructions = "Move from standing to a floor position and return to standing with controlled speed.",
                mediaPath = null,
                isCustom = false
            ),

            ExerciseEntity(
                id = "treadmill_running",
                name = "Treadmill Running",
                primaryMuscle = "Cardio",
                secondaryMuscles = "Legs",
                equipment = "Machine",
                movementType = "Cardio",
                difficulty = "Beginner",
                instructions = "Run at an appropriate pace while maintaining controlled posture.",
                mediaPath = null,
                isCustom = false
            )
        )
}