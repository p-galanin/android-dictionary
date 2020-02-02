package com.halo.dictionary.mvp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.halo.dictionary.repository.sql.WordContract;
import com.halo.dictionary.repository.sql.WordDbHelper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Utils {

    private static final String TAG = "Utils";

    public static boolean hasPermission(final Context context, final String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(final Activity activity, final String permission, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            Toast.makeText(activity, "Permission is required to perform this action", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }

    public static InputStream getStreamFromUri(final Context context, final Uri fileUri) {
        try {
            return context.getContentResolver().openInputStream(fileUri);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Unable to open stream for " + fileUri.toString());
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    public static Optional<String> getDirectoryPathFromUri(@NonNull final Uri uri) {
        if (uri.getPath() != null && "com.android.externalstorage.documents".equals(uri.getAuthority())) {
            return Optional.of(Environment.getExternalStorageDirectory() + "/" + uri.getPath().split(":")[1]);
        } else {
            return Optional.empty();
        }
    }
}
