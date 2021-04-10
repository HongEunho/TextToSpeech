package kr.ac.sejong.texttospeech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText ttsEdit;
    private Button speakButton;
    private TextToSpeech ttsEngine;
    private TextView stoText;
    private TextView srMsg;
    private SpeechRecognizer mRecognizer;
    private Button srButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ttsEdit = findViewById(R.id.ttsEdit);
        speakButton = findViewById(R.id.speakButton);
        stoText = findViewById(R.id.StoText);
        srMsg = findViewById(R.id.srSysMsg);
        srButton = findViewById(R.id.srButton);

        // 1. TextToSpeech class를 인스턴스 화 한다.
        ttsEngine = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR)
                {
                    ttsEngine.setLanguage(Locale.KOREAN);
                }
            }
        });

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ttsEngine.setPitch(1.0f);
                ttsEngine.setSpeechRate(1.0f);
                Editable editable = ttsEdit.getText();
                ttsEngine.speak(editable, TextToSpeech.QUEUE_ADD, null, "1");
            }
        });

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {                srMsg.setText("[onReadyForSpeech]");            }
            @Override
            public void onBeginningOfSpeech() {                srMsg.setText("[onBeginningOfSpeech]");            }
            @Override
            public void onRmsChanged(float rmsdB) {                srMsg.setText("[onRmsChanged]");            }
            @Override
            public void onBufferReceived(byte[] buffer) {                srMsg.setText("[onBufferReceived]");            }
            @Override
            public void onEndOfSpeech() {                srMsg.setText("[onEndOfSpeech]");            }
            @Override
            public void onError(int error) {                srMsg.setText("[onError]");            }
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> mResult = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String str = "";
                for(String elem : mResult)
                    str+=elem+" , ";
                stoText.setText(str);
            }

            @Override
            public void onPartialResults(Bundle partialResults) {                srMsg.setText("[onPartialResults]");            }
            @Override
            public void onEvent(int eventType, Bundle params) {                srMsg.setText("[onEvent]");            }
        });

        srButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //1. 권한 체크 INTERNET, RECORD_AUDIO
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.INTERNET)!=PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO},0);
                }

                Intent srIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                srIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
                srIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

                mRecognizer.startListening(srIntent);
            }
        });
    }
}
