package com.christolis;

import com.christolis.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;

public class ModClient implements ClientModInitializer {
	public static final ConfigManager CONFIG_MANAGER = new ConfigManager();

	@Override
	public void onInitializeClient() {
	}
}