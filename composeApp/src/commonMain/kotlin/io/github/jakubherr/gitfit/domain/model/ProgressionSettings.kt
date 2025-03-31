package io.github.jakubherr.gitfit.domain.model

data class ProgressionSettings(
    val incrementWeightByKg: Double,
    val incrementReps: Int,
    val type: ProgressionType,
    val trigger: ProgressionTrigger,
    val threshold: Int,
)

enum class ProgressionType {
    INCREASE_WEIGHT,
    INCREASE_REPS,
}

// increase every workout is a bad idea
enum class ProgressionTrigger {
    MINIMUM_REPS_EVERY_SET,
}

// a workout can progress by EITHER increasing reps OR adding weight
// progression MUST have a goal/threshold that will trigger the increase
    // the most basic threshold: minimum weight AND minimum reps every set
// the most basic trigger is a minimum number of repetitions every set
// progression logic must take into account both the type and trigger
    // if progression is increase reps, the minimum number of reps should be also increased
    // if progression is increase weight, the minimum weight for the next progression should increase by increment
// the progression should define a starting weight and reps