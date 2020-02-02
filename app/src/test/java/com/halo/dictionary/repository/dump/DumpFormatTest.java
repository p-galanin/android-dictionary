package com.halo.dictionary.repository.dump;

import android.util.Pair;

import com.halo.dictionary.mvp.WordEntry;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

public class DumpFormatTest {

    @Test
    public void formatConsistency() {

        final Map<String, String> source = new LinkedHashMap<>();
        final List<String> dumped = new ArrayList<>();
        final Map<String, String> restored = new LinkedHashMap<>();

        source.put("apple", "яблоко");
        source.put("arrogant", "высокомерный, надменный");
        source.put("empty", "");

        source.forEach((word, translation) -> dumped.add(DumpFormat.composeStringEntry(word, translation)));
        dumped.add("i'm incorrect");

        for (final String entryString : dumped) {
            DumpFormat.parseEntry(entryString).ifPresent(wordEntry -> restored.put(wordEntry.getWord(), wordEntry.getTranslation()));
        }

        assertEquals(source.size(), restored.size());
        source.forEach((word, translation) -> {
            final String restoredTranslation = restored.get(word);
            assertEquals(translation, restoredTranslation);
        });

    }

}