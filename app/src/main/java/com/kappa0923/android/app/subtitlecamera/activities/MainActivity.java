
package com.kappa0923.android.app.subtitlecamera.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kappa0923.android.app.subtitlecamera.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecognitionListener {
    private static final String TAG = "SubtitleLog";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private SpeechRecognizer mRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permissionがないよ");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                Log.d(TAG, "一回キャンセルされてるよ");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            }
        } else {
            Log.d(TAG, "Permissionがあるよ");
        }

        Button button = (Button)findViewById(R.id.start_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListening();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopListening();
    }

    protected void startListening() {
        try {
            if (mRecognizer == null) {
                mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
                if (!SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
                    Log.d(TAG, "Cannot recognize");
                    finish();
                }
                mRecognizer.setRecognitionListener(this);
            }

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
            mRecognizer.startListening(intent);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecognizer.stopListening();
                }
            }, 5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void stopListening() {
        if (mRecognizer != null) {
            mRecognizer.destroy();
            mRecognizer = null;
        }
    }

    protected void restartListening() {
        stopListening();
        startListening();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission granted");
                    startListening();
                } else {
                    Log.d(TAG, "Permission denied");
                }
                break;
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Toast.makeText(getApplicationContext(), "準備完了", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "認識終了");
    }

    @Override
    public void onError(int error) {
        Log.d(TAG, "認識失敗");
        restartListening();
    }

    @Override
    public void onResults(Bundle results) {
        // 結果をArrayListとして取得
        List<String> results_array = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (results_array != null) {
            Log.d(TAG, results_array.get(0));
            Toast.makeText(getApplicationContext(), results_array.get(0), Toast.LENGTH_LONG).show();
            // 取得した文字列を結合
//            StringBuilder builder = new StringBuilder();
//            for (String result : results_array) {
//                builder.append(result);
//                Log.d(TAG, "認識 : " + result);
//            }
//            Toast.makeText(getApplicationContext(), new String(builder), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "何も聞こえまてん", Toast.LENGTH_SHORT).show();
        }

        restartListening();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }
}
