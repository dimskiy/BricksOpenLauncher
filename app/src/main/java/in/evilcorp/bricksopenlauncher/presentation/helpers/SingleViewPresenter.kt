package `in`.evilcorp.bricksopenlauncher.presentation.helpers

import `in`.evilcorp.bricksopenlauncher.RxLifecycleDelegate
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.atomic.AtomicReference

fun Disposable.untilUnbind(presenter: SingleViewPresenter<*>) {
    presenter.disposer = this
}

abstract class SingleViewPresenter<VIEW>: RxLifecycleDelegate() {
    private val viewReference = AtomicReference<VIEW?>()

    open fun bindView(view: VIEW) {
        viewReference.set(view)
        Timber.d("View BINDED: $view")
    }

    open fun unbindView() {
        clearSubscriptions()
        viewReference.set(null)
        Timber.d("View UNBINDED")
    }

    fun applyView(action: (VIEW) -> Unit) {
        viewReference.get()?.let { action.invoke(it) }
    }
}