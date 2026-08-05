package gtnh_additional_crafts.recipe.loaders;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.item.ModItems;
import gtnh_additional_crafts.recipe.util.MachineRecipes;

public final class MiscRecipes {

    private MiscRecipes() {}

    public static void register() {
        registerSodiumBatteryX16Recipe();
        registerPotassiumOxygenToPotashRecipe();
    }

    public static void registerSodiumBatteryX16Recipe() {
        ItemStack quadSodiumBattery = GregtechItemList.Battery_RE_EV_Sodium.get(1L);
        if (quadSodiumBattery == null) {
            MyMod.logInfo("Skipped 16x Sodium Battery recipe: Quad Cell Sodium Battery is missing.");
            return;
        }

        GTModHandler.addCraftingRecipe(
            ModItems.sodiumBatteryX16IV(),
            GTModHandler.RecipeBits.BUFFERED,
            new Object[] { "BWB", "CTC", "BWB", 'B', quadSodiumBattery, 'W',
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Aluminium, 1L), 'C', "circuitData", 'T',
                ItemList.Transformer_EV_HV.get(1L) });
    }

    public static void registerPotassiumOxygenToPotashRecipe() {
        ItemStack potassiumDust = GTOreDictUnificator.get(OrePrefixes.dust, Materials.Potassium, 2L);
        FluidStack oxygen = Materials.Oxygen.getGas(1000L);
        ItemStack potashDust = GTOreDictUnificator.get(OrePrefixes.dust, Materials.Potash, 1L);

        MachineRecipes.chemicalReactor()
            .itemInputs(potassiumDust)
            .itemOutputs(potashDust)
            .fluidInputs(oxygen)
            .duration(5 * GTRecipeBuilder.SECONDS)
            .eut(30)
            .register(
                "Skipped Potassium + Oxygen -> Potash recipe: required items/fluids unavailable.",
                "Registered Chemical Reactor recipe: 2x Potassium Dust + 1000L Oxygen -> 1x Potash Dust.");
    }

}
