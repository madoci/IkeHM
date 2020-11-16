package fr.frars.ikehm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEventListener;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private boolean running = false;
    private float startSteps = -1;
    private float currentSteps = 0;

    // Step sensor
    private SensorManager sensorManager;
    private Sensor stepsSensor;
    private TriggerEventListener triggerEventListener;
    private long startTime = 0;

    // UI elements
    private TextView stepTextView = null;
    private Button runButton = null;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stepTextView = (TextView) findViewById(R.id.textView8);
        runButton = (Button) findViewById(R.id.runButton);

        runButton.setOnClickListener(v -> {
            if(running){
                running = false;
                startTime = System.currentTimeMillis();
                runButton.setText(R.string.start_run);
            } else {
                running = true;
                startSteps = -1;
                currentSteps = 0;
                runButton.setText(R.string.stop_run);
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepsSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepsSensor == null) {
            Toast.makeText(this, "No Step Counter Sensor !", Toast.LENGTH_SHORT).show();
        } else {
            sensorManager.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(running){
            if(startSteps == -1){
                startSteps = event.values[0];
            }
            currentSteps = event.values[0];

            stepTextView.setText(String.valueOf(currentSteps - startSteps));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}