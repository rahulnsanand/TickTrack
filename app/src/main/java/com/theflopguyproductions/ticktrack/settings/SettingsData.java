package com.theflopguyproductions.ticktrack.settings;

public class SettingsData {

    public enum Frequency {
        MONTHLY(1), WEEKLY(2), DAILY(3);
        private int frequencyCode;
        Frequency(int value) {
            frequencyCode = value;
        }
        public int getCode() {
            return frequencyCode;
        }
    }
    public enum Theme {
        DARK(1), LIGHT(2);
        private int themeMode;
        Theme(int value) {
            themeMode = value;
        }
        public int getCode() {
            return themeMode;
        }
    }

    int themeMode, syncDataFrequency;
    boolean isHapticFeedback, isCounterBackupOn, isTimerBackupOn;
    long lastBackupTime;

    public long getLastBackupTime() {
        return lastBackupTime;
    }

    public void setLastBackupTime(long lastBackupTime) {
        this.lastBackupTime = lastBackupTime;
    }

    public int getThemeMode() {
        return themeMode;
    }

    public void setThemeMode(int themeMode) {
        this.themeMode = themeMode;
    }

    public int getSyncDataFrequency() {
        return syncDataFrequency;
    }

    public void setSyncDataFrequency(int syncDataFrequency) {
        this.syncDataFrequency = syncDataFrequency;
    }

    public boolean isHapticFeedback() {
        return isHapticFeedback;
    }

    public void setHapticFeedback(boolean hapticFeedback) {
        isHapticFeedback = hapticFeedback;
    }

    public boolean isCounterBackupOn() {
        return isCounterBackupOn;
    }

    public void setCounterBackupOn(boolean counterBackupOn) {
        isCounterBackupOn = counterBackupOn;
    }

    public boolean isTimerBackupOn() {
        return isTimerBackupOn;
    }

    public void setTimerBackupOn(boolean timerBackupOn) {
        isTimerBackupOn = timerBackupOn;
    }
}

