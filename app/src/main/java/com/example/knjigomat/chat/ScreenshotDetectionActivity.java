package com.example.knjigomat.chat;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public abstract class ScreenshotDetectionActivity extends AppCompatActivity implements ScreenshotDetectionDelegate.ScreenshotDetectionListener {
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 3009;
    private ScreenshotDetectionDelegate screenshotDetectionDelegate = new ScreenshotDetectionDelegate(this, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkReadExternalStoragePermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        screenshotDetectionDelegate.startScreenshotDetection();
    }

    @Override
    protected void onStop() {
        super.onStop();
        screenshotDetectionDelegate.stopScreenshotDetection();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showReadExternalStoragePermissionDeniedMessage();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onScreenCaptured(String path) {
        // Do something when screen was captured
    }

    @Override
    public void onScreenCapturedWithDeniedPermission() {
        // Do something when screen was captured but read external storage permission has denied
    }

    private Boolean checkReadExternalStoragePermission() {
        PackageManager pm = getApplicationContext().getPackageManager();
        return pm.checkPermission(android.Manifest.permission.READ_MEDIA_IMAGES, getApplicationContext().getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }

    private void showReadExternalStoragePermissionDeniedMessage() {
        Toast.makeText(this, "Read external storage permission has denied", Toast.LENGTH_SHORT).show();
    }
}
