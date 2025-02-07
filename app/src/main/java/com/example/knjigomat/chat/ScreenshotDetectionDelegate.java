package com.example.knjigomat.chat;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;

import java.lang.ref.WeakReference;

public class ScreenshotDetectionDelegate {
    private WeakReference<Activity> activityWeakReference;
    private ScreenshotDetectionListener listener;
    private ContentObserver contentObserver = new ContentObserver(new Handler()) {
        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (isReadExternalStoragePermissionGranted()) {
                String path = getFilePathFromContentResolver(activityWeakReference.get(), uri);
                if (isScreenshotPath(path)) {
                    onScreenCaptured(path);
                }
            } else {
                onScreenCapturedWithDeniedPermission();
            }
        }
    };

    public ScreenshotDetectionDelegate(Activity activityWeakReference, ScreenshotDetectionListener listener) {
        this.activityWeakReference = new WeakReference<>(activityWeakReference);
        this.listener = listener;
    }

    public void startScreenshotDetection() {
        activityWeakReference.get()
                .getContentResolver()
                .registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, contentObserver);
    }

    public void stopScreenshotDetection() {
        activityWeakReference.get().getContentResolver().unregisterContentObserver(contentObserver);
    }

    private void onScreenCaptured(String path) {
        if (listener != null) {
            listener.onScreenCaptured(path);
        }
    }

    private void onScreenCapturedWithDeniedPermission() {
        if (listener != null) {
            listener.onScreenCapturedWithDeniedPermission();
        }
    }

    private boolean isScreenshotPath(String path) {
        return path != null && path.toLowerCase().contains("screenshots");
    }

    private String getFilePathFromContentResolver(Context context, Uri uri) {
        try {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATA
            }, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                String path = cursor.getString(index);
                cursor.close();
                return path;
            }
        } catch (IllegalStateException ignored) {
        }
        return null;
    }

    private boolean isReadExternalStoragePermissionGranted() {
        PackageManager pm = activityWeakReference.get().getPackageManager();
        return pm.checkPermission(android.Manifest.permission.READ_MEDIA_IMAGES, activityWeakReference.get().getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }

    public interface ScreenshotDetectionListener {
        void onScreenCaptured(String path);

        void onScreenCapturedWithDeniedPermission();
    }
}