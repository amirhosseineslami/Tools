package com.example.qrscanner;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MaterialButton startScanningButton, startVoiceRecordingBtn;
    private TextView scannerInstructionTxt, scannerDetailsLinkTxt, recorderInstructionTxt, recorderLastDateTxt;
    private String[] permissions = new String[]{Manifest.permission.CAMERA};
    private String[] permissions2 = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
    public static final int PERMISSION_CAMERA_REQ_CODE = 100;
    public static final int OPEN_CAMERA_REQ_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    void init() {
        startScanningButton = findViewById(R.id.button_scan);
        scannerDetailsLinkTxt = findViewById(R.id.textView_details_link);
        scannerInstructionTxt = findViewById(R.id.textView_instruction);
        recorderInstructionTxt = findViewById(R.id.textView_voiceRecorder_instruction);
        recorderLastDateTxt = findViewById(R.id.textView_last_recording_time);
        startVoiceRecordingBtn = findViewById(R.id.button_recorder);

        startScanningButton.setOnClickListener(this);
        scannerDetailsLinkTxt.setOnClickListener(this);
        startVoiceRecordingBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == startScanningButton) {
            if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                requestCameraPermission();
            }
        } else if (view == scannerDetailsLinkTxt) {
            if (!scannerDetailsLinkTxt.getText().toString().trim().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scannerDetailsLinkTxt.getText().toString()));
                startActivity(intent);
            }
        } else if (view == startVoiceRecordingBtn) {
            if (ActivityCompat.checkSelfPermission(this, permissions2[0]) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permissions2[1]) == PackageManager.PERMISSION_GRANTED)
            {
                startActivity(new Intent(MainActivity.this, VoiceRecorderActivity.class));
            }
            else{
                requestRecordingPermission();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CAMERA_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                requestCameraPermission();
            }
        } else if (requestCode == R.string.record_fragment_permission_req_code) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this,VoiceRecorderActivity.class));
            }else {
                requestRecordingPermission();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_CAMERA_REQ_CODE);
    }

    private void openCamera() {
        Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
        startActivityForResult(intent, OPEN_CAMERA_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_CAMERA_REQ_CODE && resultCode == RESULT_OK) {
            scannerInstructionTxt.setVisibility(View.VISIBLE);
            scannerDetailsLinkTxt.setText(data.getStringExtra(ScannerActivity.RESULT_SCAN_KEY));
        }
    }

    private boolean checkPermission() {
        boolean isPermissionsGranted = ActivityCompat.checkSelfPermission(this, permissions2[0]) != PackageManager.PERMISSION_GRANTED;
        if (ActivityCompat.checkSelfPermission(this, permissions2[1]) == PackageManager.PERMISSION_DENIED) {
            isPermissionsGranted = false;
        }
        if (!isPermissionsGranted) {
            Log.d(TAG, "checkPermission: ");
            ActivityCompat.requestPermissions(this, permissions2, R.string.record_fragment_permission_req_code);
        }

        return isPermissionsGranted;
    }

    private void requestRecordingPermission() {
        ActivityCompat.requestPermissions(this, permissions2, R.string.record_fragment_permission_req_code);
    }
}