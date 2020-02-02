package com.halo.dictionary.repository.dump;

import com.halo.dictionary.mvp.WordEntry;

import java.util.Optional;

import androidx.annotation.NonNull;

public class DumpFormat {

    private static final String SEPARATOR = "###";

    public static Optional<WordEntry> parseEntry(@NonNull final String entryString) {
        final String[] parts = entryString.split(SEPARATOR);
        if (parts.length == 2 || (parts.length == 1 && entryString.endsWith(SEPARATOR))) {
            return Optional.of(new WordEntry(parts[0], parts.length == 2 ? parts[1] : "", -1));
        } else {
            return Optional.empty();
        }
    }

    public static String composeStringEntry(final String word, final String translation) {
        return word + SEPARATOR + translation;
    }
}
