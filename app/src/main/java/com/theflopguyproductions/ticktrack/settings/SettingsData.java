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

    int themeMode, syncDataFrequency;
    boolean isHapticFeedback, isCounterBackupOn, isTimerBackupOn;

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

