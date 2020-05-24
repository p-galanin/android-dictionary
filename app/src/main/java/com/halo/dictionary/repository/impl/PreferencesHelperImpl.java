package com.halo.dictionary.repository.impl;

import android.content.Context;
import android.content.SharedPreferences;

import com.halo.dictionary.repository.PreferencesHelper;

import androidx.annotation.NonNull;

public class PreferencesHelperImpl implements PreferencesHelper {

    private static final String PREF_FILE_NAME = "dictionary_prefs";
    private static final String BLOCK_NUMBER_KEY = "block_number";

    private final Context context;

    public PreferencesHelperImpl(@NonNull final Context context) {
        this.context = context;
    }

    @Override
    public int updateBlockNumber(final int blocksAmount) {

        final SharedPreferences preferences = this.context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        final int currentBlock = preferences.getInt(BLOCK_NUMBER_KEY, 0);

        final SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(BLOCK_NUMBER_KEY, currentBlock == blocksAmount ? 0 : currentBlock + 1);
        editor.apply();

        return currentBlock;
    }

    @Override
    public void dropBlockNumber() {
        final SharedPreferences preferences = this.context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        if (preferences.getInt(BLOCK_NUMBER_KEY, 0) != 0) {
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(BLOCK_NUMBER_KEY, 0);
            editor.apply();
        }
    }
}
