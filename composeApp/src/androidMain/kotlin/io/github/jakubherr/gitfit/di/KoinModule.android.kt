package io.github.jakubherr.gitfit.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule =
    module {
        single { Localization(context = androidContext()) }
    }
