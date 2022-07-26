package com.example.qrscanner;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Application;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordFragment extends Fragment implements View.OnClickListener {
    private Chronometer chronometer;
    private MaterialButton recordBtn;
    private ImageView listImageView;
    private boolean isRecording = false;
    private ValueAnimator valueAnimator;
    private MediaRecorder mediaRecorder;
    private String path;
    private String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO};



    public RecordFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_record, container, false);
        init(rootView);
        return rootView;
    }

    void init(View rootView) {
        chronometer = rootView.findViewById(R.id.chronometer);
        listImageView = rootView.findViewById(R.id.imageView_list);
        recordBtn = rootView.findViewById(R.id.button_startRecording);
        listImageView.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
        recordBtn.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.record_button_start));
        path = getActivity().getExternalFilesDir("/").getAbsolutePath();
    }

    @Override
    public void onClick(View view) {
        if (view == listImageView) {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out)
                    .replace(R.id.my_fragment_container,new RecordingListFragment(), String.valueOf(R.string.fragment_voice_list_tag))
                    .addToBackStack(getTag())
                    .commit();


        }

        else if (view == recordBtn) {
            if (!isRecording) {
                    try {
                        startRecording();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            } else {
                stopRecording();
            }

        }
    }

    private void startRecording() throws IOException {
        recordBtn.getAnimation().cancel();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        recordBtn.setText(R.string.fragmentRecord_button_stopRecording);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.US);
        Date thisMomentDate = new Date();
        String name = "Recording "+simpleDateFormat.format(thisMomentDate)+".3gp";

        mediaRecorder.setOutputFile(path+"/"+name);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.prepare();
        mediaRecorder.start();

        valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator()
                , ContextCompat.getColor(getActivity(), R.color.animation_color_item1)
                , ContextCompat.getColor(getActivity(), R.color.animation_color_item2)
                , ContextCompat.getColor(getActivity(), R.color.animation_color_item3));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                recordBtn.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
            }
        });
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.start();
        isRecording = true;
    }

    private void stopRecording() {

        mediaRecorder.stop();
        mediaRecorder.release();

        valueAnimator.cancel();
        recordBtn.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.record_button_start));
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        recordBtn.setText(R.string.fragmentRecord_button_startRecording);
        isRecording = false;

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaRecorder!=null && isRecording){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder=null;
        }
    }
}
