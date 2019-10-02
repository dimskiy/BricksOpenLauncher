package `in`.evilcorp.bricksopenlauncher.repository

import `in`.evilcorp.bricksopenlauncher.repository.entities.AppPackage
import `in`.evilcorp.bricksopenlauncher.repository.entities.SelectionItem
import `in`.evilcorp.bricksopenlauncher.repository.entities.Shortcut
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


interface SelectionItemStorage {
    fun getSelection(textKey: String): Maybe<SelectionItem>
    fun saveSelection(selectionItem: SelectionItem): Completable
}

interface AppPackageStorage {
    fun getAvailablePackages(): Observable<AppPackage>
    fun getAvailablePackagesCached(): Observable<AppPackage>
}


class ShortcutsRepository @Inject constructor(
        private val packages: AppPackageStorage,
        private val storage: SelectionItemStorage) {

    fun getShortcutsUpdated(): Observable<Shortcut> {
        return packages.getAvailablePackages().compose(this::createStorageUpdatedShortcuts)
    }

    fun getShortcutsUpdatedCached(): Observable<Shortcut> {
        return packages.getAvailablePackagesCached().compose(this::createStorageUpdatedShortcuts)
    }

    private fun createStorageUpdatedShortcuts(upstream: Observable<AppPackage>): Observable<Shortcut> {
        return upstream
                .subscribeOn(Schedulers.io())
                .flatMapMaybe { pkg ->
                    storage.getSelection(pkg.pkgId)
                            .map { selection -> restoreShortcut(pkg, selection) }
                            .defaultIfEmpty(createShortcut(pkg))
                }
    }

    private fun restoreShortcut(appPackage: AppPackage, selectionItem: SelectionItem): Shortcut {
        return createShortcut(appPackage).apply {
            selected = selectionItem.selection
            position = selectionItem.position
        }
    }

    private fun createShortcut(appPackage: AppPackage): Shortcut {
        return Shortcut().apply {
            pkgId = appPackage.pkgId
            title = appPackage.title
            icon = appPackage.icon
            launchIntent = appPackage.launchIntent
        }
    }
    
    fun updateSingleShortcut(shortcut: Shortcut): Completable {
        return storage.getSelection(shortcut.pkgId)
                .subscribeOn(Schedulers.io())
                .map { selection -> selection.update(shortcut.selected, shortcut.position) }
                .defaultIfEmpty(createSelectionFrom(shortcut))
                .flatMapCompletable(storage::saveSelection)
    }

    private fun createSelectionFrom(shortcut: Shortcut): SelectionItem {
        return SelectionItem(shortcut.pkgId, shortcut.position, shortcut.selected)
    }
}