package `in`.evilcorp.bricksopenlauncher.repository

import android.content.Context
import android.content.SharedPreferences.Editor
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository
@Inject constructor(ctx: Context) {

    companion object {
        private const val TAG = "bricksopenlauncher.internal.prefshelper"
        private const val EXIT_ON_USB_DISCONNECT = "prefs_helper_exit_on_usb_disconnect"
        private const val KEEP_SCREEN_ON = "prefs_helper_keep_screen_on"
        private const val OVERLAY_CONTROLS_ACTIVE = "prefs_helper_overlay_controls_active"
    }

    private var sharedPreferences = ctx.getSharedPreferences(TAG, Context.MODE_PRIVATE)
    private val keepScreenOnSubj = BehaviorSubject.createDefault(keepScreenOn)

    var exitOnUsbDisconnect: Boolean
        get() = sharedPreferences.getBoolean(EXIT_ON_USB_DISCONNECT, false)
        set(newState) = useEditor {
            it.putBoolean(EXIT_ON_USB_DISCONNECT, newState)
        }

    var overlayControlsFeatureState: Boolean
        get() = sharedPreferences.getBoolean(OVERLAY_CONTROLS_ACTIVE, false)
        set(state) = useEditor {
            it.putBoolean(OVERLAY_CONTROLS_ACTIVE, state)
        }

    var keepScreenOn: Boolean
        get() = sharedPreferences.getBoolean(KEEP_SCREEN_ON, false)
        set(newState) = useEditor {
            it.putBoolean(KEEP_SCREEN_ON, newState)
            keepScreenOnSubj.onNext(newState)
        }

    val keepScreenOnObservable: Observable<Boolean> = keepScreenOnSubj

    private fun useEditor(action: (Editor) -> Unit) {
        sharedPreferences.edit().apply {
            action.invoke(this)
            apply()
        }
    }
}