package gtnh_additional_crafts.item;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

public final class ModItems {

    public static final SodiumBatteryX16Item SODIUM_BATTERY_X16_IV = new SodiumBatteryX16Item();

    private ModItems() {}

    public static void registerItems() {
        GameRegistry.registerItem(SODIUM_BATTERY_X16_IV, SodiumBatteryX16Item.NAME);
    }

    public static ItemStack sodiumBatteryX16IV() {
        return new ItemStack(SODIUM_BATTERY_X16_IV);
    }
}
