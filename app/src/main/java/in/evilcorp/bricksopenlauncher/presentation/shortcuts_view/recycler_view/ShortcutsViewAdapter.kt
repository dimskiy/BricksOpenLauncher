package `in`.evilcorp.bricksopenlauncher.presentation.shortcuts_view.recycler_view

import `in`.evilcorp.bricksopenlauncher.R
import `in`.evilcorp.bricksopenlauncher.presentation.shortcuts_view.ChangeEvent
import `in`.evilcorp.bricksopenlauncher.presentation.shortcuts_view.Reorder
import `in`.evilcorp.bricksopenlauncher.presentation.shortcuts_view.SimpleClick
import `in`.evilcorp.bricksopenlauncher.repository.entities.Shortcut
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.element_app_shortcut.view.*

class ShortcutsViewAdapter(private val eventsListener: (ChangeEvent) -> Unit)
    : RecyclerView.Adapter<ShortcutsViewAdapter.Holder>(), MutableList<Shortcut> by ArrayList() {

    override fun onCreateViewHolder(group: ViewGroup, type: Int): Holder {
        val view = LayoutInflater
                .from(group.context)
                .inflate(R.layout.element_app_shortcut, group, false)

        return Holder(view)
    }

    override fun getItemCount(): Int = size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = get(position)

        if (item.position == -1) {
            item.position = position
            eventsListener(Reorder(item))
        }

        holder.run {
            iconView?.setImageDrawable(item.icon)
            titleView?.text = item.title
        }

        holder.itemView.setOnClickListener { eventsListener(SimpleClick(item)) }
    }

    fun setData(data: Collection<Shortcut>) {
        val sortedData = data.sortedBy(Shortcut::position)

        clear()
        addAll(sortedData)
        notifyDataSetChanged()
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        val firstItem = get(fromPosition)
        val secondItem = get(toPosition)

        set(fromPosition, secondItem)
        set(toPosition, firstItem)
        notifyItemMoved(fromPosition, toPosition)

        eventsListener(Reorder(firstItem.copy(position = toPosition)))
        eventsListener(Reorder(secondItem.copy(position = fromPosition)))
    }

    inner class Holder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val iconView: ImageView? = itemView.iv_shortcut_icon
        val titleView: TextView? = itemView.tv_shortcut_label
    }
}