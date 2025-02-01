package net.storm.plugins.commons.utils;

import java.text.DecimalFormat;

public class TrackingUtils {
    private long startTime;
    private long totalElapsedTime = 0;
    private boolean isTimeTracking = false;

    public TrackingUtils() {}

    public void start() {
        if (!isTimeTracking) {
            this.startTime = System.currentTimeMillis();
            this.isTimeTracking = true;
        }
    }

    public void pause() {
        if (isTimeTracking) {
            this.totalElapsedTime += System.currentTimeMillis() - startTime;
            this.isTimeTracking = false;
        }
    }

    public long getElapsedTimeSeconds() {
        if (isTimeTracking) {
            return (totalElapsedTime + (System.currentTimeMillis() - startTime)) / 1000;
        } else {
            return totalElapsedTime / 1000;
        }
    }

    public String getFormatedTime() {
        long totalTime = this.getElapsedTimeSeconds();

        long hours = totalTime / 3600;
        long minutes = (totalTime % 3600) / 60;
        long seconds = (totalTime % 60);

        // Format as HH:MM:SS.mmm with leading zeros
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public int getDivider(long amount) {
        int divider = 1;

        if (amount > 10000) {
            divider = 1000;
        }

        if (amount > 1000000) {
            divider = 1000000;
        }

        return divider;
    }

    public String getUnit(long amount) {
        String unit = "";


        if (amount > 10000) {
            unit = "k";
        }

        if (amount > 1000000) {
            unit = "m";
        }

        return unit;
    }

    public String calculateRatePerHour(long amount) {
        double elapsedTimeHours = (double) getElapsedTimeSeconds() / 3600;

        if (elapsedTimeHours == 0 || amount == 0) {
            return "0";
        }

        double rate = (amount / elapsedTimeHours) / getDivider(amount);

        DecimalFormat df = new DecimalFormat("#.00" + getUnit(amount));

        return df.format(rate) + "/hr";
    }

    public String totalAmount(long amount) {
        double rate = (double) amount / getDivider(amount);

        DecimalFormat df = new DecimalFormat("#" + getUnit(amount));

        return df.format(rate);
    }

    public String getTotalAmountAndRate(long amount) {
        return totalAmount(amount) + " | " + calculateRatePerHour(amount);
    }
}
