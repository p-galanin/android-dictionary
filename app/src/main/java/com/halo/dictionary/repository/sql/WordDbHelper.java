package com.halo.dictionary.repository.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Path;
import android.util.Log;

import com.halo.dictionary.mvp.WordEntry;

import java.util.Optional;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Класс для управления обменом данными с БД
 *
 * Created by halo on 17.09.2017.
 */

public class WordDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "words.db";
    private static final String TAG = WordDbHelper.class.getSimpleName();

    private static WordDbHelper INSTANCE;

    private static final int DATABASE_VERSION = 1;

    private WordDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     * Получить экземпляр {@link WordDbHelper} или создать новый, если экземпляр ещё не создан.
     *
     * @param context - контекст, с которым будет создан экземпляр, если до этого ещё не создан
     * @return {@link WordDbHelper}
     */
    @NonNull
    public static synchronized WordDbHelper getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new WordDbHelper(context);
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_WORDS_TABLE =
                "CREATE TABLE " + WordContract.WordEntry.TABLE_NAME + " (" +
                        WordContract.WordEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WordContract.WordEntry.COLUMN_NAME_WORD + " VARCHAR(63) NOT NULL, " +
                        WordContract.WordEntry.COLUMN_NAME_TRANSLATION + " VARCHAR(511)" +
                        ");";

        sqLiteDatabase.execSQL(SQL_CREATE_WORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WordContract.WordEntry.TABLE_NAME); // TODO easy, boy
        onCreate(sqLiteDatabase);
    }

    /**
     * Возвращает все хранящиеся в базе записи о словах.
     *
     * @return все хранящиеся записи о словах
     */
    public Cursor getAllWordsEntries() {
        return getAllWordsEntries(WordContract.WordEntry.COLUMN_NAME_WORD);
    }

    /**
     * Возвращает все хранящиеся в базе записи о словах.
     *
     * @param sortingColumn сортировка результата
     * @return все хранящиеся записи о словах
     */
    public Cursor getAllWordsEntries(final String sortingColumn) {
        final String[] columns = {
                WordContract.WordEntry.COLUMN_NAME_WORD,
                WordContract.WordEntry.COLUMN_NAME_TRANSLATION,
                WordContract.WordEntry._ID
        };
        return this.getReadableDatabase().query(
                WordContract.WordEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortingColumn
        );
    }

    /**
     * Loads the word entry by it's word value.
     *
     * @param word word value, not null
     * @return first found word entry with such word value or empty object, if there is no entries with such value
     */
    Optional<WordEntry> getWordEntryByWord(@NonNull final String word) {
        final String[] columns = {
                WordContract.WordEntry.COLUMN_NAME_WORD,
                WordContract.WordEntry.COLUMN_NAME_TRANSLATION,
                WordContract.WordEntry._ID
        };
        try (final Cursor found = this.getReadableDatabase().query(
                WordContract.WordEntry.TABLE_NAME,
                columns,
                WordContract.WordEntry.COLUMN_NAME_WORD + "=?",
                new String[] { word },
                null,
                null,
                null
        )) {
            if (found.moveToFirst()) {
                return Optional.of(new WordEntry(found.getString(found.getColumnIndex(WordContract.WordEntry.COLUMN_NAME_WORD)),
                        found.getString(found.getColumnIndex(WordContract.WordEntry.COLUMN_NAME_TRANSLATION)),
                        found.getLong(found.getColumnIndex(WordContract.WordEntry._ID))));
            } else {
                return Optional.empty();
            }
        }
    }


    /**
     * Создаёт в БД запись о слове
     *
     * @param word - слово
     * @return созданную запись или {@code null}, если запись не была создана
     */
    @Nullable
    public WordEntry createWordEntry(@NonNull final String word, @Nullable final String translation) {
        WordEntry result = null;

        final SQLiteDatabase dbWordsWr = this.getWritableDatabase();
        long index = this.createWordEntry(word, translation, dbWordsWr);
        if (index > 0) {
            result = new WordEntry(word, translation, index);
        }

        return result;
    }

    /**
     * Удаляет запись с указанным идентификатором
     *
     * @param id - идентификатор удаляемой записи
     */
    public void removeWordEntry(long id) {
        Log.d(TAG, "Remove " + id);
        this.getWritableDatabase().delete(
                WordContract.WordEntry.TABLE_NAME, WordContract.WordEntry._ID + "='" + id + "'", null);
    }

    /**
     * Добавление записи о слове в переданную базу.
     *
     * @param word        - слово
     * @param translation - перевод слова
     * @param dbWordsWr   - база данных для внесения записи
     * @return ID добавленной записи в таблице или -1, если запись не была добавлена
     */
    private long createWordEntry(
            @NonNull final String word, final String translation, @NonNull SQLiteDatabase dbWordsWr) {

        ContentValues values = new ContentValues();
        values.put(WordContract.WordEntry.COLUMN_NAME_WORD, word);
        values.put(WordContract.WordEntry.COLUMN_NAME_TRANSLATION, translation);

        Log.d(TAG, "Adding new word: " + word + " (" + translation + ")");

        return dbWordsWr.insert(WordContract.WordEntry.TABLE_NAME, null, values);
    }

}
