package com.christolis;

import com.christolis.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;

public class SneakSprintClientMod implements ClientModInitializer {
    public static final ConfigManager CONFIG_MANAGER = new ConfigManager();

    @Override
    public void onInitializeClient() {}
}
