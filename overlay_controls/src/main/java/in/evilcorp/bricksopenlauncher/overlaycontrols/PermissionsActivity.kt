package `in`.evilcorp.bricksopenlauncher.overlaycontrols

import `in`.evilcorp.bricksopenlauncher.overlaycontrols.dependency.OverlayControlsPreferenceManager
import `in`.evilcorp.bricksopenlauncher.overlaycontrols.helpers.WorkaroundOverlayCheckPre27Api
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_permissions.*
import timber.log.Timber
import javax.inject.Inject

class PermissionsActivity : DaggerAppCompatActivity() {
    companion object {
        fun launch(ctx: Context) {
            val intent = Intent(ctx, PermissionsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ctx.startActivity(intent)
        }
    }

    @Inject
    lateinit var prefsHelper: OverlayControlsPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.d("Permission requesting activity called.")

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
            val workaroundOverlayChecker = WorkaroundOverlayCheckPre27Api(application)
            if (workaroundOverlayChecker.deepOverlayPermissionsCheck()) {
                finish()
                return
            }
        }

        setContentView(R.layout.activity_permissions)
    }

    override fun onResume() {
        super.onResume()

        AlertDialog.Builder(this)
                .setTitle(R.string.permissions_dialog_title)
                .setMessage(R.string.permissions_dialog_description)
                .setPositiveButton(R.string.btn_open_settings) { _, _ -> openOverlayPermissionsToggleUi() }
                .setNegativeButton(android.R.string.cancel){ _, _ -> overlayPermissionDeclined() }
                .setOnCancelListener { overlayPermissionDeclined() }
                .show()
    }

    @SuppressLint("InlinedApi")
    private fun openOverlayPermissionsToggleUi() {
        val packageUri = Uri.parse("package:${packageName}")
        val permissionSettings = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, packageUri)

        if (permissionSettings.isResolvable(this)) {
            startActivity(permissionSettings)
        } else {
            Snackbar
                    .make(content_holder, R.string.cannot_open_system_settings, Snackbar.LENGTH_LONG)
                    .show()
        }
    }

    private fun overlayPermissionDeclined() {
        Timber.d("User refused request for overlay permissions.")
        prefsHelper.setOverlayControlsState(false)

        val goBackHome = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        if (goBackHome.isResolvable(this)) {
            startActivity(goBackHome)
        } else {
            finish()
        }
    }
}