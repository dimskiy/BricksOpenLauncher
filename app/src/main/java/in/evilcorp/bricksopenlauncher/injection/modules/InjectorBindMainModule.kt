package `in`.evilcorp.bricksopenlauncher.injection.modules

import `in`.evilcorp.bricksopenlauncher.presentation.MainActivity
import `in`.evilcorp.bricksopenlauncher.presentation.shortcuts_view.LauncherFragment
import `in`.evilcorp.bricksopenlauncher.service.HelperService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [AppModule::class, LauncherModule::class, RepositoryModule::class])
abstract class InjectorBindMainModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector
    abstract fun contributeHelperService(): HelperService
}