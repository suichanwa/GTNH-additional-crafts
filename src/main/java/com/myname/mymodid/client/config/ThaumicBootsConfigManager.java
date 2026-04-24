package com.myname.mymodid.client.config;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.Loader;

public final class ThaumicBootsConfigManager {

    private static final String[] CANDIDATE_FILE_NAMES = new String[] { "thaumicboots.cfg", "ThaumicBoots.cfg",
        "thaumic_boots.cfg" };

    private static Configuration configuration;

    private ThaumicBootsConfigManager() {}

    public static synchronized Configuration getConfiguration() {
        if (configuration == null) {
            configuration = loadConfiguration();
        }
        return configuration;
    }

    public static synchronized void saveIfChanged() {
        if (configuration != null && configuration.hasChanged()) {
            configuration.save();
        }
    }

    private static Configuration loadConfiguration() {
        File configFile = resolveThaumicBootsConfigFile();
        Configuration loadedConfiguration = new Configuration(configFile);
        loadedConfiguration.load();
        return loadedConfiguration;
    }

    private static File resolveThaumicBootsConfigFile() {
        File configDir = Loader.instance()
            .getConfigDir();
        for (String fileName : CANDIDATE_FILE_NAMES) {
            File candidate = new File(configDir, fileName);
            if (candidate.exists()) {
                return candidate;
            }
        }

        File[] matchingFiles = configDir.listFiles((directory, fileName) -> {
            String lowerCaseName = fileName.toLowerCase(Locale.ROOT);
            return lowerCaseName.endsWith(".cfg") && lowerCaseName.contains("thaumic")
                && lowerCaseName.contains("boot");
        });
        if (matchingFiles != null && matchingFiles.length > 0) {
            Arrays.sort(matchingFiles, Comparator.comparing(File::getName));
            return matchingFiles[0];
        }
        return new File(configDir, CANDIDATE_FILE_NAMES[0]);
    }
}
