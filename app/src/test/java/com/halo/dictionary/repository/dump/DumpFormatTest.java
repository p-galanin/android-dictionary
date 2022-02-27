package com.halo.dictionary.repository.dump;

import com.halo.dictionary.mvp.WordEntry;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.halo.dictionary.repository.dump.DumpFormat.DEFAULT_TRANSLATION;
import static org.junit.Assert.*;

public class DumpFormatTest {

    @Test
    public void formatConsistency() {

        final Map<String, String> source = new LinkedHashMap<>();
        final List<String> dumped = new ArrayList<>();
        final Map<String, WordEntry> restored = new HashMap<>(source.size());
        final boolean isArchived = false;
        final int weight = 42;

        source.put("apple", "яблоко");
        source.put("arrogant", "высокомерный, надменный");
        source.put("empty", "");

        source.forEach((word, translation) ->
                dumped.add(DumpFormat.serializeEntry(word, translation, weight, isArchived))
        );
        dumped.add("i'm incorrect");

        for (final String entryString : dumped) {
            DumpFormat.parseEntry(entryString).ifPresent(entry -> restored.put(entry.getWord(), entry));
        }

        assertEquals(source.size(), restored.size());
        source.forEach((word, translation) -> {
            WordEntry entry = restored.get(word);
            assertNotNull(entry);
            assertEquals(translation.isEmpty() ? DEFAULT_TRANSLATION : translation, entry.getTranslation());
            assertEquals(isArchived, entry.isArchived());
            assertEquals(weight, entry.getWeight());
        });
    }

}