package `in`.evilcorp.bricksopenlauncher.repository.entities

import android.content.Intent
import android.graphics.drawable.Drawable

class Shortcut(var selected: Boolean = false, var position: Int = 0): Comparable<Shortcut> {
    companion object {
        const val NO_POSITION = -1
    }

    var pkgId = ""
    var title = ""
    var icon: Drawable? = null
    var launchIntent: Intent? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Shortcut

        if (pkgId != other.pkgId) return false
        if (title != other.title) return false
        if (selected != other.selected) return false
        if (position != other.position) return false

        return true
    }

    override fun hashCode(): Int {
        var result = 31 + title.hashCode()
        result = 31 * result + selected.hashCode()
        result = 31 * result + position
        result = 31 * result + pkgId.hashCode()
        return result
    }

    fun copy(selected: Boolean? = null, position: Int? = null): Shortcut {
        return Shortcut().apply {
            pkgId = this@Shortcut.pkgId
            title = this@Shortcut.title
            icon = this@Shortcut.icon
            launchIntent = this@Shortcut.launchIntent
            this.selected = selected ?: this@Shortcut.selected
            this.position = position ?: this@Shortcut.position
        }
    }

    override fun compareTo(other: Shortcut): Int {
        val selectedCompare = other.selected.compareTo(selected)

        return if (selectedCompare != 0) {
            selectedCompare
        } else {
            other.position.compareTo(position)
        }
    }
}