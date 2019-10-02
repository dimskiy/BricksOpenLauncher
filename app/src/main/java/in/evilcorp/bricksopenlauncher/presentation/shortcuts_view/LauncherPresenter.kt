package `in`.evilcorp.bricksopenlauncher.presentation.shortcuts_view

import `in`.evilcorp.bricksopenlauncher.interactors.ShortcutsInteractor
import `in`.evilcorp.bricksopenlauncher.presentation.helpers.SingleViewPresenter
import `in`.evilcorp.bricksopenlauncher.presentation.helpers.untilUnbind
import `in`.evilcorp.bricksopenlauncher.repository.PreferencesRepository
import `in`.evilcorp.bricksopenlauncher.repository.entities.Shortcut
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

interface LauncherView {
    fun setShortcuts(data: Collection<Shortcut>)
    fun toggleNoShortcutsPane(show: Boolean)
    fun showAppsSelector(data: Collection<Shortcut>, maxItemsCount: Int)
    fun launchExternalApp(shortcut: Shortcut)
    fun getShortcutsPerViewCount(): Int
    fun notifyAppUnavailable()
    fun notifyOverlayControlsEnabled()
}

@Singleton
class LauncherPresenter
@Inject constructor(
        private val shortcutsInteractor: ShortcutsInteractor,
        private val preferencesRepo: PreferencesRepository) : SingleViewPresenter<LauncherView>() {

    private val shortcutsCountAllowed = AtomicInteger()

    fun getExitByUsbSetting() = preferencesRepo.exitOnUsbDisconnect

    fun getKeepScreenOnSetting() = preferencesRepo.keepScreenOn

    fun getOverlayControlsActiveSetting() = preferencesRepo.overlayControlsFeatureState

    fun onExitByUsbChanged(exitByUsbSelected: Boolean) {
        preferencesRepo.exitOnUsbDisconnect = exitByUsbSelected
    }

    fun onKeepScreenOnChanged(keepScreenOn: Boolean) {
        preferencesRepo.keepScreenOn = keepScreenOn
    }

    fun onOverlayControlsActiveChanged(controlsActive: Boolean) {
        preferencesRepo.overlayControlsFeatureState = controlsActive

        if (controlsActive) {
            applyView { it.notifyOverlayControlsEnabled() }
        }
    }

    fun onAppUnavailable(shortcut: Shortcut) {
        removeFromSelected(shortcut)
        applyView { it.notifyAppUnavailable() }
    }

    private fun removeFromSelected(shortcut: Shortcut) {
        val unavailableShortcut = shortcut.copy(selected = false)
        onShortcutChanged(unavailableShortcut, true)
    }

    fun onShortcutChanged(shortcut: Shortcut, forceRefresh: Boolean = false) {
        shortcutsInteractor.updateShortcut(shortcut)
                .doOnComplete { if (forceRefresh) refreshShortcutsSelected() }
                .subscribe()
                .untilUnbind(this)
    }

    fun onShortcutClick(clicked: Shortcut) {
       applyView { it.launchExternalApp(clicked) }
    }

    fun onAppsSelected(selections: Collection<Shortcut>) {
        Observable.fromIterable(selections)
                .flatMapCompletable(shortcutsInteractor::updateShortcut)
                .doOnTerminate { refreshShortcutsSelected() }
                .subscribe()
                .untilUnbind(this)
    }

    override fun bindView(view: LauncherView) {
        super.bindView(view)
        shortcutsCountAllowed.set(view.getShortcutsPerViewCount())
        refreshShortcutsSelected()
    }

    private fun refreshShortcutsSelected() {
        shortcutsInteractor.getSelectedShortcuts()
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess { updateNoShortcutsPaneBy(it.isEmpty()) }
                .subscribe(this::setShortcuts)
                .untilUnbind(this)
    }

    private fun updateNoShortcutsPaneBy(noShortcutsSelected: Boolean) {
        applyView { it.toggleNoShortcutsPane(noShortcutsSelected) }
    }

    private fun setShortcuts(data: Collection<Shortcut>) {
        applyView { it.setShortcuts(data) }
    }

    fun onShowAppsClick() {
        shortcutsInteractor.getAllShortcuts()
                .sorted()
                .toList()
                .subscribe(this::callAppsSelectionDialog)
                .untilUnbind(this)
    }

    private fun callAppsSelectionDialog(shortcuts: Collection<Shortcut>) {
        applyView { it.showAppsSelector(shortcuts, shortcutsCountAllowed.get()) }
    }
}