package com.example.tugas7;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tugas7.adapter.SensorAdapter;
import com.example.tugas7.model.SensorData;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;

    private final List<SensorData> sensorDataList = new ArrayList<>();
    private SensorAdapter adapter;

    // Dark Mode Debounce
    private Handler darkModeHandler;
    private Runnable applyDarkModeRunnable;
    private Runnable applyLightModeRunnable;
    private int pendingNightMode = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }

        initSensorList();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewSensors);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SensorAdapter(sensorDataList);
        recyclerView.setAdapter(adapter);

        MaterialButton btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(v -> resetDashboard());

        initDarkModeRunnables();
    }

    private void initSensorList() {
        sensorDataList.clear();

        // Light Sensor only
        sensorDataList.add(new SensorData(
                Sensor.TYPE_LIGHT,
                getString(R.string.title_light),
                lightSensor != null,
                lightSensor != null ? lightSensor.getMaximumRange() : 500f
        ));
    }

    private void initDarkModeRunnables() {
        darkModeHandler = new Handler(Looper.getMainLooper());

        applyDarkModeRunnable = () -> {
            pendingNightMode = -1;
            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        };

        applyLightModeRunnable = () -> {
            pendingNightMode = -1;
            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        if (darkModeHandler != null) {
            darkModeHandler.removeCallbacksAndMessages(null);
            pendingNightMode = -1;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event == null || event.sensor == null) return;

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lux = event.values[0];
            if (!sensorDataList.isEmpty()) {
                SensorData data = sensorDataList.get(0);
                data.updateLight(lux);
                adapter.notifyItemChanged(0);
            }
            handleAutoDarkMode(lux);
        }
    }

    private void handleAutoDarkMode(float lux) {
        int currentUiMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (lux < 50.0f) {
            if (currentUiMode != Configuration.UI_MODE_NIGHT_YES && pendingNightMode != AppCompatDelegate.MODE_NIGHT_YES) {
                darkModeHandler.removeCallbacksAndMessages(null);
                pendingNightMode = AppCompatDelegate.MODE_NIGHT_YES;
                darkModeHandler.postDelayed(applyDarkModeRunnable, 2000);
            }
        } else if (lux > 100.0f) {
            if (currentUiMode != Configuration.UI_MODE_NIGHT_NO && pendingNightMode != AppCompatDelegate.MODE_NIGHT_NO) {
                darkModeHandler.removeCallbacksAndMessages(null);
                pendingNightMode = AppCompatDelegate.MODE_NIGHT_NO;
                darkModeHandler.postDelayed(applyLightModeRunnable, 2000);
            }
        } else {
            // Hysteresis range [50, 100 lux] - cancel pending mode change if active
            if (pendingNightMode != -1) {
                darkModeHandler.removeCallbacksAndMessages(null);
                pendingNightMode = -1;
            }
        }
    }

    private void resetDashboard() {
        for (SensorData data : sensorDataList) {
            data.resetMinMax();
        }
        adapter.notifyDataSetChanged();
        Toast.makeText(this, R.string.reset_toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not required for dashboard updates
    }
}