package com.bohra.voicerecorderjava;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {

    private NavController navController;
    private ImageView record_list_button, record_button;
    private TextView fileNameText;
    private boolean isRecording = false;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PermissionCode = 23;
    private MediaRecorder mediaRecorder;
    private Chronometer record_timer;

    //we need a variable to store the file
    private String recordFile;


    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        record_list_button = view.findViewById(R.id.record_list_button);
        record_button = view.findViewById(R.id.record_button);
        record_timer = view.findViewById(R.id.record_timer);
        fileNameText = view.findViewById(R.id.record_filename);

        record_list_button.setOnClickListener(this);
        record_button.setOnClickListener(this);
    }

    // handling the click event
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.record_list_button:
                Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();

                if (isRecording) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                            isRecording = false;
                        }
                    });
                    alertDialog.setNegativeButton("Cancel", null);

                    alertDialog.setTitle("Audio still recording");
                    alertDialog.setMessage("Are sure you want to stop the recording");
                    alertDialog.create().show();

                } else {
                    navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                }

                break;
            case R.id.record_button:
                // if it is already recording and we click the button then we have to set it false to stop recording
                if (isRecording) {
                    isRecording = false;
                    record_button.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_stopped));
                    // when stop recording button is clicked then stopRecording method is called
                    stopRecording();

                } else {

                    //Checking the recording permission permission
                    if (checkpermission()) {
                        // if it is not recording and we click the button then we have to set it true to start recording
                        isRecording = true;
                        record_button.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_recording));

                        // when stop recording button is clicked then startRecording method is called
                        startRecording();
                    }
                }
                break;
        }
    }

    private void startRecording() {
        // it will reset the timer and start to 0
        // if user press start and few second later stop and again he press start the time will reset to 0
        record_timer.setBase(SystemClock.elapsedRealtime());
        // when user will press start button timer will start
        record_timer.start();

        //we need the path where we can store the file
        // here '/' is to save file to root
        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date date = new Date();
        recordFile = "Recording" + dateFormat.format(date) + ".3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //here we need to provide path to the file
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        //preparing the recording
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // it will start recording
        mediaRecorder.start();

        fileNameText.setText("Recording_File_Name : " + recordFile);

    }

    private void stopRecording() {
        // when user will press start button timer will stop
        record_timer.stop();

        //stopping media recording
        mediaRecorder.stop();

        //we also need the file
        mediaRecorder.release();
        //if user press record button again it initalize new media recoder and new file will recorded
        mediaRecorder = null;

        fileNameText.setText("Recording_Stopped, File_Saved : " + recordFile);
    }

    private boolean checkpermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            //if we dont have permission we have to ask permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, PermissionCode);
            return false;
        }
    }

    // once we close recording we need to stop recording
    @Override
    public void onStop() {
        super.onStop();

        if (isRecording) {
            stopRecording();
        }
    }
}
