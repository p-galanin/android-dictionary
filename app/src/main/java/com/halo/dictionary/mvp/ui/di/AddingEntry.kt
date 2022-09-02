package com.halo.dictionary.mvp.ui.di

import android.content.Context
import com.halo.dictionary.mvp.AddEntryPresenter
import com.halo.dictionary.mvp.AddEntryView
import com.halo.dictionary.mvp.ui.AddEntryActivity
import com.halo.dictionary.mvp.ui.AddEntryPresenterImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

/**
 * Created by p.galanin on 01.09.2022
 */
@Module
@InstallIn(ActivityComponent::class)
class AddEntryViewModule {
    @Provides
    fun provideAddEntryActivity(@ActivityContext context: Context): AddEntryView {
        return context as AddEntryView
    }
}

@Module
@InstallIn(ActivityComponent::class)
interface AddEntryModule {
    @Binds
    fun addEntryPresenter(addEntryPresenter: AddEntryPresenterImpl): AddEntryPresenter
}