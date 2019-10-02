package `in`.evilcorp.bricksopenlauncher.repository.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "selection_item")
data class SelectionItem (
        @ColumnInfo(name = "text_key") var textKey: String = "",
        @ColumnInfo(name = "position") var position: Int = Shortcut.NO_POSITION,
        @ColumnInfo(name = "selection") var selection: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    fun update(selection: Boolean, position: Int): SelectionItem {
        this.selection = selection
        this.position = position

        return this
    }
}