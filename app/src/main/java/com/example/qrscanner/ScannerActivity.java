package com.example.qrscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class ScannerActivity extends AppCompatActivity implements DialogInterface.OnClickListener {
    private CodeScannerView codeScannerView;
    private CodeScanner codeScanner;
    public static final String RESULT_SCAN_KEY = "100";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        init();
        codeScanner.setDecodeCallback(decodeCallback);
    }
    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }
    DecodeCallback decodeCallback = new DecodeCallback() {
        @Override
        public void onDecoded(@NonNull Result result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(ScannerActivity.this)
                            .setTitle(R.string.scanner_dialog_title)
                            .setMessage(R.string.scanner_dialog_message+"\n"+result.getText())
                            .setPositiveButton(R.string.scanner_dialog_positive_button_text, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent();
                                    intent.putExtra(RESULT_SCAN_KEY,result.getText());
                                    setResult(RESULT_OK,intent);
                                    finish();
                                }
                            })
                            .setNegativeButton(R.string.scanner_dialog_negative_button_text, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    codeScanner.startPreview();
                                }
                            })
                            .show();
                }
            });
        }
    };






    void init(){
        codeScannerView = findViewById(R.id.code_scanner_view);
        codeScanner = new CodeScanner(this,codeScannerView);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }
}