package `in`.evilcorp.bricksopenlauncher.presentation.shortcuts_view.recycler_view

import `in`.evilcorp.bricksopenlauncher.R
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ShortcutsRecyclerView(private val ctx: Context, attrs: AttributeSet?): RecyclerView(ctx, attrs) {

    companion object {
        const val ITEMS_PER_SCREEN_DEFAULT = 5
        const val ITEMS_MARGIN_DEFAULT_DP = 10f
    }

    private var itemsPerScreen: Int = ITEMS_PER_SCREEN_DEFAULT
    private var itemsMarginPx: Int = getPxFromDp(ITEMS_MARGIN_DEFAULT_DP)

    init {
        ctx.obtainStyledAttributes(attrs, R.styleable.ShortcutsRecyclerView)?.apply {
            itemsPerScreen = getInt(R.styleable.ShortcutsRecyclerView_shortcutsPerScreen, itemsPerScreen)
            itemsMarginPx = getDimensionPixelSize(R.styleable.ShortcutsRecyclerView_marginBetweenShortcuts, itemsMarginPx)

            recycle()
        }

        layoutManager = ShortcutsLayoutManager(ctx)
        addItemDecoration(ShortcutsDecorator(itemsPerScreen, itemsMarginPx))
        setHasFixedSize(true)
    }

    private fun getPxFromDp(dpValue: Float): Int {
        val displayMetrics = ctx.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, displayMetrics).toInt()
    }

    fun getItemsCountDesired(): Int = itemsPerScreen

    private class ShortcutsLayoutManager(ctx: Context) : LinearLayoutManager(ctx) {
        init { orientation = HORIZONTAL }

        override fun canScrollHorizontally() = false
    }

    private class ShortcutsDecorator(private val itemsPerScreen: Int, private val itemMargin: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
            super.getItemOffsets(outRect, view, parent, state)

            outRect.apply {
                if (parent.getChildAdapterPosition(view) == 0) {
                    left = itemMargin
                }

                top =  itemMargin
                right = itemMargin
                bottom = itemMargin
            }

            val itemWidth = (parent.measuredWidth - parent.paddingLeft - parent.paddingRight) / itemsPerScreen
            view.layoutParams.width = itemWidth - itemMargin - (itemMargin / itemsPerScreen) + 1
        }
    }
}

class ShortcutsTouchCallback(private val consumer: (Int, Int) -> Unit): ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0) {
    override fun onMove(recyclerView: RecyclerView, srcHolder: RecyclerView.ViewHolder, targetHolder: RecyclerView.ViewHolder): Boolean {
        val sourcePosition = recyclerView.getChildAdapterPosition(srcHolder.itemView)
        val targetPosition = recyclerView.getChildAdapterPosition(targetHolder.itemView)
        consumer.invoke(sourcePosition, targetPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun isLongPressDragEnabled(): Boolean = true

    override fun isItemViewSwipeEnabled(): Boolean = false
}