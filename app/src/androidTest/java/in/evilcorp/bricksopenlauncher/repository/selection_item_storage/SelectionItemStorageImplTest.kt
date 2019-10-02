package `in`.evilcorp.bricksopenlauncher.repository.selection_item_storage

import `in`.evilcorp.bricksopenlauncher.RxTestWrapper
import `in`.evilcorp.bricksopenlauncher.repository.entities.SelectionItem
import `in`.evilcorp.bricksopenlauncher.repository.storage.SelectionItemDB
import `in`.evilcorp.bricksopenlauncher.repository.storage.SelectionItemDao
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import io.reactivex.Observable
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4ClassRunner::class)
class SelectionItemStorageImplTest : RxTestWrapper() {
    private lateinit var dbInstance: SelectionItemDB
    private lateinit var selectionItemsDao: SelectionItemDao

    @Before
    fun createDb() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        dbInstance = Room.inMemoryDatabaseBuilder(ctx, SelectionItemDB::class.java).build()
        selectionItemsDao = dbInstance.getSelectionItemsDAO()
    }

    @Test
    fun testPackWriteAndRead() {
        val dataSet = getTestDataSet()

        disposer = Observable.fromIterable(dataSet)
                .flatMapCompletable(selectionItemsDao::saveItem)
                .test()
                .assertComplete()
                .assertTerminated()

        disposer = Observable.fromIterable(dataSet)
                .map { it.textKey }
                .flatMapMaybe (selectionItemsDao::getItemByTextKey)
                .test()
                .assertValueSet(dataSet)
                .assertNoErrors()
                .assertTerminated()
    }

    private fun getTestDataSet(): Collection<SelectionItem> {
        val testData = ArrayList<SelectionItem>()
        for (i in 0..9) {
            testData.add(SelectionItem("test$1", i, i % 2 == 0))
        }
        return testData
    }

    @Test
    fun testReadNonPresentItem() {
        disposer = selectionItemsDao.getItemByTextKey("non-present-item111")
                .test()
                .assertNoValues()
                .assertNoErrors()
    }

    @Test
    fun testOverwriteExistingItem() {
        val dataItem = SelectionItem("test1", 1, true).apply { id = 1 }
        val testingNewPosition = 99

        disposer = selectionItemsDao.saveItem(dataItem)
                .test()

        val changedItem = dataItem.apply { position = testingNewPosition }
        disposer = selectionItemsDao.saveItem(changedItem)
                .test()

        disposer = selectionItemsDao.getItemByTextKey(dataItem.textKey)
                .map { it.position }
                .test()
                .assertValue(testingNewPosition)
                .assertTerminated()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        dbInstance.close()
    }
}