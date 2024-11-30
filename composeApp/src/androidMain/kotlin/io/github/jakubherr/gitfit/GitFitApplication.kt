package io.github.jakubherr.gitfit

import android.app.Application
import io.github.jakubherr.gitfit.di.initKoin
import org.koin.android.ext.koin.androidContext

class GitFitApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin { androidContext(this@GitFitApplication) }
    }
}
