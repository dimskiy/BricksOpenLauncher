package `in`.evilcorp.bricksopenlauncher.overlaycontrols

import `in`.evilcorp.bricksopenlauncher.overlaycontrols.dependency.OverlayControlsManager
import `in`.evilcorp.bricksopenlauncher.overlaycontrols.dependency.OverlayControlsPreferenceManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import javax.inject.Inject


class OverlayControlsManagerImpl
@Inject constructor(
        private val appCtx: Context,
        private val prefsHelper: OverlayControlsPreferenceManager) : OverlayControlsManager {

    private var serviceActionHandler: ((String) -> Unit)? = null

    override fun prepareOverlayControlsService() {
        serviceActionHandler = { ControlsService.start(appCtx, it) }
    }

    override fun hideOverlayControls() {
        if (isFeatureReady()) {
            serviceActionHandler?.invoke(ControlsService.INTENT_ACTION_HIDE_CONTROLS)
        }
    }

    override fun showOverlayControls() {
        if (isFeatureReady()) {
            serviceActionHandler?.invoke(ControlsService.INTENT_ACTION_SHOW_CONTROLS)
        }
    }

    private fun isFeatureReady(): Boolean {
        val pendingEnabled = prefsHelper.getOverlayControlsState()

        return if (pendingEnabled && permissionsGranted()) {
            true
        } else if (pendingEnabled) {
            PermissionsActivity.launch(appCtx)
            false
        } else {
            false
        }
    }

    private fun permissionsGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(appCtx)
        } else {
            true
        }
    }

    override fun stopOverlayControlsService() {
        serviceActionHandler?.invoke(ControlsService.INTENT_ACTION_TERMINATE)
        serviceActionHandler = null
    }
}