package `in`.evilcorp.bricksopenlauncher.repository.storage

import `in`.evilcorp.bricksopenlauncher.repository.SelectionItemStorage
import `in`.evilcorp.bricksopenlauncher.repository.entities.SelectionItem
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject

@Dao
interface SelectionItemDao {
    @Query("SELECT * FROM selection_item WHERE text_key = :textKey LIMIT 1")
    fun getItemByTextKey(textKey: String): Maybe<SelectionItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveItem(item: SelectionItem): Completable
}

class SelectionItemStorageImpl
@Inject constructor(private val dao: SelectionItemDao) : SelectionItemStorage {

    override fun getSelection(textKey: String) = dao.getItemByTextKey(textKey)

    override fun saveSelection(selectionItem: SelectionItem) = dao.saveItem(selectionItem)
}