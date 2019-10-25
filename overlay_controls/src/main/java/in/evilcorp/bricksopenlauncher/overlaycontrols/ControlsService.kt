package `in`.evilcorp.bricksopenlauncher.overlaycontrols

import `in`.evilcorp.bricksopenlauncher.overlaycontrols.dependency.OverlayControlsNotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import dagger.android.DaggerService
import kotlinx.android.synthetic.main.layout_overlay_controls.view.*
import timber.log.Timber
import javax.inject.Inject

fun Intent.isResolvable(ctx: Context?): Boolean {
    val pm = ctx?.packageManager
    val appInfo = pm?.resolveActivity(this, PackageManager.MATCH_DEFAULT_ONLY)
    return appInfo != null
}

class ControlsService : DaggerService() {
    companion object {
        const val INTENT_ACTION_SHOW_CONTROLS = "bricksopenlauncher.control_service:show_controls"
        const val INTENT_ACTION_HIDE_CONTROLS = "bricksopenlauncher.controls_service:hide_controls"
        const val INTENT_ACTION_TERMINATE = "bricksopenlauncher.controls_service:stop_overlay_service"

        private const val CONTROLS_FADE_DURATION_MS = 200L

        private var serviceStarted = false

        fun start(ctx: Context, intentAction: String? = null) {
            val intent = Intent(ctx, ControlsService::class.java)
            intentAction?.let { intent.action = it }
            ContextCompat.startForegroundService(ctx, intent)
        }

        fun stop(ctx: Context) {
            if (serviceStarted) {
                val intent = Intent(ctx, ControlsService::class.java)
                ctx.stopService(intent)
                serviceStarted = false
                Timber.d("Stopping ControlsService...")
            } else {
                Timber.d("Called ControlsService STOP before it finished launching -> skipping")
            }
        }
    }

    private var overlayControls: View? = null

    @Inject
    lateinit var notifyManager: OverlayControlsNotificationManager

    override fun onBind(intent: Intent) = Binder()

    override fun onCreate() {
        super.onCreate()

        startForeground(notifyManager.getOverlayControlsNotificationId(), notifyManager.getOverlayControlsNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceStarted = true

        intent?.let {
            when (it.action) {
                INTENT_ACTION_SHOW_CONTROLS -> showControls()
                INTENT_ACTION_HIDE_CONTROLS -> hideControls()
                INTENT_ACTION_TERMINATE -> stop(this)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun showControls() {
        if (overlayControls == null) {
            addOverlayControls()
        } else {
            overlayControls?.animate()?.alpha(1.0f)?.setDuration(CONTROLS_FADE_DURATION_MS)?.start()
        }
    }

    private fun addOverlayControls() {
        val viewType = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            TYPE_SYSTEM_ALERT
        } else {
            TYPE_APPLICATION_OVERLAY
        }

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val viewParams = WindowManager.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                viewType,
                FLAG_NOT_TOUCH_MODAL or FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        )
        viewParams.gravity = Gravity.START or Gravity.BOTTOM

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        overlayControls = inflater.inflate(R.layout.layout_overlay_controls, null)
        overlayControls?.let {
            it.btn_center.setOnClickListener { callHomeScreen() }
            windowManager?.addView(it, viewParams)
        }
    }

    private fun callHomeScreen() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (intent.isResolvable(this)) {
            startActivity(intent)
        }
    }

    private fun hideControls() {
        overlayControls?.animate()?.alpha(0.0f)?.setDuration(CONTROLS_FADE_DURATION_MS)?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeControls()
    }

    private fun removeControls() {
        overlayControls?.let {
            val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
            windowManager?.removeView(it)
        }

        overlayControls = null
    }
}
