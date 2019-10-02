package `in`.evilcorp.bricksopenlauncher

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

interface RxLifecycle {
    var disposer: Disposable
    fun disposeSubscriptions()
    fun clearSubscriptions()
}

open class RxLifecycleDelegate: RxLifecycle {
    private val disposables by lazy { CompositeDisposable() }

    override var disposer: Disposable
        get() = disposables
        set(value) { disposables.add(value) }

    override fun disposeSubscriptions() {
        disposables.dispose()
    }

    override fun clearSubscriptions() {
        disposables.clear()
    }
}