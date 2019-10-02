package `in`.evilcorp.bricksopenlauncher.presentation.shortcuts_view

import `in`.evilcorp.bricksopenlauncher.R
import `in`.evilcorp.bricksopenlauncher.isResolvable
import `in`.evilcorp.bricksopenlauncher.presentation.MainActivity
import `in`.evilcorp.bricksopenlauncher.presentation.helpers.NoDoubleClickGuard
import `in`.evilcorp.bricksopenlauncher.presentation.shortcuts_selector_view.SelectorDialog
import `in`.evilcorp.bricksopenlauncher.presentation.shortcuts_view.recycler_view.ShortcutsTouchCallback
import `in`.evilcorp.bricksopenlauncher.presentation.shortcuts_view.recycler_view.ShortcutsViewAdapter
import `in`.evilcorp.bricksopenlauncher.repository.entities.Shortcut
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_shortcuts.*
import javax.inject.Inject

class LauncherFragment : DaggerFragment(), LauncherView {

    @Inject
    lateinit var presenter: LauncherPresenter

    private val shortcutsViewAdapter = ShortcutsViewAdapter(eventsListener = { event ->
        when (event) {
            is SimpleClick -> presenter.onShortcutClick(event.shortcut)
            is Reorder -> presenter.onShortcutChanged(event.shortcut)
        }
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_shortcuts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_apps_list.adapter = shortcutsViewAdapter
        ItemTouchHelper(ShortcutsTouchCallback(shortcutsViewAdapter::onItemMove))
                .attachToRecyclerView(rv_apps_list)
    }

    override fun onResume() {
        super.onResume()
        presenter.bindView(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.unbindView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_shortcuts_main, menu)

        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            menuItem.setOnMenuItemClickListener(NoDoubleClickGuard())
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val exitOnUsbDisconnect = menu.findItem(R.id.action_close_on_disconnect)
        exitOnUsbDisconnect?.isChecked = presenter.getExitByUsbSetting()

        val keepScreenOn = menu.findItem(R.id.action_keep_screen_on)
        keepScreenOn?.isChecked = presenter.getKeepScreenOnSetting()

        val overlayControlsActive = menu.findItem(R.id.action_show_overlay_controls)
        overlayControlsActive?.isChecked = presenter.getOverlayControlsActiveSetting()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_change_items -> presenter.onShowAppsClick()
            R.id.action_close_on_disconnect -> presenter.onExitByUsbChanged(!item.isChecked)
            R.id.action_keep_screen_on -> presenter.onKeepScreenOnChanged(!item.isChecked)
            R.id.action_show_overlay_controls -> presenter.onOverlayControlsActiveChanged(!item.isChecked)
            R.id.action_close_activity -> MainActivity.terminate(requireContext())
        }
        return false
    }

    override fun setShortcuts(data: Collection<Shortcut>) {
        shortcutsViewAdapter.setData(data)
    }

    override fun toggleNoShortcutsPane(show: Boolean) {
        ll_empty_hint.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showAppsSelector(data: Collection<Shortcut>, maxItemsCount: Int) {
        val fm = fragmentManager ?: return

        SelectorDialog()
                .setListData(data)
                .selectionCallback(presenter::onAppsSelected)
                .selectionItemsLimit(maxItemsCount)
                .show(fm, "")
    }

    override fun launchExternalApp(shortcut: Shortcut) {
        if (shortcut.launchIntent?.isResolvable(context) == true) {
            startActivity(shortcut.launchIntent)
        } else {
            presenter.onAppUnavailable(shortcut)
        }
    }

    override fun getShortcutsPerViewCount(): Int = rv_apps_list.getItemsCountDesired()

    override fun notifyAppUnavailable() {
        Snackbar
                .make(ll_empty_hint, R.string.notify_app_unavailable, Snackbar.LENGTH_LONG)
                .show()
    }

    override fun notifyOverlayControlsEnabled() {
        val snack = Snackbar.make(ll_empty_hint, R.string.notify_overlay_controls_enabled, Snackbar.LENGTH_LONG)
        snack
                .setAction(R.string.label_btn_great) { snack.dismiss() }
                .show()
    }
}