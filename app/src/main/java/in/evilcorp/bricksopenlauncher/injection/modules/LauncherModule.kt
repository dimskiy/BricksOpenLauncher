package `in`.evilcorp.bricksopenlauncher.injection.modules

import `in`.evilcorp.bricksopenlauncher.interactors.ShortcutsInteractor
import `in`.evilcorp.bricksopenlauncher.presentation.shortcuts_view.LauncherPresenter
import `in`.evilcorp.bricksopenlauncher.repository.PreferencesRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ AppModule::class, InteractorsModule::class, OverlayControlsModule::class ])
class LauncherModule {

    @Provides
    @Singleton
    fun provideLauncherPresenter(interactor: ShortcutsInteractor, prefs: PreferencesRepository): LauncherPresenter {
        return LauncherPresenter(interactor, prefs)
    }
}