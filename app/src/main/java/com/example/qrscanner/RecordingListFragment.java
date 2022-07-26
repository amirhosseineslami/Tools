package com.example.qrscanner;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class RecordingListFragment extends Fragment implements View.OnClickListener, RecyclerAdapter.RecyclerViewOnItemClick, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    private RecyclerView recyclerView;
    private BottomSheetBehavior behavior;
    private View bottomSheet;
    private ImageButton playPauseBtn;
    private SeekBar seekBar;
    private TextView voiceNameTxt;
    private String path;
    private RecyclerAdapter adapter;
    private File[] files;
    private File directory;
    private File currentPlayingFile;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private final Handler handler=new Handler();
    private Timer timer;

    public RecordingListFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_voice_list,container,false);
        init(rootView);
        adapter.setRecyclerViewOnItemClick(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        return rootView;
    }

    private void init(View rootView) {

        recyclerView = rootView.findViewById(R.id.voice_recyclerView);
        bottomSheet = rootView.findViewById(R.id.my_bottomSheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        playPauseBtn = rootView.findViewById(R.id.bottomSheetBtn);
        seekBar = rootView.findViewById(R.id.bottomSheet_seekBar);
        voiceNameTxt = rootView.findViewById(R.id.textView_bottomSheet);
        mediaPlayer.setOnCompletionListener(this);


        path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        directory = new File(path);
        files = directory.listFiles();
        adapter = new RecyclerAdapter(files);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }else {
                mediaPlayer.start();
                playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);
            }

    }

    @Override
    public void onItemClickListener(File file, int position) {
        playPauseBtn.setOnClickListener(this);
        if ((mediaPlayer!=null && mediaPlayer.isPlaying())||currentPlayingFile!=file) {
            mediaPlayer.reset();
        }

            try {
                currentPlayingFile = file;
                mediaPlayer.setDataSource(file.getAbsolutePath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                voiceNameTxt.setVisibility(View.VISIBLE);
                voiceNameTxt.setText(file.getName());


            } catch (IOException e) {
                e.printStackTrace();
            }
        seekBar.setMax(mediaPlayer.getDuration());
            seekBar.setOnSeekBarChangeListener(this);
            if (timer == null){
                timer = new Timer();
            }
            timer.schedule(new MyTimerTask(),0,500);

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        seekBar.setProgress(0);
        playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (b) {
            mediaPlayer.seekTo(i);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    class MyTimerTask extends TimerTask {


        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaPlayer!=null && timer!=null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            timer.cancel();
        }
        mediaPlayer = null;
        timer = null;
    }

}
