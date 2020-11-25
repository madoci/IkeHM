package fr.frars.ikehm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private RunSession runSession;
    private Avatar avatar;

    // Sensors
    Sensor stepCounter;
    private int lastStepCount = -1;

    //
    Vibrator vibrator;

    // UI elements
    private Button runButton;
    private TextView distanceTextView;
    private TextView speedTextView;
    private TextView levelTextView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runSession = new RunSession();
        avatar = new Avatar();

        initSensors();
        initViews();
        updateViews();

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        runButton.setOnClickListener(v -> {
            if (runSession.isStarted()) {
                runSession.stop();
                runButton.setText(R.string.start_run);
            } else {
                runSession.start();
                runButton.setText(R.string.stop_run);
                updateViews();
            }
        });
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
            if (avatar.addExperience(deltaStepCount)) {
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
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
        levelTextView.setText(getString(R.string.level, avatar.getLevel()));
    }
}