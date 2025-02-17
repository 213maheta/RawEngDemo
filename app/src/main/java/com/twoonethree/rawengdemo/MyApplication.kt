package com.twoonethree.rawengdemo

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication) // Provides the context to Koin
            modules(appModule)
        }
    }
}