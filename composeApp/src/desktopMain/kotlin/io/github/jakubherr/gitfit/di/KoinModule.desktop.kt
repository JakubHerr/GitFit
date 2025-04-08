package io.github.jakubherr.gitfit.di

import io.github.jakubherr.gitfit.presentation.Localization
import org.koin.dsl.module

actual val platformModule =
    module {
        single { Localization() }
    }
