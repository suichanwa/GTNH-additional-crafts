package gtnh_additional_crafts.machine;

import net.minecraft.item.ItemStack;

import gtnh_additional_crafts.Config;
import gtnh_additional_crafts.MyMod;

public final class ModMachines {

    public static MTESmallLiquidBoiler smallLiquidBoiler;
    public static MTELargeLiquidBoiler largeLiquidBoiler;

    private ModMachines() {}

    public static void registerMachines() {
        smallLiquidBoiler = new MTESmallLiquidBoiler(
            Config.smallLiquidBoilerId,
            "gtnh_additional_crafts.small_liquid_boiler",
            "Small Liquid Boiler");
        largeLiquidBoiler = new MTELargeLiquidBoiler(
            Config.largeLiquidBoilerId,
            "gtnh_additional_crafts.large_liquid_boiler",
            "High Pressure Liquid Boiler");

        MyMod.logInfo(
            "Registered Liquid Boilers: Small (ID " + Config.smallLiquidBoilerId
                + "), Large (ID "
                + Config.largeLiquidBoilerId
                + ")");
    }

    public static ItemStack getSmallLiquidBoiler(long amount) {
        return smallLiquidBoiler != null ? smallLiquidBoiler.getStackForm(amount) : null;
    }

    public static ItemStack getLargeLiquidBoiler(long amount) {
        return largeLiquidBoiler != null ? largeLiquidBoiler.getStackForm(amount) : null;
    }
}
