package com.halo.dictionary.repository.impl.sql

import android.database.Cursor
import com.halo.dictionary.repository.DictionaryRepository.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Provider

@Module
@InstallIn(SingletonComponent::class)
object SqlModule {

    @Provides
    fun provideAllCursor(repository: SqLiteDictionaryRepository): Cursor {
        return repository.allEntriesCursor
    }

    @Provides
    @Named("not archived")
    fun provideNotArchivedCursor(repository: SqLiteDictionaryRepository): Cursor {
        return repository.notArchivedEntriesCursor
    }

    @Provides
    @Named("not archived")
    fun provideNotArchivedNavigator(
        @Named("not archived") cursor: Provider<Cursor>
    ): Navigator {
        return SqLiteNavigator(cursor)
    }

    @Provides
    fun provideNavigator(cursor: Provider<Cursor>): Navigator {
        return SqLiteNavigator(cursor)
    }
}