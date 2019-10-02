package `in`.evilcorp.bricksopenlauncher.injection.modules

import `in`.evilcorp.bricksopenlauncher.LauncherApp
import `in`.evilcorp.bricksopenlauncher.repository.PreferencesRepository
import `in`.evilcorp.bricksopenlauncher.repository.SystemNotificationRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: LauncherApp) {

    @Provides
    @Singleton
    fun providePreferencesRepo() = PreferencesRepository(app)

    @Provides
    @Singleton
    fun provideSystemNotifyManager() = SystemNotificationRepository(app)
}