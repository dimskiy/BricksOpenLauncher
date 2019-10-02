package `in`.evilcorp.bricksopenlauncher.service

import `in`.evilcorp.bricksopenlauncher.RxLifecycle
import `in`.evilcorp.bricksopenlauncher.RxLifecycleDelegate
import `in`.evilcorp.bricksopenlauncher.presentation.MainActivity
import `in`.evilcorp.bricksopenlauncher.repository.PreferencesRepository
import `in`.evilcorp.bricksopenlauncher.repository.SystemNotificationRepository
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.PowerManager
import dagger.android.DaggerService
import timber.log.Timber
import javax.inject.Inject

class HelperService: DaggerService(), RxLifecycle by RxLifecycleDelegate() {

    companion object {
        private const val INTENT_SYS_USB_DISCONNECT = Intent.ACTION_POWER_DISCONNECTED
        private const val WAKE_LOCK_TAG = "bricksopenlauncher.app:keep_screen_on"
    }

    @Inject
    lateinit var prefsHelper: PreferencesRepository

    @Inject
    lateinit var sysNotifyRepo: SystemNotificationRepository

    private val genericReceiver = GenericReceiver()
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent?) = Binder()

    override fun onCreate() {
        super.onCreate()

        val notificationId = sysNotifyRepo.getHelperServiceNotificationId()
        val notification = sysNotifyRepo.getHelperServiceNotification()
        startForeground(notificationId, notification)

        subscribeUsbBroadcast()
        subscribeWakeLockToggle()
        Timber.d("Service created")
    }

    private fun subscribeUsbBroadcast() {
        val intentFilter = IntentFilter(INTENT_SYS_USB_DISCONNECT)
        registerReceiver(genericReceiver, intentFilter)
    }

    private fun subscribeWakeLockToggle() {
        disposer = prefsHelper.keepScreenOnObservable
                .distinctUntilChanged()
                .doFinally { toggleWakeLock(false) }
                .subscribe(this::toggleWakeLock)
    }

    @Suppress("DEPRECATION")
    @SuppressLint("WakelockTimeout")
    private fun toggleWakeLock(hold: Boolean) {
        if (hold) {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, WAKE_LOCK_TAG)
            wakeLock?.acquire()
            Timber.d("Wakelock acquired")

        } else if (wakeLock?.isHeld == true){
            wakeLock?.release()
            Timber.d("Wakelock released")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeSubscriptions()
        unregisterReceiver(genericReceiver)
        Timber.d("Service destroyed")
    }

    private fun handleUsbDisconnect() {
        if (prefsHelper.exitOnUsbDisconnect) {
            MainActivity.terminate(this)
        }
    }

    private inner class GenericReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.d("Intent incoming: ${intent?.action}")
            if (intent?.action == INTENT_SYS_USB_DISCONNECT) {
                handleUsbDisconnect()
            }
        }
    }
}