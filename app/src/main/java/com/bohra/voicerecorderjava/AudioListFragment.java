package com.bohra.voicerecorderjava;


import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class AudioListFragment extends Fragment implements AudioListAdapter.onItemClick {

    private ConstraintLayout playerSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView audioListRecyclerview;
    //now we need to to get list of audio that we have in our directory
    private File[] files;
    private AudioListAdapter audioListAdapter;
    // this will contain the file that we wiil click to play
    private File fileToPlay;
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;
    private ImageButton player_play_btn;
    private TextView player_header_title, player_filename;
    private SeekBar player_seekbar;

    //to make seekbar sync with media player in real time we need handler and runnable
    private Handler seekbarHandler;
    //using runnable to updateseekbar;
    private Runnable updateSeekbar;

    public AudioListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playerSheet = view.findViewById(R.id.player_sheet);
        audioListRecyclerview = view.findViewById(R.id.audioListRecyclerview);
        player_filename = view.findViewById(R.id.player_filename);
        player_header_title = view.findViewById(R.id.player_header_title);
        player_play_btn = view.findViewById(R.id.player_play_btn);
        player_seekbar = view.findViewById(R.id.player_seekbar);

        //this is the path where we store our audio
        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        // now we are getting the directory
        File directory = new File(path);
        //now we will get all the files in the directory
        files = directory.listFiles();

        // here we are passing list of all files that we goes to adapter
        //the second parameter is click for which below we implemnted the method i.e onClickItemListner
        audioListAdapter = new AudioListAdapter(files, this);

        audioListRecyclerview.setHasFixedSize(true);
        audioListRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        audioListRecyclerview.setAdapter(audioListAdapter);


        bottomSheetBehavior = BottomSheetBehavior.from(playerSheet);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                //if we try to drag playerSheet down out of sccreen it will come back again in top of screen
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        player_play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPlaying) {
                    pauseAudio();
                } else {
                    //when user click the play button ans file isnt playing we need to check if we had file to play or not
                    if (fileToPlay != null) {
                        resumeAudio();
                    }
                }
            }
        });

        player_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // here it will pause the audio
                pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // it will resume the audio bcz we need to change the progess on stopTacking touch
                // getting the progress from seekbar where the user has left
                int progress = seekBar.getProgress();
                //mediaPlayer will start from the progress provided here
                mediaPlayer.seekTo(progress);
                resumeAudio();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });

    }

    @Override
    public void onClickItemListner(File file, int position) {
        Log.d("Play_log", "File_Playing " + file.getName());
        // here we are getting the file we clicked now we can play that file
        if (isPlaying) {
            // if audio is already playing then we need to stop the audio and play the new audio that is clicke
            stopAudio();
            //playing the audio
            playAudio(fileToPlay);
        } else {

            // here we are using fileToPlay bcz once we click on pause button we dont want to click on list of file to play
            //from ther only we can play the file
            fileToPlay = file;
            // if audio is not playing then we have to play audio and we need to pass the audio to be played
            playAudio(fileToPlay);
        }

    }

    //stop audio
    private void stopAudio() {
        //changing the play button icon
        player_play_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_play_btn, null));
        player_header_title.setText("Stopped");

        isPlaying = false;
        mediaPlayer.stop();

        //handler should be null once we stop or stop the audio
        //whenever we click the stop button ito gone a stop the handler
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    // play the Audio
    private void playAudio(File fileToPlay) {
        mediaPlayer = new MediaPlayer();
        //once a audio start playing we will change the behaviour of bottomSheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //changing the play button icon
        player_play_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_pause_btn, null));
        player_filename.setText(fileToPlay.getName());
        player_header_title.setText("Playing");

        isPlaying = true;

        //handing when audio is completed
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
                player_header_title.setText("Finished");
                player_play_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_play_btn, null));
            }
        });

        //we will get the current progress of media player and we will asign to the seekbar in realTime
        // setting the maximum length of seeekbar depend on duration of song
        player_seekbar.setMax(mediaPlayer.getDuration());
        seekbarHandler = new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);
    }

    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                //setting the current progress
                player_seekbar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 500);
            }
        };
    }

    private void pauseAudio() {
        mediaPlayer.pause();
        isPlaying = false;
        player_play_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_play_btn, null));

        //handler should be null once we stop or pause the audio
        //whenever we click the pause button ito gone a stop the handler
        seekbarHandler.removeCallbacks(updateSeekbar);
        player_header_title.setText("Paused");
    }

    private void resumeAudio() {
        mediaPlayer.start();
        isPlaying = true;
        player_play_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_pause_btn, null));

        //in resume audio we need to set the handler again or progress will not update
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar,0);
        player_header_title.setText("Playing");

    }

    // if the fragment stop we need to stop the audio also
    @Override
    public void onStop() {
        super.onStop();
        stopAudio();
    }
}
