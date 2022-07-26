package com.example.qrscanner;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

public class VoiceRecorderActivity extends AppCompatActivity {
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recorder);
        init();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.my_fragment_container,new RecordFragment(), String.valueOf(R.string.fragment_record_tag))
                    .commit();
        }
    }



    void init(){
        frameLayout = findViewById(R.id.my_fragment_container);
    }
}