package fr.frars.ikehm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private RunSession runSession;
    private User user;

    // Sensors
    Sensor stepCounter;
    private int lastStepCount = -1;

    // Vibrator
    Vibrator vibrator;
    long[] vibratePattern = new long[]{0, 400, 200};

    // UI elements
    private Button runButton;
    private TextView distanceTextView;
    private TextView speedTextView;
    private TextView levelTextView;
    private ProgressBar progressBar;
    private TextView progressTextView;

    private TextToSpeech textToSpeech;

    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = new User();
        runSession = new RunSession();

        initSensors();
        initViews();
        updateViews();

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);



        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);

        runButton.setOnClickListener(v -> {
            if (runSession.isStarted()) {
                runSession.stop();
                user.globalStatistics.update(runSession.getTotalDistanceRan(), runSession.getTotalTimeRan());
                runButton.setText(R.string.start_run);
                speechRecognizer.startListening(speechRecognizerIntent);
            } else {
                runSession.start();
                runButton.setText(R.string.stop_run);
                updateViews();
                speechRecognizer.startListening(speechRecognizerIntent);
            }
        });

        speechRecognizer.setRecognitionListener(new RecognitionListener(){

            @Override
            public void onReadyForSpeech(Bundle params) {

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
                speechRecognizer.startListening(speechRecognizerIntent);
            }

            @Override
            public void onError(int error) {
                speechRecognizer.startListening(speechRecognizerIntent);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (runSession.isStarted() && data.get(0).contains("stop")) {
                    runSession.stop();
                    user.globalStatistics.update(runSession.getTotalDistanceRan(), runSession.getTotalTimeRan());
                    runButton.setText(R.string.start_run);
                    String toSpeak = "stopped";
                    Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                    textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                } else if(!runSession.isStarted() && data.get(0).contains("start")) {
                    runSession.start();
                    runButton.setText(R.string.stop_run);
                    updateViews();
                    String toSpeak = "started";
                    Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                    textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
                speechRecognizer.startListening(speechRecognizerIntent);
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                speechRecognizer.startListening(speechRecognizerIntent);
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });

        speechRecognizer.startListening(speechRecognizerIntent);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER && runSession.isStarted()) {
            int stepCount = (int) event.values[0];
            if (lastStepCount == -1) {
                lastStepCount = stepCount;
            }
            int deltaStepCount = stepCount - lastStepCount;
            lastStepCount = stepCount;

            runSession.update(deltaStepCount);
            if (user.avatar.addExperience(deltaStepCount)) {
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createWaveform(vibratePattern, -1));
                } else {
                    vibrator.vibrate(200);
                }
            }

            updateViews();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initSensors() {
        PackageManager packageManager = getPackageManager();
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)) {
            stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "No Step Counter Sensor !", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        runButton = findViewById(R.id.runButton);
        distanceTextView = findViewById(R.id.textView8);
        speedTextView = findViewById(R.id.textView9);
        levelTextView = findViewById(R.id.LevelText);
        progressBar = findViewById(R.id.progressBar);
        progressTextView = findViewById(R.id.progressText);
    }

    private void updateViews() {
        double distance = runSession.getTotalDistanceRan();
        String distanceUnit = "m";
        if (distance >= 1000) {
            distance /= 1000;
            distanceUnit = "km";
        }
        distanceTextView.setText(getString(R.string.run_distance, distance, distanceUnit));
        speedTextView.setText(getString(R.string.run_speed, runSession.getAverageSpeed()));
        levelTextView.setText(getString(R.string.level, user.avatar.getLevel()));
        progressBar.setMax(user.avatar.getThresholdExperience());
        progressBar.setProgress(user.avatar.getExperience());
        progressTextView.setText(getString(R.string.exp_progress, user.avatar.getExperience(), user.avatar.getThresholdExperience()));
    }
}