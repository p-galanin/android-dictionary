package com.halo.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import com.halo.dictionary.sql.WordContract;
import com.halo.dictionary.sql.WordDbHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class Utils {

    public static void dumpWordsToExternalStorage(final Context context) {

        final WordDbHelper dbHelper = WordDbHelper.getInstance(context);

        final File sdcard = Environment.getExternalStorageDirectory();
        final File dir = new File(sdcard.getAbsolutePath() + "/text" + System.currentTimeMillis() + "/");

        if (!dir.mkdir()) {
            Log.e("oops", "");
            throw new RuntimeException("Unable to create dir");
        }

        final File file = new File(dir, "dump.txt");

        try (FileOutputStream os = new FileOutputStream(file)) {

            os.write("Started\n".getBytes());

            final Cursor wordsCursor = dbHelper.getAllWordsEntries();
            final int columnWord = wordsCursor.getColumnIndex(WordContract.WordEntry.COLUMN_NAME_WORD);
            final int columnTrns = wordsCursor.getColumnIndex(WordContract.WordEntry.COLUMN_NAME_TRANSLATION);
            for (wordsCursor.moveToFirst(); !wordsCursor.isAfterLast(); wordsCursor.moveToNext()) {
                os.write((wordsCursor.getString(columnWord) + "###" + wordsCursor.getString(columnTrns) + "\n").getBytes(StandardCharsets.UTF_8));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
