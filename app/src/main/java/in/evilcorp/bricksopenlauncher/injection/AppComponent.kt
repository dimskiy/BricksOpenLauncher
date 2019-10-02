package `in`.evilcorp.bricksopenlauncher.injection

import `in`.evilcorp.bricksopenlauncher.LauncherApp
import `in`.evilcorp.bricksopenlauncher.injection.modules.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Component(modules = [
    AndroidSupportInjectionModule::class,
    InjectorBindMainModule::class,
    InjectorBindOverlayModule::class,
    AppModule::class,
    OverlayControlsModule::class,
    StorageModule::class
])
@Singleton
interface AppComponent: AndroidInjector<LauncherApp> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: LauncherApp): Builder
        fun appModule(appModule: AppModule): Builder
        fun build(): AppComponent
    }
}