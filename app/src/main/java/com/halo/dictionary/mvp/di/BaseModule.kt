package com.halo.dictionary.mvp.di

import android.content.Context
import androidx.work.WorkManager
import com.halo.dictionary.repository.DictionaryRepository
import com.halo.dictionary.repository.impl.sql.SqLiteDictionaryRepository
import com.halo.dictionary.repository.impl.sql.WordDbHelper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface BaseModule {
    @Binds
    fun bindDictionaryRepository(repositoryImpl: SqLiteDictionaryRepository): DictionaryRepository
}

@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {
    @Provides
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}