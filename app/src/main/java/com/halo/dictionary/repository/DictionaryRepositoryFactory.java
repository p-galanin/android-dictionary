package com.halo.dictionary.repository;

import com.halo.dictionary.mvp.ViewBase;
import com.halo.dictionary.repository.sql.SqLiteDictionaryRepository;

import androidx.annotation.NonNull;

public class DictionaryRepositoryFactory {

    @NonNull
    public static DictionaryRepository createDictionaryRepository(final ViewBase viewBase) {
        return SqLiteDictionaryRepository.getInstance(viewBase.getContext());
    }

}
