package `in`.evilcorp.bricksopenlauncher.interactors

import `in`.evilcorp.bricksopenlauncher.repository.ShortcutsRepository
import `in`.evilcorp.bricksopenlauncher.repository.entities.Shortcut
import io.reactivex.Completable
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShortcutsInteractor
@Inject constructor(private val shortcutsRepository: ShortcutsRepository) {

    fun getAllShortcuts(): Observable<Shortcut> = shortcutsRepository.getShortcutsUpdated()

    fun getSelectedShortcuts(): Observable<Shortcut> {
        return shortcutsRepository.getShortcutsUpdatedCached()
                .filter(Shortcut::selected)
    }

    fun updateShortcut(shortcut: Shortcut): Completable {
        return shortcutsRepository.updateSingleShortcut(shortcut)
                .doOnError(Timber::d)
    }
}