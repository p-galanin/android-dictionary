package com.halo.dictionary.repository.impl.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
                "CREATE TABLE " + WordContract.Entry.TABLE_NAME + " (" +
                        WordContract.Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WordContract.Entry.COLUMN_NAME_WORD + " VARCHAR(63) NOT NULL, " +
                        WordContract.Entry.COLUMN_NAME_TRANSLATION + " VARCHAR(511)" +
                        ");";

        sqLiteDatabase.execSQL(SQL_CREATE_WORDS_TABLE);
    }

    public boolean update(@NonNull final WordEntry wordEntry) {

        final ContentValues values = new ContentValues();
        values.put(WordContract.Entry.COLUMN_NAME_WORD, wordEntry.getWord());
        values.put(WordContract.Entry.COLUMN_NAME_TRANSLATION, wordEntry.getTranslation());

        return getWritableDatabase().update(
                WordContract.Entry.TABLE_NAME,
                values,
                WordContract.Entry._ID + "=?",
                new String[] { String.valueOf(wordEntry.getId()) }
                ) > 0;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WordContract.Entry.TABLE_NAME); // TODO easy, boy
        onCreate(sqLiteDatabase);
    }

    /**
     * Возвращает все хранящиеся в базе записи о словах.
     *
     * @return все хранящиеся записи о словах
     */
    public Cursor getAllWordsEntries() {
        return getAllWordsEntries(WordContract.Entry.COLUMN_NAME_WORD);
    }

    /**
     * Возвращает все хранящиеся в базе записи о словах.
     *
     * @param sortingColumn сортировка результата
     * @return все хранящиеся записи о словах
     */
    public Cursor getAllWordsEntries(final String sortingColumn) {
        final String[] columns = {
                WordContract.Entry.COLUMN_NAME_WORD,
                WordContract.Entry.COLUMN_NAME_TRANSLATION,
                WordContract.Entry._ID
        };
        return getReadableDatabase().query(
                WordContract.Entry.TABLE_NAME,
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
        return findFirstEntry(WordContract.Entry.COLUMN_NAME_WORD + "=?", new String[] { word });
    }

    /**
     * Loads the word entry by it's id.
     *
     * @param id entry id, not null
     * @return word entry with such id or empty object, if there is no one
     */
    Optional<WordEntry> getEntryById(@NonNull final Long id) {
        return findFirstEntry(WordContract.Entry._ID + "=?", new String[] { id.toString() });
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

        final SQLiteDatabase dbWordsWr = getWritableDatabase();
        long index = createWordEntry(word, translation, dbWordsWr);
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
        getWritableDatabase().delete(
                WordContract.Entry.TABLE_NAME, WordContract.Entry._ID + "='" + id + "'", null);
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
        values.put(WordContract.Entry.COLUMN_NAME_WORD, word);
        values.put(WordContract.Entry.COLUMN_NAME_TRANSLATION, translation);

        Log.d(TAG, "Adding new word: " + word + " (" + translation + ")");

        return dbWordsWr.insert(WordContract.Entry.TABLE_NAME, null, values);
    }

    private Optional<WordEntry> findFirstEntry(final String selection, final String[] selectionArgs) {
        final String[] columns = {
                WordContract.Entry.COLUMN_NAME_WORD,
                WordContract.Entry.COLUMN_NAME_TRANSLATION,
                WordContract.Entry._ID
        };
        try (final Cursor found = getReadableDatabase().query(
                WordContract.Entry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        )) {
            if (found.moveToFirst()) {
                return Optional.of(composeWordEntryByCursor(found));
            } else {
                return Optional.empty();
            }
        }
    }

    private WordEntry composeWordEntryByCursor(@NonNull final Cursor cursor) {
        return new WordEntry(cursor.getString(cursor.getColumnIndex(WordContract.Entry.COLUMN_NAME_WORD)),
                cursor.getString(cursor.getColumnIndex(WordContract.Entry.COLUMN_NAME_TRANSLATION)),
                cursor.getLong(cursor.getColumnIndex(WordContract.Entry._ID)));
    }

}
