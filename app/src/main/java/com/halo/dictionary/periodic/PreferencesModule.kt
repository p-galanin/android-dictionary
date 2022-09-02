package com.halo.dictionary.periodic

import android.app.NotificationManager
import android.content.Context
import com.halo.dictionary.repository.PreferencesHelper
import com.halo.dictionary.repository.impl.PreferencesStorage
import com.halo.dictionary.repository.impl.PreferencesStorageImpl
import com.halo.dictionary.repository.impl.PreferencesHelperImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {
    @Binds
    abstract fun bindPreferencesStorage(
        preferenceStorageImpl: PreferencesStorageImpl
    ): PreferencesStorage
}