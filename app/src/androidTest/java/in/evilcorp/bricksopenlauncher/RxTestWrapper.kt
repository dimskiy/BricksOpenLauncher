package `in`.evilcorp.bricksopenlauncher

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before

open class RxTestWrapper {
    val disposables = CompositeDisposable()
    var disposer: Disposable
        get() = disposables
        set(value) { disposables.add(value) }

    @Before
    fun overrideSchedulers() {
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
    }

    @After
    fun resetSchedulers() {
        disposables.dispose()
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()
    }
}