package `in`.evilcorp.bricksopenlauncher

import `in`.evilcorp.bricksopenlauncher.injection.DaggerAppComponent
import `in`.evilcorp.bricksopenlauncher.injection.modules.AppModule
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

fun Intent.isResolvable(ctx: Context?): Boolean {
    val pm = ctx?.packageManager
    val appInfo = pm?.resolveActivity(this, PackageManager.MATCH_DEFAULT_ONLY)
    return appInfo != null
}

class LauncherApp : DaggerApplication() {
    companion object {
        private const val DEBUG_LOG_PREFIX = "LauncherApp>>"
    }

    private val appInjector = DaggerAppComponent.builder()
            .application(this)
            .appModule(AppModule(this))
            .build()

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = appInjector

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(TimberPrefixTagTree())
        }
    }

    private inner class TimberPrefixTagTree: Timber.DebugTree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            val tagPrefixed = "$DEBUG_LOG_PREFIX $tag"
            super.log(priority, tagPrefixed, message, t)
        }
    }
}