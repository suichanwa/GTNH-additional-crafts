package com.myname.mymodid;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

    public static String greeting = "Hello World";

    private static final String CATEGORY_LCE = "large_combustion_engine";

    public static int lceDefaultMaxEfficiency = 10000;
    public static int lceOxygenBoostedMaxEfficiency = 30000;
    public static int lceDinitrogenTetroxideBoostedMaxEfficiency = 40000;
    public static int lceOverclockNominalOutput = 5120;
    public static int lceOverclockEfficiencyNumerator = 4;
    public static int lceOverclockEfficiencyDenominator = 5;
    public static int lceDinitrogenTetroxideConsumptionPerTick = 1;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        greeting = configuration.getString("greeting", Configuration.CATEGORY_GENERAL, greeting, "How shall I greet?");
        lceDefaultMaxEfficiency = configuration.getInt(
            "defaultMaxEfficiency",
            CATEGORY_LCE,
            lceDefaultMaxEfficiency,
            1,
            Integer.MAX_VALUE,
            "LCE max efficiency without boost. 10000 = 100%.");
        lceOxygenBoostedMaxEfficiency = configuration.getInt(
            "oxygenBoostedMaxEfficiency",
            CATEGORY_LCE,
            lceOxygenBoostedMaxEfficiency,
            1,
            Integer.MAX_VALUE,
            "LCE max efficiency with Oxygen boost. 30000 = 300%.");
        lceDinitrogenTetroxideBoostedMaxEfficiency = configuration.getInt(
            "dinitrogenTetroxideBoostedMaxEfficiency",
            CATEGORY_LCE,
            lceDinitrogenTetroxideBoostedMaxEfficiency,
            1,
            Integer.MAX_VALUE,
            "LCE max efficiency with Dinitrogen Tetroxide boost. 40000 = 400%.");
        lceOverclockNominalOutput = configuration.getInt(
            "overclockNominalOutput",
            CATEGORY_LCE,
            lceOverclockNominalOutput,
            1,
            Integer.MAX_VALUE,
            "LCE nominal EU/t in overclock mode.");
        lceOverclockEfficiencyNumerator = configuration.getInt(
            "overclockEfficiencyNumerator",
            CATEGORY_LCE,
            lceOverclockEfficiencyNumerator,
            0,
            Integer.MAX_VALUE,
            "Overclock fuel-efficiency penalty numerator.");
        lceOverclockEfficiencyDenominator = configuration.getInt(
            "overclockEfficiencyDenominator",
            CATEGORY_LCE,
            lceOverclockEfficiencyDenominator,
            1,
            Integer.MAX_VALUE,
            "Overclock fuel-efficiency penalty denominator.");
        lceDinitrogenTetroxideConsumptionPerTick = configuration.getInt(
            "dinitrogenTetroxideConsumptionPerTick",
            CATEGORY_LCE,
            lceDinitrogenTetroxideConsumptionPerTick,
            1,
            Integer.MAX_VALUE,
            "Dinitrogen Tetroxide consumption in mB/t. 1 mB/t = 20 L/s.");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
