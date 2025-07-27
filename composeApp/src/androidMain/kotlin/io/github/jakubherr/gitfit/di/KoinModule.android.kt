package io.github.jakubherr.gitfit.di

import io.github.jakubherr.gitfit.presentation.NotificationHandler
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule =
    module {
        singleOf(::NotificationHandler)
    }
