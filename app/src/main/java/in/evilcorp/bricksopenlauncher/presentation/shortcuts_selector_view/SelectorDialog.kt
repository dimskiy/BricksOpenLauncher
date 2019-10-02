package `in`.evilcorp.bricksopenlauncher.presentation.shortcuts_selector_view

import `in`.evilcorp.bricksopenlauncher.R
import `in`.evilcorp.bricksopenlauncher.repository.entities.Shortcut
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SelectorDialog : DialogFragment() {

    private var doOnItemsSelection: (Collection<Shortcut>) -> Unit = {}
    private var maxItemsSelectLimit: Int = 0
    private var currentSelectionsCount: Int = 0
    private val selectorAdapter = SelectorAdapter(this::maxItemsSelectLimit, this::onSelectionsCountUpdate)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ctx = requireContext()
        val selectorView = RecyclerView(ctx).apply {
            layoutManager = LinearLayoutManager(ctx)
            adapter = selectorAdapter
        }

        return AlertDialog.Builder(ctx)
                .setTitle(getTitleWithSelectedCount())
                .setView(selectorView)
                .setPositiveButton(R.string.label_btn_save) { _, _ -> doOnItemsSelection.invoke(selectorAdapter.getUpdatedItems()) }
                .setNegativeButton(android.R.string.cancel) { _, _ -> dialog?.cancel() }
                .create()
    }

    fun setListData(data: Collection<Shortcut>): SelectorDialog {
        selectorAdapter.setData(data)
        return this
    }

    fun selectionCallback(callback: (Collection<Shortcut>) -> Unit): SelectorDialog {
        doOnItemsSelection = callback
        return this
    }

    fun selectionItemsLimit(limit: Int): SelectorDialog {
        maxItemsSelectLimit = limit
        return this
    }

    private fun onSelectionsCountUpdate(currentSelectionsCount: Int) {
        this.currentSelectionsCount = currentSelectionsCount
        dialog?.takeIf { it.isShowing }?.setTitle(getTitleWithSelectedCount())
    }

    private fun getTitleWithSelectedCount(): String {
        return resources.getString(R.string.label_apps_dialog_title, currentSelectionsCount, maxItemsSelectLimit)
    }
}