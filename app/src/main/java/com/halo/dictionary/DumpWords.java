package com.halo.dictionary;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.halo.dictionary.sql.WordDbHelper;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class DumpWords {

    private static final int PERMISSION_REQUEST_CODE = 100;


    private FloatingActionButton mFabDumpWords;
    private FloatingActionButton mFabDumpWordsThroughService;
    private ResultReceiverImpl mReceiver;
    private ResultReceiverCallback mReceiverCallback;
    private Handler handler;
    private WordDbHelper mDbHelperWords;
    private Activity activity;



    public void doDump(final Activity activity, WordDbHelper wordDbHelper) {
        this.activity = activity;
        this.mDbHelperWords = wordDbHelper;
        this.mFabDumpWords = activity.findViewById(R.id.button_dump_to_file);
        this.mFabDumpWords.setOnClickListener(v -> {
            if (checkPermission()) {
                dumpWordsToFile();
            } else {
                requestPermission();
            }
        });

        this.mFabDumpWordsThroughService = activity.findViewById(R.id.button_dump_to_file_through_service);
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
        final Intent intent = new Intent(activity, DumpFileWordsService.class);
        this.mReceiver = new ResultReceiverImpl<>(this.handler, new DumpedToFileResultReceiver(activity));
        intent.putExtra("RECEIVER", this.mReceiver);
        intent.setAction("DUMP_WORDS");
        activity.startService(intent);
    }

    private void dumpWordsToFile() {
        new DumpWordsAsyncTask(this.activity.getApplicationContext()).execute(this.mDbHelperWords);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this.activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return (result == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this.activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this.activity, "Write External Storage permission allows us to create files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this.activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.e("value", "Permission Granted, Now you can use local drive .");
//            } else {
//                Log.w("value", "Permission Denied, You cannot use local drive .");
//            }
//        }
//    }


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


    public interface ResultReceiverCallback<T> {
        void onSuccess(T data);
        void onError(Exception e);
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
