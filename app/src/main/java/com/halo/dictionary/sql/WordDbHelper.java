package com.halo.dictionary.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.halo.dictionary.WordEntry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Класс для управления обменом данными с БД
 *
 * Created by halo on 17.09.2017.
 */

public class WordDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "words.db";

    private Long latestWordIndex;

    private static WordDbHelper INSTANCE;

    private static final int DATABASE_VERSION = 1;

    /**
     * Создаёт экземпляр
     * @param context
     */
    private WordDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_WORDS_TABLE =
                "CREATE TABLE " + WordContract.WordEntry.TABLE_NAME + " (" +
                 WordContract.WordEntry._ID                      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                 WordContract.WordEntry.COLUMN_NAME_WORD         + " VARCHAR(63) NOT NULL, " +
                 WordContract.WordEntry.COLUMN_NAME_TRANSLATION  + " VARCHAR(511)" +
                 ");";

        sqLiteDatabase.execSQL(SQL_CREATE_WORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WordContract.WordEntry.TABLE_NAME);
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
     * Добавляет в БД запись о слове, а также фиксирует его индекс,
     * который можно получить в {@link #getLatestWordIndex()}
     *
     * @param word - слово
     * @param translation - перевод
     *
     * @return созданный объект записи о слове или {@code null}, если запись не была создана
     */
    @Nullable
    public WordEntry createWordEntryAndSetLatestIndex(@NonNull final String word, @Nullable final String translation) {
        final WordEntry newWordEntry = this.createWordEntry(word, translation);
        if (newWordEntry != null) {
            this.setLatestWordIndex(this.getOrderedIndex(newWordEntry.getId()));
        }
        return newWordEntry;
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
        Log.d("Remove " + id, "");
        this.getWritableDatabase().delete(
                WordContract.WordEntry.TABLE_NAME, WordContract.WordEntry._ID + "='" + id + "'", null);
    }

    /**
     * Возвраащает индекс последней добавленной записи о слове
     *
     * @return индекс последней добавленной записи о слове или {@code null}, если индекс сброшен
     */
    @Nullable
    public Long getLatestWordIndex() {
        return this.latestWordIndex;
    }

    /**
     * Получить экземпляр {@link WordDbHelper}
     *
     * @return {@link WordDbHelper} или {@code null}, если экземпляр ещё не создан
     */
    @Nullable
    public static synchronized WordDbHelper getInstance() {
        return INSTANCE;
    }

    /**
     * Получить экземпляр {@link WordDbHelper} или создать новый, если экземпляр ещё не создан
     *
     * @param context - контекст, с которым будет создан экземпляр, если до этого ещё не создан
     *
     * @return {@link WordDbHelper}
     */
    @NonNull
    public static synchronized WordDbHelper getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new WordDbHelper(context);
        }
        return INSTANCE;
    }

    /**
     * Возвращает индекс записи в отсортированной таблице по индексу в таблице из хранилища
     *
     * @param id - идентификатор записи в БД
     *
     * @return индекс записи слова в отсортированной таблице или -1, если слово не найдено
     */
    private long getOrderedIndex(long id) {
        long result = 0;

        Cursor cursor = this.getAllWordsEntries();
        while (cursor.moveToNext()) {
            if (id == cursor.getLong(cursor.getColumnIndex(WordContract.WordEntry._ID))) {
                return result;
            }
            result++;
        }

        return -1;
    }

    /**
     * Добавление записи о слове в переданную базу.
     *
     * @param word - слово
     * @param translation - перевод слова
     * @param dbWordsWr - база данных для внесения записи
     *
     * @return ID добавленной записи в таблице или -1, если запись не была добавлена
     */
    private long createWordEntry(
            @NonNull final String word, final String translation, @NonNull SQLiteDatabase dbWordsWr) {

        ContentValues values = new ContentValues();
        values.put(WordContract.WordEntry.COLUMN_NAME_WORD, word);
        values.put(WordContract.WordEntry.COLUMN_NAME_TRANSLATION, translation);

        Log.d("Adding new word: " + word + " (" + translation + ")", "");

        return dbWordsWr.insert(WordContract.WordEntry.TABLE_NAME, null, values);
    }

    /**
     * Устанавливает индекс последнего добавленной записи о слове
     *
     * @param newIndex - новый индекс последнего добавленного слова
     */
    private synchronized void setLatestWordIndex(Long newIndex) {
        this.latestWordIndex = newIndex;
    }

}
