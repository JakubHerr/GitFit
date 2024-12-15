package io.github.jakubherr.gitfit.di

import io.github.jakubherr.gitfit.data.Supabase
import io.github.jakubherr.gitfit.data.repository.ExerciseRepositoryImpl
import io.github.jakubherr.gitfit.data.repository.WorkoutRepositoryImpl
import io.github.jakubherr.gitfit.data.source.ExerciseDataSource
import io.github.jakubherr.gitfit.data.source.LocalExerciseDataSource
import io.github.jakubherr.gitfit.data.source.LocalWorkoutDataSource
import io.github.jakubherr.gitfit.domain.ExerciseRepository
import io.github.jakubherr.gitfit.domain.WorkoutRepository
import io.github.jakubherr.gitfit.presentation.auth.AuthViewModel
import io.github.jakubherr.gitfit.presentation.exercise.ExerciseViewModel
import io.github.jakubherr.gitfit.presentation.workout.WorkoutViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

private val apiModule = module {
    single { Supabase() }
}

private val repositoryModule = module {
    singleOf(::LocalExerciseDataSource).bind<ExerciseDataSource>()
    singleOf(::LocalWorkoutDataSource)

    singleOf(::ExerciseRepositoryImpl).bind<ExerciseRepository>()
    singleOf(::WorkoutRepositoryImpl).bind<WorkoutRepository>()
}

expect val platformModule: Module

private val viewmodelModule = module {
    viewModelOf(::AuthViewModel)
    viewModel { WorkoutViewModel(get()) }
    viewModel { ExerciseViewModel(get()) }
}

private val sharedModules = listOf(viewmodelModule, repositoryModule, apiModule)

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)
    modules(sharedModules)
    modules(platformModule)
}
