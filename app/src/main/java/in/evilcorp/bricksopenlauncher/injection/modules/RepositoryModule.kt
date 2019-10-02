package `in`.evilcorp.bricksopenlauncher.injection.modules

import `in`.evilcorp.bricksopenlauncher.repository.AppPackageStorage
import `in`.evilcorp.bricksopenlauncher.repository.SelectionItemStorage
import `in`.evilcorp.bricksopenlauncher.repository.ShortcutsRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ StorageModule::class ])
class RepositoryModule {

    @Provides
    @Singleton
    fun provideShortcutsRepo(storage: SelectionItemStorage, packages: AppPackageStorage): ShortcutsRepository {
        return ShortcutsRepository(packages, storage)
    }
}