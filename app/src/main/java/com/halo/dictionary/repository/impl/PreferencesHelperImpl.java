package com.halo.dictionary.repository.impl;

import com.halo.dictionary.repository.PreferencesHelper;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Singleton
public class PreferencesHelperImpl implements PreferencesHelper {

    private static final String BLOCK_NUMBER_KEY = "block_number";

    private final PreferencesStorage preferencesStorage;

    @Inject
    public PreferencesHelperImpl(@NonNull final PreferencesStorage preferencesStorage) {
        this.preferencesStorage = preferencesStorage;
    }

    @Override
    public int updateBlockNumber(final int blocksAmount) {
        final int currentBlock = preferencesStorage.getInt(BLOCK_NUMBER_KEY, 0);
        final int newBlock = (currentBlock >= blocksAmount - 1) ? 0 : currentBlock + 1;

        preferencesStorage.saveInt(BLOCK_NUMBER_KEY, newBlock);
        return newBlock;
    }

    @Override
    public void dropBlockNumber() {
        if (preferencesStorage.getInt(BLOCK_NUMBER_KEY, 0) != 0) {
            preferencesStorage.saveInt(BLOCK_NUMBER_KEY, 0);
        }
    }
}
