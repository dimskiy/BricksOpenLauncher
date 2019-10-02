package `in`.evilcorp.bricksopenlauncher.overlaycontrols.helpers

import android.app.Application
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import timber.log.Timber

//Used as workaround for Android 8 bug that cause 'Settings.canDrawOverlays()' return 'false' even if permission granted.
//https://issuetracker.google.com/issues/66072795

class WorkaroundOverlayCheckPre27Api(private val app: Application) {
    fun deepOverlayPermissionsCheck(): Boolean {
        Timber.d("Starting RealOverlayPermissionsCheck because of Android API version = ${Build.VERSION.SDK_INT}")
        val testView = View(app)

        return try {
            drawOverlayView(testView)
            Timber.d("RealOverlayPermissionsCheck => OK")
            removeOverlayView(testView)
            true

        } catch (e: Exception) {
            Timber.d("RealOverlayPermissionsCheck => FAILED: ${e.message}")
            false

        }
    }

    @Throws(Exception::class)
    private fun drawOverlayView(testView: View) {
        val viewType = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            (WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        } else {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }

        val viewParams = WindowManager.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                viewType,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        )

        val windowManager = app.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        windowManager?.addView(testView, viewParams)
    }

    private fun removeOverlayView(testView: View) {
        val windowManager = app.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        windowManager?.removeView(testView)
    }
}