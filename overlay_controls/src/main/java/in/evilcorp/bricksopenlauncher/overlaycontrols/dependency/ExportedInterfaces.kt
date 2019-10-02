package `in`.evilcorp.bricksopenlauncher.overlaycontrols.dependency

interface OverlayControlsManager {
    fun hideOverlayControls()
    fun showOverlayControls()
    fun prepareOverlayControlsService()
    fun stopOverlayControlsService()
}