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
        DARK(2), LIGHT(1);
        private int themeMode;
        Theme(int value) {
            themeMode = value;
        }
        public int getCode() {
            return themeMode;
        }
    }

    int themeMode, syncDataFrequency, screensaverClockStyle;
    boolean isHapticFeedback;
    boolean isCounterBackupOn;
    boolean isTimerBackupOn;
    boolean isSumDisplayed;
    boolean isMilestoneVibrate;

    public boolean isSwitchedToFirebase() {
        return isSwitchedToFirebase;
    }

    public void setSwitchedToFirebase(boolean switchedToFirebase) {
        isSwitchedToFirebase = switchedToFirebase;
    }

    boolean isSwitchedToFirebase;
    long lastBackupTime, settingsChangeTime;

    public long getSettingsChangeTime() {
        return settingsChangeTime;
    }

    public void setSettingsChangeTime(long settingsChangeTime) {
        this.settingsChangeTime = settingsChangeTime;
    }

    public int getScreensaverClockStyle() {
        return screensaverClockStyle;
    }

    public void setScreensaverClockStyle(int screensaverClockStyle) {
        this.screensaverClockStyle = screensaverClockStyle;
    }

    public boolean isMilestoneVibrate() {
        return isMilestoneVibrate;
    }

    public void setMilestoneVibrate(boolean milestoneVibrate) {
        isMilestoneVibrate = milestoneVibrate;
    }

    public boolean isSumDisplayed() {
        return isSumDisplayed;
    }

    public void setSumDisplayed(boolean sumDisplayed) {
        isSumDisplayed = sumDisplayed;
    }

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

