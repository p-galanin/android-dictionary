package com.halo.dictionary.repository.impl;

import com.halo.dictionary.repository.PreferencesHelper;

import androidx.annotation.NonNull;

public class PreferencesHelperImpl implements PreferencesHelper {

    private static final String BLOCK_NUMBER_KEY = "block_number";

    private final PreferenceStorage mPreferenceStorage;

    public PreferencesHelperImpl(@NonNull final PreferenceStorage preferenceStorage) {
        mPreferenceStorage = preferenceStorage;
    }

    @Override
    public int updateBlockNumber(final int blocksAmount) {
        final int currentBlock = mPreferenceStorage.getInt(BLOCK_NUMBER_KEY, 0);
        final int newBlock = (currentBlock >= blocksAmount - 1) ? 0 : currentBlock + 1;

        mPreferenceStorage.saveInt(BLOCK_NUMBER_KEY, newBlock);
        return newBlock;
    }

    @Override
    public void dropBlockNumber() {
        if (mPreferenceStorage.getInt(BLOCK_NUMBER_KEY, 0) != 0) {
            mPreferenceStorage.saveInt(BLOCK_NUMBER_KEY, 0);
        }
    }
}
