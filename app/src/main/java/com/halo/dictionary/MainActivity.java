/*
 * TODO s:
 * - удобная красивая таблица
 * - различные сортировки (в том числе случайные)
 * - представления ру-анг/анг-ру
 * - поиск
 * - случайный отбор 5-10-20 слов для проверки
 * - подсказка по таймеру (в уведомления) - firebase
 * - синхронизация?
 */

package com.halo.dictionary;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.halo.dictionary.rcclrview.WordsRcclrViewAdapter;
import com.halo.dictionary.sql.WordDbHelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private RecyclerView mRcclrViewWords;
    private RecyclerView.Adapter mRcclrViewWordsAdapter;
    private RecyclerView.LayoutManager mRcclrViewWordsLayoutManager;
    private FloatingActionButton mFabAddWord;
    private FloatingActionButton mFabDumpWords;
    private FloatingActionButton mFabDumpWordsThroughService;

    private WordDbHelper mDbHelperWords;
    private ResultReceiverImpl mReceiver;
    private ResultReceiverCallback mReceiverCallback;
    private Handler handler;

    private static final String TAG = "MainActivity";

    @Override
    protected void onDestroy() {
        mDbHelperWords.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Long position = mDbHelperWords.getLatestWordIndex();
//        if (position != null) {
//            mRcclrViewWords.getLayoutManager().scrollToPosition(position.intValue());
//        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRcclrViewWords = (RecyclerView) findViewById(R.id.recycler_view_words);
        mRcclrViewWords.setHasFixedSize(true); // Improve performance
        mRcclrViewWordsLayoutManager = new LinearLayoutManager(this);
        mRcclrViewWords.setLayoutManager(mRcclrViewWordsLayoutManager);

        mDbHelperWords = WordDbHelper.getInstance(this);

        final Cursor cursor = mDbHelperWords.getAllWordsEntries();
        mRcclrViewWordsAdapter = new WordsRcclrViewAdapter(cursor, this);
        mRcclrViewWords.setAdapter(mRcclrViewWordsAdapter);

        Toast.makeText(this, getDatabasePath(WordDbHelper.DATABASE_NAME).getPath(), Toast.LENGTH_LONG).show();

        /*
         * Remove word entry swiping
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(
                    RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                View view = viewHolder.itemView;
                String idString = ((TextView) view.findViewById(R.id.tv_c_rcclr_view_words_id)).getText().toString();
                Long id = Long.parseLong(idString);
                mDbHelperWords.removeWordEntry(id);
                WordsRcclrViewAdapter.getInstance().swapCursor(mDbHelperWords.getAllWordsEntries());
            }

        }).attachToRecyclerView(mRcclrViewWords);

        this.mFabAddWord = findViewById(R.id.button_add);
        this.mFabAddWord.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddWordActivity.class);
            startActivity(intent);
        });


        this.mFabDumpWords = findViewById(R.id.button_dump_to_file);
        this.mFabDumpWords.setOnClickListener(v -> {
            if (checkPermission()) {
                dumpWordsToFile();
            } else {
                requestPermission();
            }
        });

        this.mFabDumpWordsThroughService = findViewById(R.id.button_dump_to_file_through_service);
        this.mFabDumpWordsThroughService.setOnClickListener(v -> {
            if (checkPermission()) {
                startDumpWordsToFileService();
            } else {
                requestPermission();
            }
        });

        this.handler = new Handler();
    }

    private void startDumpWordsToFileService() {
        final Intent intent = new Intent(this, DumpFileWordsService.class);
        this.mReceiver = new ResultReceiverImpl<>(this.handler, new DumpedToFileResultReceiver(this));
        intent.putExtra("RECEIVER", this.mReceiver);
        intent.setAction("DUMP_WORDS");
        startService(intent);
    }

    private void dumpWordsToFile() {
        new DumpWordsAsyncTask(getApplicationContext()).execute(this.mDbHelperWords);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return (result == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to create files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
                Log.w("value", "Permission Denied, You cannot use local drive .");
            }
        }
    }

    public interface ResultReceiverCallback<T> {
        void onSuccess(T data);
        void onError(Exception e);
    }

    public static class DumpedToFileResultReceiver implements ResultReceiverCallback<Boolean> {

        private final Activity activity;

        public DumpedToFileResultReceiver(final Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onSuccess(final Boolean data) {
            Toast.makeText(activity, "OK", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(final Exception e) {
            Toast.makeText(activity, "НЕ OK", Toast.LENGTH_LONG).show();
        }
    }

    public class ResultReceiverImpl<T> extends ResultReceiver {

        private ResultReceiverCallback<T> callback;

        ResultReceiverImpl(final Handler handler, final ResultReceiverCallback<T> callback) {
            super(handler);
            this.callback = callback;
        }

        @Override
        protected void onReceiveResult(final int resultCode, final Bundle resultData) {
            if (resultCode == 0) {
                this.callback.onSuccess(null);
            } else {
                this.callback.onError(null);
            }
        }
    }

}
