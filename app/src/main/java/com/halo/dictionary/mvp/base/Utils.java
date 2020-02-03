package com.halo.dictionary.mvp.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Utils {

    private static final String TAG = "Utils";

    /**
     * Resolve directory path from it's URI.
     * Only external storage supported now.
     *
     * @param uri directory URI, not null
     * @return directory path which can be used to create files in it or empty object if URI is unsupported now
     */
    public static Optional<String> getDirectoryPathFromUri(@NonNull final Uri uri) {
        final Optional<String> result;
        // TODO Get rid of deprecations
        if (uri.getPath() != null && "com.android.externalstorage.documents".equals(uri.getAuthority())) {
            result = Optional.of(Environment.getExternalStorageDirectory() + "/" + uri.getPath().split(":")[1]);
        } else if (uri.getPath() != null && "com.android.providers.downloads.documents".equals(uri.getAuthority())) {
            result = Optional.of(Environment.getExternalStorageDirectory() + "/Download");
        } else {
            result = Optional.empty();
        }

        return result;
    }

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

    public static InputStream getStreamFromUri(@NonNull final Context context, @NonNull final Uri fileUri) {
        try {
            return context.getContentResolver().openInputStream(fileUri);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Unable to open stream for " + fileUri.toString());
            return new ByteArrayInputStream(new byte[0]);
        }
    }

}
