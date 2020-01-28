package com.halo.dictionary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.halo.dictionary.sql.WordContract;
import com.halo.dictionary.sql.WordDbHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.DomainCombiner;

public class DumpWordsAsyncTask extends AsyncTask<WordDbHelper, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private final Context mContext;

    public DumpWordsAsyncTask(final Context context) {
        this.mContext = context;
    }

    @Override
    protected final Void doInBackground(final WordDbHelper... dbHelpers) {

        Utils.dumpWordsToExternalStorage(this.mContext);

        return null;
    }

    @Override
    protected void onPostExecute(final Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(this.mContext, "Done", Toast.LENGTH_LONG).show();
    }
}
