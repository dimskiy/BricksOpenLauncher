package `in`.evilcorp.bricksopenlauncher.injection.modules

import `in`.evilcorp.bricksopenlauncher.LauncherApp
import `in`.evilcorp.bricksopenlauncher.overlaycontrols.OverlayControlsManagerImpl
import `in`.evilcorp.bricksopenlauncher.overlaycontrols.dependency.OverlayControlsManager
import `in`.evilcorp.bricksopenlauncher.overlaycontrols.dependency.OverlayControlsPreferenceManager
import `in`.evilcorp.bricksopenlauncher.repository.PreferencesRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module (includes = [ AppModule::class ])
class OverlayControlsModule {

    @Provides
    @Singleton
    fun providePrefsManager(prefsHelper: PreferencesRepository): OverlayControlsPreferenceManager {
        return object : OverlayControlsPreferenceManager {
            override fun setOverlayControlsState(enabled: Boolean) {
                prefsHelper.overlayControlsFeatureState = enabled
            }
            override fun getOverlayControlsState() = prefsHelper.overlayControlsFeatureState
        }
    }

    @Provides
    @Singleton
    fun provideOverlayControlsManager(app: LauncherApp, prefsManager: OverlayControlsPreferenceManager): OverlayControlsManager {
        return OverlayControlsManagerImpl(app.applicationContext, prefsManager)
    }
}