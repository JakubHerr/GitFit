package io.github.jakubherr.gitfit.di

import io.github.jakubherr.gitfit.data.repository.FirebaseAuthRepository
import io.github.jakubherr.gitfit.data.repository.FirestoreExerciseRepository
import io.github.jakubherr.gitfit.data.repository.FirestoreMeasurementRepository
import io.github.jakubherr.gitfit.data.repository.FirestoreWorkoutRepository
import io.github.jakubherr.gitfit.domain.ExerciseRepository
import io.github.jakubherr.gitfit.domain.MeasurementRepository
import io.github.jakubherr.gitfit.domain.WorkoutRepository
import io.github.jakubherr.gitfit.presentation.auth.AuthViewModel
import io.github.jakubherr.gitfit.presentation.exercise.ExerciseViewModel
import io.github.jakubherr.gitfit.presentation.measurement.MeasurementViewModel
import io.github.jakubherr.gitfit.presentation.workout.WorkoutViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

private val apiModule =
    module {
    }

private val repositoryModule =
    module {
        singleOf(::FirestoreExerciseRepository).bind<ExerciseRepository>()
        singleOf(::FirestoreWorkoutRepository).bind<WorkoutRepository>()
        singleOf(::FirebaseAuthRepository).bind<FirebaseAuthRepository>()
        singleOf(::FirestoreMeasurementRepository).bind<MeasurementRepository>()
    }

expect val platformModule: Module

private val viewmodelModule =
    module {
        viewModelOf(::AuthViewModel)
        viewModelOf(::WorkoutViewModel)
        viewModelOf(::ExerciseViewModel)
        viewModelOf(::MeasurementViewModel)
    }

private val sharedModules = listOf(viewmodelModule, repositoryModule, apiModule)

fun initKoin(config: KoinAppDeclaration? = null) =
    startKoin {
        config?.invoke(this)
        modules(sharedModules)
        modules(platformModule)
    }
