package `in`.evilcorp.bricksopenlauncher.overlaycontrols.dependency

import android.app.Notification

interface OverlayControlsNotificationManager {
    fun getOverlayControlsNotificationId(): Int
    fun getOverlayControlsNotification(): Notification
}

interface OverlayControlsPreferenceManager {
    fun setOverlayControlsState(enabled: Boolean)
    fun getOverlayControlsState(): Boolean
}