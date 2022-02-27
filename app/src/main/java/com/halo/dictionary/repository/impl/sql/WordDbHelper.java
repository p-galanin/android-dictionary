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
 * <p>
 * Created by halo on 17.09.2017.
 */

public class WordDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "words.db";
    private static final String TAG = WordDbHelper.class.getSimpleName();

    private static WordDbHelper INSTANCE;

    private static final int DATABASE_VERSION = 2;

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
                        WordContract.Entry.COLUMN_NAME_TRANSLATION + " VARCHAR(511)," +
                        WordContract.Entry.COLUMN_NAME_IS_ARCHIVED + " INTEGER DEFAULT 0," +
                        WordContract.Entry.COLUMN_NAME_WEIGHT + " INTEGER DEFAULT 0" +
                        ");";

        sqLiteDatabase.execSQL(SQL_CREATE_WORDS_TABLE);
    }

    public boolean update(@NonNull final WordEntry wordEntry) {

        final ContentValues values = new ContentValues();
        values.put(WordContract.Entry.COLUMN_NAME_WORD, wordEntry.getWord());
        values.put(WordContract.Entry.COLUMN_NAME_TRANSLATION, wordEntry.getTranslation());
        values.put(WordContract.Entry.COLUMN_NAME_IS_ARCHIVED, wordEntry.isArchived());
        values.put(WordContract.Entry.COLUMN_NAME_WEIGHT, wordEntry.getWeight());

        return getWritableDatabase().update(
                WordContract.Entry.TABLE_NAME,
                values,
                WordContract.Entry._ID + "=?",
                new String[]{String.valueOf(wordEntry.getId())}
        ) > 0;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + WordContract.Entry.TABLE_NAME); // TODO easy, boy
//        onCreate(db);
        // todo unit for migration
        if (oldVersion == 1 && newVersion == 2) {
            db.beginTransaction();
            try {
                db.execSQL("ALTER TABLE " + WordContract.Entry.TABLE_NAME + " ADD COLUMN " +
                        WordContract.Entry.COLUMN_NAME_IS_ARCHIVED + " INTEGER DEFAULT 0");
                db.execSQL("ALTER TABLE " + WordContract.Entry.TABLE_NAME + " ADD COLUMN " +
                        WordContract.Entry.COLUMN_NAME_WEIGHT + " INTEGER DEFAULT 0");
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    /**
     * Возвращает все хранящиеся в базе записи о словах.
     *
     * @return все хранящиеся записи о словах
     */
    public Cursor getAllWordsEntries() {
        return getWordsEntries(
                WordContract.Entry.COLUMN_NAME_WEIGHT + ", " + WordContract.Entry.COLUMN_NAME_WORD);
    }

    public Cursor getWordsEntries(final String sortingColumn) {
        return getWordsEntries(sortingColumn, null, null);
    }

    /**
     * Возвращает все хранящиеся в базе записи о словах.
     *
     * @return все хранящиеся записи о словах
     */
    public Cursor getWordsEntries(final String sortingColumn,
                                  @Nullable String selection,
                                  @Nullable String[] selectionArgs) {
        final String[] columns = {
                WordContract.Entry.COLUMN_NAME_WORD,
                WordContract.Entry.COLUMN_NAME_TRANSLATION,
                WordContract.Entry._ID,
                WordContract.Entry.COLUMN_NAME_IS_ARCHIVED,
                WordContract.Entry.COLUMN_NAME_WEIGHT,
        };
        return getReadableDatabase().query(
                WordContract.Entry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
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
        return findFirstEntry(WordContract.Entry.COLUMN_NAME_WORD + "=?", new String[]{word});
    }

    /**
     * Loads the word entry by it's id.
     *
     * @param id entry id, not null
     * @return word entry with such id or empty object, if there is no one
     */
    public Optional<WordEntry> getEntryById(@NonNull final Long id) {
        return findFirstEntry(WordContract.Entry._ID + "=?", new String[]{id.toString()});
    }


    /**
     * Создаёт в БД запись о слове
     *
     * @return созданную запись или {@code null}, если запись не была создана
     */
    @Nullable
    public WordEntry saveWordEntry(@NonNull WordEntry wordEntry) {
        WordEntry result = null;

        final SQLiteDatabase dbWordsWr = getWritableDatabase();
        long id = insertWordEntry(wordEntry, dbWordsWr);
        if (id > 0) {
            result = new WordEntry(
                    wordEntry.getWord(),
                    wordEntry.getTranslation(),
                    wordEntry.getWeight(), wordEntry.isArchived(), id
            );
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
     * @return ID добавленной записи в таблице или -1, если запись не была добавлена
     */
    private long insertWordEntry(
            @NonNull WordEntry entry,
            @NonNull SQLiteDatabase dbWordsWr) {

        ContentValues values = new ContentValues();
        values.put(WordContract.Entry.COLUMN_NAME_WORD, entry.getWord());
        values.put(WordContract.Entry.COLUMN_NAME_TRANSLATION, entry.getTranslation());
        values.put(WordContract.Entry.COLUMN_NAME_IS_ARCHIVED, entry.isArchived());
        values.put(WordContract.Entry.COLUMN_NAME_WEIGHT, entry.getWeight());

        Log.d(TAG, "Adding new word: " + entry);

        return dbWordsWr.insert(WordContract.Entry.TABLE_NAME, null, values);
    }

    private Optional<WordEntry> findFirstEntry(final String selection,
                                               final String[] selectionArgs) {
        final String[] columns = {
                WordContract.Entry.COLUMN_NAME_WORD,
                WordContract.Entry.COLUMN_NAME_TRANSLATION,
                WordContract.Entry.COLUMN_NAME_WEIGHT,
                WordContract.Entry.COLUMN_NAME_IS_ARCHIVED,
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
        // todo one time from db
        return new WordEntry(
                cursor.getString(cursor.getColumnIndex(WordContract.Entry.COLUMN_NAME_WORD)),
                cursor.getString(cursor.getColumnIndex(WordContract.Entry.COLUMN_NAME_TRANSLATION)),
                cursor.getInt(cursor.getColumnIndex(WordContract.Entry.COLUMN_NAME_WEIGHT)),
                cursor.getInt(cursor.getColumnIndex(WordContract.Entry.COLUMN_NAME_IS_ARCHIVED)) == 1,
                cursor.getLong(cursor.getColumnIndex(WordContract.Entry._ID))
        );
    }

    public Cursor getNotArchivedWordsEntries() {
        return getWordsEntries(
                WordContract.Entry.COLUMN_NAME_WORD + ", " + WordContract.Entry.COLUMN_NAME_TRANSLATION,
                WordContract.Entry.COLUMN_NAME_IS_ARCHIVED + "=0",
                null
        );
    }
}
