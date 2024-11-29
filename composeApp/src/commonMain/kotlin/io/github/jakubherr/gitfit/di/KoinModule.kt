package io.github.jakubherr.gitfit.di

import androidx.compose.runtime.Composable
import io.github.jakubherr.gitfit.data.Supabase
import io.github.jakubherr.gitfit.presentation.AuthViewModel
import org.koin.compose.KoinApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

private val apiModule = module {
    single { Supabase() }
}

private val repositoryModule = module {

}

private val viewmodelModule = module {
    viewModel { AuthViewModel(get()) }
}

private val sharedModules = listOf(viewmodelModule, repositoryModule, apiModule)

@Composable
fun initKoin(content: @Composable () -> Unit) = KoinApplication(application = { modules(sharedModules) }, content)
