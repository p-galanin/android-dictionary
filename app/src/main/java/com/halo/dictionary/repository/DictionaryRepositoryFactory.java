package com.halo.dictionary.repository;

import android.content.Context;

import com.halo.dictionary.mvp.base.ViewBase;
import com.halo.dictionary.repository.sql.SqLiteDictionaryRepository;

import androidx.annotation.NonNull;

/**
 * Repositories factory.
 */
public class DictionaryRepositoryFactory {

    /**
     * Returns the repository instance.
     *
     * @param viewBase view to create repository :(, not null
     * @return repository instance, not null
     */
    @NonNull
    public static DictionaryRepository createDictionaryRepository(@NonNull final ViewBase viewBase) {
        return SqLiteDictionaryRepository.getInstance(viewBase.getContext());
    }

    /**
     * Returns the repository instance.
     *
     * @param context to create repository :(, not null
     * @return repository instance, not null
     */
    @NonNull
    public static DictionaryRepository createDictionaryRepository(@NonNull final Context context) {
        return SqLiteDictionaryRepository.getInstance(context);
    }

}
