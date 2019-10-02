package `in`.evilcorp.bricksopenlauncher.injection.modules

import `in`.evilcorp.bricksopenlauncher.LauncherApp
import `in`.evilcorp.bricksopenlauncher.repository.AppPackageStorage
import `in`.evilcorp.bricksopenlauncher.repository.SelectionItemStorage
import `in`.evilcorp.bricksopenlauncher.repository.storage.AppPackageStorageImpl
import `in`.evilcorp.bricksopenlauncher.repository.storage.SelectionItemDB
import `in`.evilcorp.bricksopenlauncher.repository.storage.SelectionItemStorageImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ AppModule::class ])
class StorageModule {

    @Provides
    @Singleton
    fun provideSelectionsStorage(app: LauncherApp): SelectionItemStorage {
        val dao = SelectionItemDB.getInstance(app).getSelectionItemsDAO()
        return SelectionItemStorageImpl(dao)
    }

    @Provides
    @Singleton
    fun provideAppsPackages(app: LauncherApp): AppPackageStorage = AppPackageStorageImpl(app)
}