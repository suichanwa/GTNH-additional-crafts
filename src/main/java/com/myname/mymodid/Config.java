package com.myname.mymodid;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

    public static String greeting = "Hello World";

    private static final String CATEGORY_LCE = "large_combustion_engine";
    private static final String CATEGORY_LSB = "large_semifluid_burner";

    public static int lceDefaultMaxEfficiency = 10000;
    public static int lceOxygenBoostedMaxEfficiency = 30000;
    public static int lceDinitrogenTetroxideBoostedMaxEfficiency = 40000;
    public static int lceOverclockNominalOutput = 5120;
    public static int lceOverclockEfficiencyNumerator = 4;
    public static int lceOverclockEfficiencyDenominator = 5;
    public static int lceDinitrogenTetroxideConsumptionPerTick = 1;

    public static int lsbDefaultMaxEfficiency = 10000;
    public static int lsbOxygenBoostedMaxEfficiency = 15000;
    public static int lsbBaseNominalOutput = 2048;
    public static int lsbBoostedNominalOutput = 4096;
    public static int lsbTitaniumOutputNumerator = 3;
    public static int lsbTitaniumOutputDenominator = 2;
    public static int lsbPerformanceOutputNumerator = 2;
    public static int lsbPerformanceOutputDenominator = 1;
    public static int lsbPerformanceEfficiencyNumerator = 4;
    public static int lsbPerformanceEfficiencyDenominator = 5;
    public static int lsbBaseLubricantPerCycle = 1;
    public static int lsbBoostedLubricantPerCycle = 2;
    public static int lsbBaseOxygenConsumptionPerTick = 4;
    public static int lsbPerformanceLubricantNumerator = 2;
    public static int lsbPerformanceLubricantDenominator = 1;
    public static int lsbPerformanceOxygenNumerator = 2;
    public static int lsbPerformanceOxygenDenominator = 1;

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
        lsbDefaultMaxEfficiency = configuration.getInt(
            "defaultMaxEfficiency",
            CATEGORY_LSB,
            lsbDefaultMaxEfficiency,
            1,
            Integer.MAX_VALUE,
            "LSB max efficiency without oxygen boost. 10000 = 100%.");
        lsbOxygenBoostedMaxEfficiency = configuration.getInt(
            "oxygenBoostedMaxEfficiency",
            CATEGORY_LSB,
            lsbOxygenBoostedMaxEfficiency,
            1,
            Integer.MAX_VALUE,
            "LSB max efficiency with oxygen boost. 15000 = 150%.");
        lsbBaseNominalOutput = configuration.getInt(
            "baseNominalOutput",
            CATEGORY_LSB,
            lsbBaseNominalOutput,
            1,
            Integer.MAX_VALUE,
            "LSB nominal EU/t before efficiency without oxygen boost.");
        lsbBoostedNominalOutput = configuration.getInt(
            "boostedNominalOutput",
            CATEGORY_LSB,
            lsbBoostedNominalOutput,
            1,
            Integer.MAX_VALUE,
            "LSB nominal EU/t before efficiency with oxygen boost.");
        lsbTitaniumOutputNumerator = configuration.getInt(
            "titaniumOutputNumerator",
            CATEGORY_LSB,
            lsbTitaniumOutputNumerator,
            1,
            Integer.MAX_VALUE,
            "Titanium gearbox nominal-output multiplier numerator.");
        lsbTitaniumOutputDenominator = configuration.getInt(
            "titaniumOutputDenominator",
            CATEGORY_LSB,
            lsbTitaniumOutputDenominator,
            1,
            Integer.MAX_VALUE,
            "Titanium gearbox nominal-output multiplier denominator.");
        lsbPerformanceOutputNumerator = configuration.getInt(
            "performanceOutputNumerator",
            CATEGORY_LSB,
            lsbPerformanceOutputNumerator,
            1,
            Integer.MAX_VALUE,
            "Performance mode nominal-output multiplier numerator.");
        lsbPerformanceOutputDenominator = configuration.getInt(
            "performanceOutputDenominator",
            CATEGORY_LSB,
            lsbPerformanceOutputDenominator,
            1,
            Integer.MAX_VALUE,
            "Performance mode nominal-output multiplier denominator.");
        lsbPerformanceEfficiencyNumerator = configuration.getInt(
            "performanceEfficiencyNumerator",
            CATEGORY_LSB,
            lsbPerformanceEfficiencyNumerator,
            0,
            Integer.MAX_VALUE,
            "Performance mode efficiency multiplier numerator.");
        lsbPerformanceEfficiencyDenominator = configuration.getInt(
            "performanceEfficiencyDenominator",
            CATEGORY_LSB,
            lsbPerformanceEfficiencyDenominator,
            1,
            Integer.MAX_VALUE,
            "Performance mode efficiency multiplier denominator.");
        lsbBaseLubricantPerCycle = configuration.getInt(
            "baseLubricantPerCycle",
            CATEGORY_LSB,
            lsbBaseLubricantPerCycle,
            1,
            Integer.MAX_VALUE,
            "LSB lubricant use every 72 ticks without oxygen boost.");
        lsbBoostedLubricantPerCycle = configuration.getInt(
            "boostedLubricantPerCycle",
            CATEGORY_LSB,
            lsbBoostedLubricantPerCycle,
            1,
            Integer.MAX_VALUE,
            "LSB lubricant use every 72 ticks with oxygen boost.");
        lsbBaseOxygenConsumptionPerTick = configuration.getInt(
            "baseOxygenConsumptionPerTick",
            CATEGORY_LSB,
            lsbBaseOxygenConsumptionPerTick,
            1,
            Integer.MAX_VALUE,
            "LSB oxygen boost consumption in mB/t.");
        lsbPerformanceLubricantNumerator = configuration.getInt(
            "performanceLubricantNumerator",
            CATEGORY_LSB,
            lsbPerformanceLubricantNumerator,
            1,
            Integer.MAX_VALUE,
            "Performance mode lubricant multiplier numerator.");
        lsbPerformanceLubricantDenominator = configuration.getInt(
            "performanceLubricantDenominator",
            CATEGORY_LSB,
            lsbPerformanceLubricantDenominator,
            1,
            Integer.MAX_VALUE,
            "Performance mode lubricant multiplier denominator.");
        lsbPerformanceOxygenNumerator = configuration.getInt(
            "performanceOxygenNumerator",
            CATEGORY_LSB,
            lsbPerformanceOxygenNumerator,
            1,
            Integer.MAX_VALUE,
            "Performance mode oxygen multiplier numerator.");
        lsbPerformanceOxygenDenominator = configuration.getInt(
            "performanceOxygenDenominator",
            CATEGORY_LSB,
            lsbPerformanceOxygenDenominator,
            1,
            Integer.MAX_VALUE,
            "Performance mode oxygen multiplier denominator.");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
