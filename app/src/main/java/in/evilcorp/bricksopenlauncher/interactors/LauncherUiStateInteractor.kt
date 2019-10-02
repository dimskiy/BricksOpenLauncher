package `in`.evilcorp.bricksopenlauncher.interactors

import `in`.evilcorp.bricksopenlauncher.overlaycontrols.dependency.OverlayControlsManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LauncherUiStateInteractor
@Inject constructor(private val overlayControlsManager: OverlayControlsManager){

    fun onLauncherUiCreated() {
        overlayControlsManager.prepareOverlayControlsService()
    }

    fun onLauncherUiVisible() {
        overlayControlsManager.hideOverlayControls()
    }

    fun onLauncherUiInvisible() {
        overlayControlsManager.showOverlayControls()
    }

    fun onLauncherUiTerminate() {
        overlayControlsManager.stopOverlayControlsService()
    }
}