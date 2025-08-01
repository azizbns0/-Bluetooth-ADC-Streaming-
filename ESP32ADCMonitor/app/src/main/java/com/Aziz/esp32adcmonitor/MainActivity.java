package com.Aziz.esp32adcmonitor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // --- CONSTANTS ---
    private static final String TAG = "ESP32_ADC_Monitor";
    private static final String ESP32_DEVICE_NAME = "ESP32_ADC_Streamer";
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int PERMISSIONS_REQUEST_CODE = 101;
    private static final int REQUEST_ENABLE_BT = 102;
    private static final int MAX_VISIBLE_ENTRIES = 500;
    private static final int CHART_UPDATE_INTERVAL = 50; // ms for smooth animation

    // --- UI Elements ---
    private TextView connectionStatus, packetCount, lastValue, rangeValue, logText;
    private TextView avgValue, minValue, maxValue, dataRate;
    private Button connectButton, clearButton;
    private LineChart chart;
    private CardView statusCard, statsCard1, statsCard2, statsCard3, statsCard4;
    private View connectionIndicator;

    // --- Bluetooth ---
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BufferedReader bluetoothReader;
    private Thread bluetoothConnectionThread;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    // --- Chart Data & Statistics ---
    private final ArrayList<Entry> entries = new ArrayList<>();
    private LineDataSet dataSet;
    private int currentXValue = 0;
    private int totalPacketsReceived = 0;
    private float currentMin = Float.MAX_VALUE;
    private float currentMax = Float.MIN_VALUE;
    private float runningSum = 0;
    private int totalSamples = 0;
    private long lastPacketTime = 0;
    private float currentDataRate = 0;

    // --- Animation & Timing ---
    private Handler chartUpdateHandler = new Handler(Looper.getMainLooper());
    private Runnable chartUpdateRunnable;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initChart();
        initBluetooth();
        setupAnimations();
        checkAndRequestPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectBluetooth();
        if (chartUpdateHandler != null && chartUpdateRunnable != null) {
            chartUpdateHandler.removeCallbacks(chartUpdateRunnable);
        }
    }

    // --------------------------------------------------------------------------------------------
    // UI INITIALIZATION & ANIMATIONS
    // --------------------------------------------------------------------------------------------
    private void initUI() {
        // Status elements
        connectionStatus = findViewById(R.id.connectionStatus);
        connectionIndicator = findViewById(R.id.connectionIndicator);
        statusCard = findViewById(R.id.statusCard);

        // Statistics
        packetCount = findViewById(R.id.packetCount);
        lastValue = findViewById(R.id.lastValue);
        rangeValue = findViewById(R.id.rangeValue);
        avgValue = findViewById(R.id.avgValue);
        minValue = findViewById(R.id.minValue);
        maxValue = findViewById(R.id.maxValue);
        dataRate = findViewById(R.id.dataRate);

        // Cards
        statsCard1 = findViewById(R.id.statsCard1);
        statsCard2 = findViewById(R.id.statsCard2);
        statsCard3 = findViewById(R.id.statsCard3);
        statsCard4 = findViewById(R.id.statsCard4);

        // Controls
        connectButton = findViewById(R.id.connectButton);
        clearButton = findViewById(R.id.clearButton);
        chart = findViewById(R.id.chart);
        logText = findViewById(R.id.logText);
        logText.setMovementMethod(new ScrollingMovementMethod());

        // Button listeners
        connectButton.setOnClickListener(v -> {
            if (isConnected) {
                disconnectBluetooth();
            } else {
                if (checkAndRequestPermissions()) {
                    connectToBluetoothDevice();
                } else {
                    toast("Bluetooth permissions required");
                }
            }
        });

        clearButton.setOnClickListener(v -> clearPlot());

        // Initial state
        updateConnectionState(false);
        resetStatistics();
    }

    private void setupAnimations() {
        // Pulse animation for connection indicator
        Animation pulseAnimation = new AlphaAnimation(0.3f, 1.0f);
        pulseAnimation.setDuration(1000);
        pulseAnimation.setRepeatCount(Animation.INFINITE);
        pulseAnimation.setRepeatMode(Animation.REVERSE);

        // Setup chart update timer for smooth animation
        chartUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isConnected && !entries.isEmpty()) {
                    updateChartSmooth();
                }
                chartUpdateHandler.postDelayed(this, CHART_UPDATE_INTERVAL);
            }
        };
        chartUpdateHandler.post(chartUpdateRunnable);
    }

    // --------------------------------------------------------------------------------------------
    // CHART INITIALIZATION & UPDATES
    // --------------------------------------------------------------------------------------------
    private void initChart() {
        dataSet = new LineDataSet(entries, "ADC Voltage (V)");

        // Modern chart styling
        dataSet.setColor(ContextCompat.getColor(this, R.color.chart_line));
        dataSet.setLineWidth(2.5f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Smooth curves
        dataSet.setCubicIntensity(0.2f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(ContextCompat.getColor(this, R.color.chart_fill));
        dataSet.setFillAlpha(50);

        // Chart appearance
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setBackgroundColor(ContextCompat.getColor(this, R.color.chart_background));
        chart.getLegend().setEnabled(false);
        chart.setGridBackgroundColor(Color.TRANSPARENT);
        chart.setDrawGridBackground(false);
        chart.setBorderWidth(0);
        chart.setViewPortOffsets(60, 20, 60, 80);

        // X-axis styling
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(ContextCompat.getColor(this, R.color.grid_color));
        xAxis.setGridLineWidth(0.5f);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        xAxis.setTextSize(10f);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.US, "%.0fs", value / 10f);
            }
        });

        // Y-axis styling
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(ContextCompat.getColor(this, R.color.grid_color));
        leftAxis.setGridLineWidth(0.5f);
        leftAxis.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        leftAxis.setTextSize(10f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(3.6f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.US, "%.1fV", value);
            }
        });

        chart.getAxisRight().setEnabled(false);

        // Set initial data
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    private void updateChartSmooth() {
        if (chart.getData() != null && !entries.isEmpty()) {
            dataSet.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

            // Smooth scrolling
            if (entries.size() > MAX_VISIBLE_ENTRIES / 2) {
                chart.setVisibleXRangeMaximum(MAX_VISIBLE_ENTRIES);
                float targetX = entries.get(entries.size() - 1).getX();
                chart.moveViewToX(targetX);
            }

            chart.invalidate();
        }
    }

    // --------------------------------------------------------------------------------------------
    // BLUETOOTH HANDLING
    // --------------------------------------------------------------------------------------------
    private void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            toast("Bluetooth not supported");
            logWithTimestamp("ERROR: Bluetooth not supported on this device", "ERROR");
            connectButton.setEnabled(false);
        }
    }

    @SuppressLint("MissingPermission")
    private void connectToBluetoothDevice() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            if (bluetoothAdapter != null) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            return;
        }

        BluetoothDevice esp32Device = findPairedDevice();
        if (esp32Device == null) {
            toast("ESP32 device not found in paired devices");
            logWithTimestamp("Device '" + ESP32_DEVICE_NAME + "' not found in paired devices", "WARNING");
            return;
        }

        updateConnectionState(false, "Connecting...");
        animateConnectionAttempt();

        bluetoothConnectionThread = new Thread(() -> {
            try {
                bluetoothSocket = esp32Device.createRfcommSocketToServiceRecord(SPP_UUID);
                bluetoothSocket.connect();
                bluetoothReader = new BufferedReader(new InputStreamReader(bluetoothSocket.getInputStream()));

                uiHandler.post(() -> {
                    updateConnectionState(true);
                    logWithTimestamp("Successfully connected to " + ESP32_DEVICE_NAME, "SUCCESS");
                    toast("Connected successfully!");
                });

                listenForBluetoothData();

            } catch (IOException | SecurityException e) {
                uiHandler.post(() -> {
                    updateConnectionState(false);
                    logWithTimestamp("Connection failed: " + e.getMessage(), "ERROR");
                    toast("Connection failed");
                });
            }
        });
        bluetoothConnectionThread.start();
    }

    @SuppressLint("MissingPermission")
    private BluetoothDevice findPairedDevice() {
        try {
            Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
            if (bondedDevices != null) {
                for (BluetoothDevice device : bondedDevices) {
                    if (ESP32_DEVICE_NAME.equals(device.getName())) {
                        return device;
                    }
                }
            }
        } catch (SecurityException e) {
            logWithTimestamp("Permission error accessing paired devices", "ERROR");
        }
        return null;
    }

    private void listenForBluetoothData() {
        try {
            while (!Thread.currentThread().isInterrupted() &&
                    bluetoothSocket != null && bluetoothSocket.isConnected()) {

                String line = bluetoothReader.readLine();
                if (line == null) {
                    uiHandler.post(() -> {
                        logWithTimestamp("Device disconnected", "WARNING");
                        disconnectBluetooth();
                    });
                    break;
                }

                processPacket(line);
            }
        } catch (IOException e) {
            if (!Thread.currentThread().isInterrupted()) {
                uiHandler.post(() -> {
                    logWithTimestamp("Connection lost: " + e.getMessage(), "ERROR");
                    disconnectBluetooth();
                });
            }
        }
    }

    private void disconnectBluetooth() {
        if (bluetoothConnectionThread != null) {
            bluetoothConnectionThread.interrupt();
            try {
                bluetoothConnectionThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            bluetoothConnectionThread = null;
        }

        try {
            if (bluetoothReader != null) {
                bluetoothReader.close();
                bluetoothReader = null;
            }
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
                bluetoothSocket = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing Bluetooth resources", e);
        }

        uiHandler.post(() -> {
            updateConnectionState(false);
            logWithTimestamp("Disconnected", "INFO");
        });
    }

    // --------------------------------------------------------------------------------------------
    // DATA PROCESSING & STATISTICS
    // --------------------------------------------------------------------------------------------
    private void processPacket(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray samplesArray = obj.getJSONArray("samples");

            if (samplesArray.length() == 0) return;

            totalPacketsReceived++;
            long currentTime = System.currentTimeMillis();

            // Calculate data rate
            if (lastPacketTime > 0) {
                float timeDiff = (currentTime - lastPacketTime) / 1000.0f;
                currentDataRate = samplesArray.length() / timeDiff;
            }
            lastPacketTime = currentTime;

            float packetMin = Float.MAX_VALUE;
            float packetMax = Float.MIN_VALUE;
            float packetSum = 0;

            // Process samples
            for (int i = 0; i < samplesArray.length(); i++) {
                float voltage = (float) samplesArray.getDouble(i);

                // Add to chart data
                entries.add(new Entry(currentXValue++, voltage));

                // Update statistics
                packetSum += voltage;
                totalSamples++;
                runningSum += voltage;

                if (voltage < packetMin) packetMin = voltage;
                if (voltage > packetMax) packetMax = voltage;
                if (voltage < currentMin) currentMin = voltage;
                if (voltage > currentMax) currentMax = voltage;
            }

            // Limit entries for performance
            while (entries.size() > MAX_VISIBLE_ENTRIES) {
                entries.remove(0);
            }

            final float lastSample = (float) samplesArray.getDouble(samplesArray.length() - 1);
            final float packetRange = packetMax - packetMin;
            final float globalAvg = runningSum / totalSamples;

            // Update UI
            uiHandler.post(() -> updateStatistics(lastSample, packetRange, globalAvg));

            logWithTimestamp(String.format(Locale.US,
                    "Packet #%d: %d samples, Last: %.3fV, Range: %.3fV",
                    totalPacketsReceived, samplesArray.length(), lastSample, packetRange), "DATA");

        } catch (JSONException e) {
            logWithTimestamp("Invalid JSON packet: " + e.getMessage(), "ERROR");
        }
    }

    private void updateStatistics(float lastSample, float packetRange, float globalAvg) {
        // Animate value changes
        animateTextChange(lastValue, String.format(Locale.US, "%.3f", lastSample));
        animateTextChange(rangeValue, String.format(Locale.US, "%.3f", packetRange));
        animateTextChange(avgValue, String.format(Locale.US, "%.3f", globalAvg));
        animateTextChange(minValue, String.format(Locale.US, "%.3f", currentMin));
        animateTextChange(maxValue, String.format(Locale.US, "%.3f", currentMax));
        animateTextChange(dataRate, String.format(Locale.US, "%.1f", currentDataRate));

        packetCount.setText(String.valueOf(totalPacketsReceived));
    }

    // --------------------------------------------------------------------------------------------
    // UI UPDATES & ANIMATIONS
    // --------------------------------------------------------------------------------------------
    private void updateConnectionState(boolean connected) {
        updateConnectionState(connected, connected ? "Connected" : "Disconnected");
    }

    private void updateConnectionState(boolean connected, String statusText) {
        isConnected = connected;

        connectionStatus.setText(statusText);
        connectionStatus.setTextColor(ContextCompat.getColor(this,
                connected ? R.color.success_color : R.color.error_color));

        connectionIndicator.setBackgroundResource(
                connected ? R.drawable.indicator_connected : R.drawable.indicator_disconnected);

        connectButton.setText(connected ? "Disconnect" : "Connect");
        connectButton.setEnabled(true);

        // Animate status card
        statusCard.setCardBackgroundColor(ContextCompat.getColor(this,
                connected ? R.color.success_background : R.color.error_background));
    }

    private void animateConnectionAttempt() {
        connectButton.setText("Connecting...");
        connectButton.setEnabled(false);

        // Pulse animation for connection indicator
        Animation pulse = new AlphaAnimation(0.3f, 1.0f);
        pulse.setDuration(800);
        pulse.setRepeatCount(Animation.INFINITE);
        pulse.setRepeatMode(Animation.REVERSE);
        connectionIndicator.startAnimation(pulse);
    }

    private void animateTextChange(TextView textView, String newText) {
        if (!textView.getText().toString().equals(newText)) {
            AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.3f);
            fadeOut.setDuration(150);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    textView.setText(newText);
                    AlphaAnimation fadeIn = new AlphaAnimation(0.3f, 1.0f);
                    fadeIn.setDuration(150);
                    textView.startAnimation(fadeIn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            textView.startAnimation(fadeOut);
        }
    }

    private void clearPlot() {
        entries.clear();
        currentXValue = 0;
        resetStatistics();

        if (chart.getData() != null) {
            chart.getData().clearValues();
        }
        dataSet.notifyDataSetChanged();
        chart.notifyDataSetChanged();
        chart.invalidate();

        logWithTimestamp("Plot cleared", "INFO");
        toast("Plot cleared");
    }

    private void resetStatistics() {
        totalPacketsReceived = 0;
        currentMin = Float.MAX_VALUE;
        currentMax = Float.MIN_VALUE;
        runningSum = 0;
        totalSamples = 0;
        currentDataRate = 0;

        packetCount.setText("0");
        lastValue.setText("--");
        rangeValue.setText("--");
        avgValue.setText("--");
        minValue.setText("--");
        maxValue.setText("--");
        dataRate.setText("--");
    }

    // --------------------------------------------------------------------------------------------
    // LOGGING
    // --------------------------------------------------------------------------------------------
    private void logWithTimestamp(String message, String level) {
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(new Date());
        String logEntry = String.format("[%s] %s: %s", timestamp, level, message);

        uiHandler.post(() -> {
            logText.append(logEntry + "\n");

            // Auto-scroll to bottom
            if (logText.getLayout() != null) {
                int scrollAmount = logText.getLayout().getLineTop(logText.getLineCount()) - logText.getHeight();
                if (scrollAmount > 0) {
                    logText.scrollTo(0, scrollAmount);
                } else {
                    logText.scrollTo(0, 0);
                }
            }
        });

        // Also log to Logcat with appropriate level
        switch (level) {
            case "ERROR": Log.e(TAG, message); break;
            case "WARNING": Log.w(TAG, message); break;
            case "SUCCESS":
            case "INFO": Log.i(TAG, message); break;
            case "DATA": Log.d(TAG, message); break;
            default: Log.v(TAG, message); break;
        }
    }

    // --------------------------------------------------------------------------------------------
    // UTILITY METHODS
    // --------------------------------------------------------------------------------------------
    private void toast(String message) {
        uiHandler.post(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    private boolean checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_SCAN);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_ADMIN);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), PERMISSIONS_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean allGranted = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    logWithTimestamp("Permission granted: " + permissions[i], "INFO");
                } else {
                    allGranted = false;
                    logWithTimestamp("Permission denied: " + permissions[i], "WARNING");
                }
            }
            if (allGranted) {
                logWithTimestamp("All permissions granted", "SUCCESS");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                logWithTimestamp("Bluetooth enabled", "SUCCESS");
                if (checkAndRequestPermissions()) {
                    connectToBluetoothDevice();
                }
            } else {
                logWithTimestamp("Bluetooth enable cancelled", "WARNING");
            }
        }
    }
}