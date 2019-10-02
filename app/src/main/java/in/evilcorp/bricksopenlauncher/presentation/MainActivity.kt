package `in`.evilcorp.bricksopenlauncher.presentation

import `in`.evilcorp.bricksopenlauncher.R
import `in`.evilcorp.bricksopenlauncher.interactors.LauncherUiStateInteractor
import `in`.evilcorp.bricksopenlauncher.isResolvable
import `in`.evilcorp.bricksopenlauncher.presentation.shortcuts_view.LauncherFragment
import `in`.evilcorp.bricksopenlauncher.service.HelperService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    companion object {
        private const val INTENT_SELF_TERMINATE = "bricksopenlauncher.activity.terminateService"
        private const val INTENT_MIRRORLINK_TERMINATE = "com.mirrorlink.android.app.TERMINATE"
        private const val VOICE_BTN_ERROR_IMG_LEVEL = 1

        fun launch(ctx: Context) {
            val intent = Intent(ctx, MainActivity::class.java)
            ctx.startActivity(intent)
        }

        fun terminate(ctx: Context) {
            val terminateIntent = Intent(ctx, MainActivity::class.java).apply {
                action = INTENT_SELF_TERMINATE
                addFlags(FLAG_ACTIVITY_NEW_TASK)
            }

            ctx.startActivity(terminateIntent)
        }
    }

    @Inject
    lateinit var viewStateInteractor: LauncherUiStateInteractor

    private lateinit var launchActivityComponent: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launchActivityComponent = ComponentName(this, HomeStubActivity::class.java)

        setContentView(R.layout.activity_main)
        setSupportActionBar(tb_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        iv_voice_command?.setOnClickListener { launchVoiceAssistant() }

        handleIntent(intent)
        viewStateInteractor.onLauncherUiCreated()
    }

    private fun launchVoiceAssistant() {
        val assistantIntent = Intent(Intent.ACTION_VOICE_COMMAND)
        assistantIntent.flags = FLAG_ACTIVITY_NEW_TASK

        if (assistantIntent.isResolvable(this)) {
            startActivity(assistantIntent)
        } else {
            setVoiceBtnErrorState()
        }
    }

    private fun setVoiceBtnErrorState() {
        iv_voice_command?.apply {
            setImageLevel(VOICE_BTN_ERROR_IMG_LEVEL)
            isEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.d("Activity started")
        viewStateInteractor.onLauncherUiVisible()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_shortcuts_place, LauncherFragment())
            commit()
        }
    }

    override fun onPause() {
        super.onPause()
        viewStateInteractor.onLauncherUiInvisible()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean = false

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == INTENT_SELF_TERMINATE || intent?.action == INTENT_MIRRORLINK_TERMINATE) {
            disableDefaultLauncher()
            finish()

        } else {
            enableDefaultLauncher()
        }
    }

    private fun disableDefaultLauncher() {
        toggleLaunchActivityState(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
        terminateHelperService()
        viewStateInteractor.onLauncherUiTerminate()
    }

    private fun terminateHelperService() {
        val intent = Intent(this, HelperService::class.java)
        stopService(intent)
    }

    private fun enableDefaultLauncher() {
        toggleLaunchActivityState(PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
        launchHelperService()
    }

    private fun launchHelperService() {
        val intent = Intent(this, HelperService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun toggleLaunchActivityState(newState: Int) {
        val currentState = packageManager.getComponentEnabledSetting(launchActivityComponent)

        if (currentState != newState) {
            packageManager.setComponentEnabledSetting(launchActivityComponent, newState, PackageManager.DONT_KILL_APP)

            val callMainIntent = Intent(Intent.ACTION_MAIN)
            callMainIntent.addCategory(Intent.CATEGORY_HOME)
            startActivity(callMainIntent)
        }
    }
}
