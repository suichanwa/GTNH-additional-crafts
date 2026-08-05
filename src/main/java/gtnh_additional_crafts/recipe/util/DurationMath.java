package gtnh_additional_crafts.recipe.util;

import gregtech.api.util.GTUtility;

public final class DurationMath {

    private DurationMath() {}

    public static int scaleDurationByPercent(int baseDuration, int durationPercent) {
        return GTUtility.safeInt((long) baseDuration * Math.max(1, durationPercent) / 100L);
    }

    public static int scaleDurationForSpeedBoost(int baseDuration, int speedBoostPercent) {
        return Math.max(1, GTUtility.safeInt((long) baseDuration * 100L / (100L + Math.max(0, speedBoostPercent))));
    }

}
