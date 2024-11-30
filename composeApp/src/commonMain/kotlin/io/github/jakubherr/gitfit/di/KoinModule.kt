package io.github.jakubherr.gitfit.di

import io.github.jakubherr.gitfit.data.Supabase
import io.github.jakubherr.gitfit.presentation.AuthViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

private val apiModule = module {
    single { Supabase() }
}

private val repositoryModule = module {

}

expect val platformModule: Module

private val viewmodelModule = module {
    viewModel { AuthViewModel(get()) }
}

private val sharedModules = listOf(viewmodelModule, repositoryModule, apiModule)

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)
    modules(sharedModules)
    modules(platformModule)
}
