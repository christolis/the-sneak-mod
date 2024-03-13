package com.christolis.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class ConfigManager {
    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);
    private final Gson gson;
    private final File file;

    private static final String MENU_TITLE = "Sneak/Sprint Configuration";

    private Config config = new Config();

    public ConfigManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        File configDir = FabricLoader.getInstance().getConfigDir().toFile();
        file = new File(configDir, "sneaksprint/config.json");
    }

    public void save() {
        try {
            FileUtils.writeStringToFile(this.file, this.gson.toJson(this.config),
                    Charset.defaultCharset());
        } catch (IOException e) {
            log.error("Failed to save config", e);
        }
    }

    public void load() {
        try {
            if (!this.file.getParentFile().mkdirs()) {
                throw new IOException("Failed to create directory " + this.file.getParent());
            }

            if (!this.file.exists()) {
                log.info("Failed to find configuration file, creating one.");
                this.save();
            } else {
                byte[] bytes = Files.readAllBytes(Paths.get(this.file.getPath()));
                this.config = this.gson.fromJson(new String(bytes, Charset.defaultCharset()),
                        Config.class);
            }
        } catch (IOException e) {
            log.error("Failed to load configuration file", e);
        }
    }

    public Config getConfig() {
        return this.config;
    }

    @Environment(EnvType.CLIENT)
    public Screen getScreen(Screen parent) {
        ConfigBuilder configBuilder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTransparentBackground(true)
            .setSavingRunnable(this::save)
            .setTitle(Text.of(MENU_TITLE));

        ConfigCategory category = configBuilder.getOrCreateCategory(Text.of("Category"));

        // Entries
        category.addEntry(generateEnableModEntry(configBuilder));
        category.addEntry(generateEnableSneakEntry(configBuilder));
        category.addEntry(generateEnableSprintEntry(configBuilder));

        return configBuilder.build();
    }

    private BooleanListEntry generateEnableModEntry(ConfigBuilder configBuilder) {
        ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
        return entryBuilder.startBooleanToggle(Text.of("Enable mod"), this.config.isEnabled())
            .setDefaultValue(true)
            .setSaveConsumer(this.config::setEnabled)
            .build();
    }

    private BooleanListEntry generateEnableSneakEntry(ConfigBuilder configBuilder) {
        ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
        return entryBuilder
            .startBooleanToggle(Text.of("Enable sneak"), this.config.isSneakEnabled())
            .setTooltip(Text.of("""
                    Toggle whether the sneak indicator
                    should be visible or not.
                    """))
            .setDefaultValue(true)
            .setSaveConsumer(this.config::setSneakEnabled)
            .build();
    }

    private BooleanListEntry generateEnableSprintEntry(ConfigBuilder configBuilder) {
        ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
        return entryBuilder
            .startBooleanToggle(Text.of("Enable sprint"), this.config.isSprintEnabled())
            .setTooltip(Text.of("""
                    Toggle whether the sprint indicator
                    should be visible or not.
                    """))
            .setDefaultValue(true)
            .setSaveConsumer(this.config::setSprintEnabled)
            .build();
    }
}
