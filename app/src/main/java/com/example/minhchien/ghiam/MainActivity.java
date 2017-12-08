package com.example.minhchien.ghiam;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button btnghiam,btndanhsach;
    MediaRecorder recorder;
    private static final String TAG = "SoundRecording";
    File audiofile = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnghiam=findViewById(R.id.btnghiam);
        btndanhsach=findViewById(R.id.btndanhsach);
        btnghiam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnghiam.setEnabled(false);
                btndanhsach.setEnabled(true);
                File sampleDir = Environment.getExternalStorageDirectory();
                try {
                    audiofile = File.createTempFile("sound", ".3gp", sampleDir);
                } catch (IOException e) {
                    Log.e(TAG, "error");
                    return;
                }
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(audiofile.getAbsolutePath());
                try {
                    recorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                recorder.start();
            }
        });
        btndanhsach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnghiam.setEnabled(true);
                btndanhsach.setEnabled(false);
                recorder.stop();
                recorder.release();
                addRecordingToMediaLibrary();
            }
        });

    }
    protected void addRecordingToMediaLibrary() {
        ContentValues values = new ContentValues(4);
        long current = System.currentTimeMillis();
        values.put(MediaStore.Audio.Media.TITLE, "audio" + audiofile.getName());
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gp");
        values.put(MediaStore.Audio.Media.DATA, audiofile.getAbsolutePath());
        ContentResolver contentResolver = getContentResolver();

        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(base, values);

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
        Toast.makeText(this, "Added File " + newUri, Toast.LENGTH_LONG).show();
    }
}
