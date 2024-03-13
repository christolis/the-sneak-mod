package com.christolis.config;

public class Config {
    private boolean isEnabled = true;
    private boolean isSneakEnabled = true;
    private boolean isSprintEnabled = true;

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isSneakEnabled() {
        return isSneakEnabled;
    }

    public void setSneakEnabled(boolean sneakEnabled) {
        isSneakEnabled = sneakEnabled;
    }

    public boolean isSprintEnabled() {
        return isSprintEnabled;
    }

    public void setSprintEnabled(boolean sprintEnabled) {
        isSprintEnabled = sprintEnabled;
    }
}
