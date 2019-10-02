package `in`.evilcorp.bricksopenlauncher.injection.modules

import `in`.evilcorp.bricksopenlauncher.interactors.LauncherUiStateInteractor
import `in`.evilcorp.bricksopenlauncher.interactors.ShortcutsInteractor
import `in`.evilcorp.bricksopenlauncher.overlaycontrols.dependency.OverlayControlsManager
import `in`.evilcorp.bricksopenlauncher.repository.ShortcutsRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [RepositoryModule::class, OverlayControlsModule::class])
class InteractorsModule {

    @Provides
    @Singleton
    fun provideShortcutsInteractor(shortcutsRepository: ShortcutsRepository): ShortcutsInteractor {
        return ShortcutsInteractor(shortcutsRepository)
    }

    @Provides
    @Singleton
    fun provideUiStateInteractor(overlaysManager: OverlayControlsManager): LauncherUiStateInteractor {
        return LauncherUiStateInteractor(overlaysManager)
    }
}