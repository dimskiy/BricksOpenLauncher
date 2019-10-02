package `in`.evilcorp.bricksopenlauncher.repository

import `in`.evilcorp.bricksopenlauncher.LauncherApp
import `in`.evilcorp.bricksopenlauncher.R
import `in`.evilcorp.bricksopenlauncher.overlaycontrols.dependency.OverlayControlsNotificationManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import javax.inject.Inject

class SystemNotificationRepository
@Inject constructor(private val app: LauncherApp) : OverlayControlsNotificationManager {

    companion object {
        private const val SINGLE_NOTIFY_CHN_ID = "bricksopenlauncher.app:service_notify"
        private const val SINGLE_NOTIFY_ID = 1
    }

    private val singleNotification: Notification by lazy {
        val channelName = app.resources.getString(R.string.service_notify_label)
        buildNotification(channelName, SINGLE_NOTIFY_CHN_ID)
    }

    fun getHelperServiceNotificationId() = SINGLE_NOTIFY_ID

    fun getHelperServiceNotification(): Notification = singleNotification

    override fun getOverlayControlsNotificationId() = SINGLE_NOTIFY_ID

    override fun getOverlayControlsNotification(): Notification = singleNotification

    @Suppress("SameParameterValue")
    private fun buildNotification(channelName: String, channelId: String): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

            val notificationManager = app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(app, channelId)
                .setSmallIcon(R.drawable.ic_all_apps)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build()
    }
}