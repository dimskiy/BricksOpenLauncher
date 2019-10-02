package `in`.evilcorp.bricksopenlauncher.repository

import `in`.evilcorp.bricksopenlauncher.repository.entities.AppPackage
import `in`.evilcorp.bricksopenlauncher.repository.entities.SelectionItem
import `in`.evilcorp.bricksopenlauncher.repository.entities.Shortcut
import android.content.Intent
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test

class ShortcutsRepositoryTest {
    private val pkgPrefix = "test.evilcorp"

    private lateinit var shortcutsRepo: ShortcutsRepository
    private lateinit var referenceApps: MutableCollection<AppPackage>
    private lateinit var referenceShortcuts: MutableCollection<Shortcut>

    private lateinit var disposables: CompositeDisposable

    @Before
    fun prepareRxEnvironment() {
        disposables = CompositeDisposable()
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    }

    @Before
    fun setUp() {
        generateApps()
        generateShortcuts()

        val appsRepoStub = object : AppPackageStorage {
            override fun getAvailablePackages(): Observable<AppPackage> = Observable.fromIterable(referenceApps)
            override fun getAvailablePackagesCached(): Observable<AppPackage> = getAvailablePackages()
        }

        val selectRepoStub = object : SelectionItemStorage {
            val srcApp = referenceApps.first()
            val testItem = SelectionItem(srcApp.pkgId, 0, true)

            override fun getSelection(textKey: String): Single<SelectionItem> = Single.just(testItem.apply { this.textKey = textKey})
            override fun saveSelection(selectionItem: SelectionItem): Completable = Completable.complete()
        }

        shortcutsRepo = ShortcutsRepository(appsRepoStub, selectRepoStub)
    }

    private fun generateApps() {
        referenceApps = ArrayList()
        val drawable = object : Drawable() {
            override fun draw(canvas: Canvas) {}
            override fun setAlpha(alpha: Int) {}
            override fun getOpacity(): Int = 1
            override fun setColorFilter(colorFilter: ColorFilter?) {}
        }
        for (i in 0..9) {
            val item = AppPackage("$pkgPrefix-app$i", "app$i", drawable, Intent())
            referenceApps.add(item)
        }
    }

    private fun generateShortcuts() {
        referenceShortcuts = referenceApps
                .map {
                    val item = Shortcut(false, -1)
                    return@map item.apply {
                        title = it.title
                        launchIntent = Intent()
                        pkgId = it.pkgId
                        icon = it.icon
                    }
                }
                .toMutableList()
    }

    @After
    fun clearRxEnvironment() {
        disposables.dispose()
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()
    }

    @Test
    fun getShortcutsUpdated() {
        val testList = referenceShortcuts
                .map {
                    it.selected = true
                    it.position = 0
                    return@map it
                }
                .toList()

        disposables.add(
                shortcutsRepo.getShortcutsUpdated()
                        .test()
                        .assertComplete()
                        .assertValueCount(testList.size)
                        .assertValueSet(testList)
        )
    }

    @Test
    fun updateSingleShortcut() {

    }
}