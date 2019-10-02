package `in`.evilcorp.bricksopenlauncher.presentation.shortcuts_selector_view

import `in`.evilcorp.bricksopenlauncher.R
import `in`.evilcorp.bricksopenlauncher.repository.entities.Shortcut
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.element_app_shortcut_horizontal.view.*

class SelectorAdapter(
        private val maxSelectionCount: () -> Int,
        private val onSelectedCountUpd: (Int) -> Unit) : RecyclerView.Adapter<SelectorAdapter.Holder>() {

    private var items = ArrayList<Shortcut>()
    private var currentSelections = 0

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): Holder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.element_app_shortcut_horizontal, parent, false)

        return Holder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val shortcut = items[position]

        holder.apply {
            ivIcon.setImageDrawable(shortcut.icon)
            tvText.text = shortcut.title
            holderStateSelected = shortcut.selected

            itemView.setOnClickListener {
                if (holderStateSelected || isAllowMakeSelection()) {
                    items[position] = shortcut.copy(!holderStateSelected, position)
                    notifyItemChanged(position)
                    updateSelectedCount()
                }
            }
        }
    }

    private fun isAllowMakeSelection(): Boolean = currentSelections < maxSelectionCount.invoke()

    fun setData(data: Collection<Shortcut>) {
        items.clear()
        items.addAll(data)

        updateSelectedCount()
        notifyDataSetChanged()
    }

    private fun updateSelectedCount() {
        currentSelections = items.count { it.selected }
        onSelectedCountUpd.invoke(currentSelections)
    }

    fun getUpdatedItems(): Collection<Shortcut> = items

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView = itemView.iv_icon
        val tvText: TextView = itemView.tv_title

        var holderStateSelected: Boolean
            get() = itemView.isSelected
            set(value) {
                itemView.isSelected = value
            }
    }
}