package com.halo.dictionary.mvp.ui.di

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.halo.dictionary.mvp.EditEntryView
import com.halo.dictionary.mvp.WordsListPresenter
import com.halo.dictionary.mvp.WordsListView
import com.halo.dictionary.mvp.ui.WordsListPresenterImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
interface WordsListBindingModule {
    @Binds
    fun wordsListPresenter(wordsListPresenter: WordsListPresenterImpl): WordsListPresenter
}

@Module
@InstallIn(ActivityComponent::class)
class WordsListViewModule {
    @Provides
    fun provideWordsListView(@ActivityContext context: Context): WordsListView {
        return context as WordsListView
    }
}

@Module
@InstallIn(FragmentComponent::class)
class EditEntryViewModule {
    @Provides
    fun provideEditEntryView(fragment: Fragment): EditEntryView {
        return fragment as EditEntryView
    }
}