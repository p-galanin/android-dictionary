package com.halo.dictionary.repository.dump;

import android.util.Log;

import com.halo.dictionary.mvp.WordEntryKt;

import java.util.Optional;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

public class DumpFormat {

    private static final String TAG = "DumpFormat";
    private static final String SEPARATOR = "###";

    @VisibleForTesting
    static final String DEFAULT_TRANSLATION = "?";

    public static Optional<WordEntryKt> parseEntry(@NonNull final String entryString) {
        final String[] parts = entryString.split(SEPARATOR);

        final WordEntryKt entry;
        if (isCorrectFormat(entryString, parts)) {
            entry = new WordEntryKt(parts[0], parts[1], parseWeight(parts[2]), Boolean.parseBoolean(parts[3]));
        } else if (isOldFormat(entryString, parts)) { // todo remove old format support
            entry = new WordEntryKt(parts[0], parts.length > 1 ? parts[1] : DEFAULT_TRANSLATION, 0, false);
        } else {
//            Log.w(TAG, "Incorrect format: " + entryString); todo log and units
            entry = null;
        }

        return Optional.ofNullable(entry);
    }

    private static int parseWeight(String part) {
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Unable to parse weight", e);
            return 0;
        }
    }

    private static boolean isCorrectFormat(String entryString, String[] parts) {
        return parts.length == 4;
    }

    private static boolean isOldFormat(@NonNull String entryString, String[] parts) {
        return parts.length == 2 || (parts.length == 1 && entryString.endsWith(SEPARATOR));
    }

    public static String serializeEntry(
            final String word,
            final String translation,
            final int weight,
            final boolean isArchived) {
        String notEmptyTranslation = translation.isEmpty() ? DEFAULT_TRANSLATION : translation;
        return word + SEPARATOR + notEmptyTranslation + SEPARATOR + weight + SEPARATOR + isArchived;
    }
}
