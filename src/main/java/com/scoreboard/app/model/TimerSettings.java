package com.scoreboard.app.model;

public class TimerSettings {
    private final boolean enabled;
    private final int seconds;

    public TimerSettings(boolean enabled, int seconds) {
        if (enabled && seconds <= 0) {
            throw new IllegalArgumentException("Seconds must be greater than 0 when timer is enabled.");
        }
        if (!enabled && seconds != 0) {
            throw new IllegalArgumentException("Seconds must be 0 when timer is disabled.");
        }
        this.enabled = enabled;
        this.seconds = seconds;
    }

    public static TimerSettings off() {
        return new TimerSettings(false, 0);
    }

    public static TimerSettings ofSeconds(int seconds) {
        return new TimerSettings(true, seconds);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getSeconds() {
        return seconds;
    }
}