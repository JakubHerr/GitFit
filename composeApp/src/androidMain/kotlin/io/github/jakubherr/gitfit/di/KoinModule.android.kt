package io.github.jakubherr.gitfit.di

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.github.jakubherr.gitfit.db.LocalDatabase
import org.koin.core.scope.Scope
import org.koin.dsl.module

actual val platformModule = module {
    single { LocalDatabase(createDriver()) }
}

private fun Scope.createDriver() = AndroidSqliteDriver(LocalDatabase.Schema, get(), "test.db")