package com.example.tugas7.model;

import com.example.tugas7.R;

public class SensorData {

    private final int sensorType;
    private final String title;
    private final boolean isAvailable;
    private final float maxSensorRange;

    private float minValue;
    private float maxValue;

    private int progressPercent;
    private String statusBadgeText;
    private String statusDescriptionText;
    private int statusDotColorRes;

    private String formattedMainValue;
    private String formattedSubDetails;
    private String formattedMinMax;

    // Pre-allocated StringBuilders to avoid allocations in onSensorChanged
    private final StringBuilder sbMain = new StringBuilder();
    private final StringBuilder sbSub = new StringBuilder();
    private final StringBuilder sbMinMax = new StringBuilder();

    public SensorData(int sensorType, String title, boolean isAvailable, float maxSensorRange) {
        this.sensorType = sensorType;
        this.title = title;
        this.isAvailable = isAvailable;
        this.maxSensorRange = maxSensorRange > 0 ? maxSensorRange : 500f;
        this.statusDotColorRes = R.color.status_grey;

        resetMinMax();

        if (!isAvailable) {
            this.statusBadgeText = "TIDAK TERSEDIA";
            this.statusDescriptionText = "Sensor tidak ada di perangkat";
            this.formattedMainValue = "Sensor tidak tersedia";
            this.formattedSubDetails = "";
            this.formattedMinMax = "";
            this.progressPercent = 0;
        }
    }

    public void resetMinMax() {
        this.minValue = Float.MAX_VALUE;
        this.maxValue = Float.MIN_VALUE;

        if (!isAvailable) return;

        this.progressPercent = 0;
        this.formattedMinMax = "Min: - | Max: -";
        this.formattedMainValue = "0 lux";
        this.formattedSubDetails = "Tingkat Cahaya";
        this.statusBadgeText = "NORMAL";
        this.statusDescriptionText = "Membaca tingkat cahaya...";
        this.statusDotColorRes = R.color.status_green;
    }

    public void updateLight(float lux) {
        if (!isAvailable) return;

        if (minValue == Float.MAX_VALUE || lux < minValue) minValue = lux;
        if (lux > maxValue) maxValue = lux;

        float maxRange = maxSensorRange > 0 ? maxSensorRange : 500f;
        this.progressPercent = Math.min(100, Math.max(0, (int) ((lux / maxRange) * 100)));

        if (lux < 50.0f) {
            this.statusBadgeText = "GELAP";
            this.statusDescriptionText = "Cahaya redup / Gelap (< 50 lx)";
            this.statusDotColorRes = R.color.status_red;
        } else if (lux <= 100.0f) {
            this.statusBadgeText = "REDUP";
            this.statusDescriptionText = "Cahaya sedang (50 - 100 lx)";
            this.statusDotColorRes = R.color.status_orange;
        } else {
            this.statusBadgeText = "TERANG";
            this.statusDescriptionText = "Cahaya terang (> 100 lx)";
            this.statusDotColorRes = R.color.status_green;
        }

        sbMain.setLength(0);
        appendFloat(sbMain, lux, 1).append(" lux");
        this.formattedMainValue = sbMain.toString();

        sbSub.setLength(0);
        sbSub.append("Mode Tema: ");
        if (lux < 50.0f) sbSub.append("Otomatis Gelap (Night Mode)");
        else if (lux > 100.0f) sbSub.append("Otomatis Terang (Day Mode)");
        else sbSub.append("Histeresis (Transisi)");
        this.formattedSubDetails = sbSub.toString();

        updateMinMaxText();
    }

    private void updateMinMaxText() {
        sbMinMax.setLength(0);
        sbMinMax.append("Min: ");
        if (minValue == Float.MAX_VALUE) sbMinMax.append("-");
        else appendFloat(sbMinMax, minValue, 1).append(" lux");

        sbMinMax.append(" | Max: ");
        if (maxValue == Float.MIN_VALUE) sbMinMax.append("-");
        else appendFloat(sbMinMax, maxValue, 1).append(" lux");

        this.formattedMinMax = sbMinMax.toString();
    }

    private StringBuilder appendFloat(StringBuilder sb, float value, int decimals) {
        if (Float.isNaN(value)) {
            return sb.append("0");
        }
        boolean isNegative = value < 0;
        float absVal = Math.abs(value);
        int intPart = (int) absVal;

        if (isNegative && (intPart > 0 || decimals > 0)) {
            sb.append('-');
        }
        sb.append(intPart);

        if (decimals > 0) {
            sb.append('.');
            float frac = absVal - intPart;
            for (int i = 0; i < decimals; i++) {
                frac *= 10;
                int digit = (int) frac;
                sb.append(digit);
                frac -= digit;
            }
        }
        return sb;
    }

    // Getters
    public int getSensorType() { return sensorType; }
    public String getTitle() { return title; }
    public boolean isAvailable() { return isAvailable; }
    public int getProgressPercent() { return progressPercent; }
    public String getStatusBadgeText() { return statusBadgeText; }
    public String getStatusDescriptionText() { return statusDescriptionText; }
    public int getStatusDotColorRes() { return statusDotColorRes; }
    public String getFormattedMainValue() { return formattedMainValue; }
    public String getFormattedSubDetails() { return formattedSubDetails; }
    public String getFormattedMinMax() { return formattedMinMax; }
}