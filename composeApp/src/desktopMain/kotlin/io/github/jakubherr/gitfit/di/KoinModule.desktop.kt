package io.github.jakubherr.gitfit.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.jakubherr.gitfit.db.LocalDatabase
import org.koin.dsl.module

actual val platformModule = module {
    single { LocalDatabase(createDriver()) }
}

private fun createDriver(): SqlDriver {
    val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:test.db")
    LocalDatabase.Schema.create(driver)
    return driver
}
