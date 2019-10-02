package `in`.evilcorp.bricksopenlauncher.injection.modules

import `in`.evilcorp.bricksopenlauncher.overlaycontrols.ControlsService
import `in`.evilcorp.bricksopenlauncher.overlaycontrols.PermissionsActivity
import `in`.evilcorp.bricksopenlauncher.overlaycontrols.dependency.OverlayControlsNotificationManager
import `in`.evilcorp.bricksopenlauncher.repository.SystemNotificationRepository
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [AppModule::class, OverlayControlsModule::class])
abstract class InjectorBindOverlayModule {

    @Binds
    abstract fun bindNotificationManager(manager: SystemNotificationRepository): OverlayControlsNotificationManager

    @ContributesAndroidInjector
    abstract fun contributeHelperService(): ControlsService

    @ContributesAndroidInjector
    abstract fun contributePermissionsActivity(): PermissionsActivity
}