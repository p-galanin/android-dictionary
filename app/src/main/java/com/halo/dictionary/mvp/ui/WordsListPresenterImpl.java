package com.halo.dictionary.mvp.ui;

import android.content.Context;
import android.net.Uri;

import com.halo.dictionary.mvp.WordEntry;
import com.halo.dictionary.mvp.WordsListPresenter;
import com.halo.dictionary.mvp.WordsListView;
import com.halo.dictionary.mvp.base.Utils;
import com.halo.dictionary.periodic.PeriodicWorkUtils;
import com.halo.dictionary.repository.DictionaryRepository;
import com.halo.dictionary.repository.dump.DumpCallback;
import com.halo.dictionary.repository.dump.RestoreFromDumpCallback;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.work.WorkManager;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.scopes.ActivityScoped;

@ActivityScoped
public class WordsListPresenterImpl implements WordsListPresenter, DictionaryRepository.Listener {

    private WordsListView view;
    private final DictionaryRepository repository;
    private final DictionaryRepository.Navigator navigator;
    private final WorkManager workManger;

    private final Set<Long> invisibleTranslations = new HashSet<>();

    @Inject
    public WordsListPresenterImpl(
            @NonNull WordsListView view,
            @NonNull DictionaryRepository repository,
            @NonNull DictionaryRepository.Navigator navigator,
            @NonNull WorkManager workManager
    ) {
        attachView(view);
        this.repository = repository;
        this.navigator = navigator;
        this.repository.registerListener(this);
        this.workManger = workManager;
    }

    @Override
    public WordsListView getView() {
        if (this.view == null) {
            throw new IllegalStateException("No view attached"); // TODO smth more loyal?
        }
        return this.view;
    }

    @Override
    public void onViewInitialized() {
        PeriodicWorkUtils.startWordOfTheDayNotifications(workManger);
    }

    @Override
    public void onAddWordButtonClicked() {
        getView().goToAddWordScreen();
    }

    @Override
    public int getEntriesAmount() {
        return this.navigator.getEntriesAmount();
    }

    @Override
    public void onWordSwiped(@NonNull Long wordId) {
        this.repository.deleteEntry(wordId);
    }

    @Override
    public void onWordLongPressed(@NonNull final Long entryId) {
        getView().goToEditEntryScreen(entryId);
    }

    @Override
    public void onClickEntry(final Long entryId) {
        if (!this.invisibleTranslations.remove(entryId)) {
            this.invisibleTranslations.add(entryId);
        }
    }

    @Override
    public Optional<WordEntry> getEntryForPosition(final int position) {
        return getEntriesNavigator().getEntryByIndex(position);
    }

    @Override
    public void onDumpFilePicked(@NonNull final Context context, @NonNull final Uri uri) {

        getView().showProgress();

        new Thread(() -> this.repository.restoreFromDump(Utils.getStreamFromUri(context, uri), new RestoreFromDumpCallback() {

            @Override
            public void onComplete(final int restoredAmount) {
                getView().executeOnUiThread(() -> {
                    getView().stopProgress();
                    getView().showMessage("Restored " + restoredAmount + " words");
                });
            }

            @Override
            public void onError(final String errorText) {
                getView().executeOnUiThread(() -> getView().stopProgress());
            }

        })).start();
    }

    @Override
    public void onRestoreFromDumpClicked() {
        getView().goToChooseFile();
    }

    @Override
    public void onDumpToFileClicked() {
        getView().goToChooseDirectory();
    }

    @Override
    public void onDisableNotificationsClicked() {
        PeriodicWorkUtils.stopWordOfTheDayNotifications(getView());
        getView().showMessage("Notifications disabled");
    }

    @Override
    public void onDumpDirectoryPicked(final Uri uri) {
        final Optional<String> directoryPath = Utils.getDirectoryPathFromUri(uri);
        if (directoryPath.isPresent()) {
            new Thread(() -> this.repository.dump(directoryPath.get(), new DumpCallback() {

                @Override
                public void onComplete() {
                    getView().executeOnUiThread(() -> getView().showMessage("Dump successful"));
                }

                @Override
                public void onError(final String errorText) {
                    getView().executeOnUiThread(() -> getView().showMessage(errorText));
                }
            })).start();
        } else {
            getView().showMessage("Choose another directory");
        }
    }

    @Override
    public boolean isTranslationVisible(final Long entryId) {
        return !this.invisibleTranslations.contains(entryId);
    }

    @Override
    public void attachView(@NonNull final WordsListView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.repository.unregisterListener(this);
    }


    @NonNull
    private DictionaryRepository.Navigator getEntriesNavigator() {
        return this.navigator;
    }

    @Override
    public void onEntriesListChanged() {
        getView().executeOnUiThread(() -> getEntriesNavigator().refresh());
        getView().executeOnUiThread(() -> getView().refreshList());
    }

    @Override
    public void onEntryChanged(@NonNull final WordEntry entry) {
        onEntriesListChanged(); // TODO refresh only changed item
        getView().executeOnUiThread(() -> getView().showMessage("Edited"));
    }

    @Override
    public void onArchiveClicked(@NonNull Long wordId) {
        this.repository.loadEntry(wordId).ifPresent(wordEntry -> {
            WordEntry updatedEntry = new WordEntry(
                    wordEntry.getWord(),
                    wordEntry.getTranslation(),
                    wordEntry.getWeight(),
                    !wordEntry.isArchived(),
                    wordEntry.getId()
            );
            this.repository.updateEntry(updatedEntry);
            this.view.refreshList();
        });
    }
}
