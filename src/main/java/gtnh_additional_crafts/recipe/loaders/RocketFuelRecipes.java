package gtnh_additional_crafts.recipe.loaders;

import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.fluid.ModFluids;
import gtnh_additional_crafts.recipe.util.FluidLookup;

public final class RocketFuelRecipes {

    private RocketFuelRecipes() {}

    public static void register() {
        registerCryonitroxOxidizerRecipe();
        registerNitrogenRocketFuelUpgradeRecipe();
        registerJetFuelRocketFuelRecipe();
    }

    public static void registerCryonitroxOxidizerRecipe() {
        FluidStack liquidOxygen = FluidLookup
            .getMaterialFluidOrFallback(Materials.LiquidOxygen, 500L, "liquidoxygen", "liquid_oxygen", "liquid.oxygen");
        FluidStack liquidNitrogen = FluidLookup.getMaterialFluidOrFallback(
            Materials.LiquidNitrogen,
            500L,
            "liquidnitrogen",
            "liquid_nitrogen",
            "liquid.nitrogen");
        if (liquidOxygen == null) {
            liquidOxygen = FluidLookup.getFluidOrGas(Materials.Oxygen, 500L);
        }
        if (liquidNitrogen == null) {
            liquidNitrogen = FluidLookup.getFluidOrGas(Materials.Nitrogen, 500L);
        }
        FluidStack cryonitroxOxidizer = ModFluids.getCryonitroxOxidizer(1000);

        if (liquidOxygen == null || liquidNitrogen == null || cryonitroxOxidizer == null) {
            MyMod.logInfo(
                "Skipped Cryonitrox Oxidizer recipe: liquid oxygen, liquid nitrogen, or Cryonitrox fluid is unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(21))
            .fluidInputs(liquidOxygen, liquidNitrogen)
            .fluidOutputs(cryonitroxOxidizer)
            .duration(8 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.mixerRecipes);

        MyMod.logInfo(
            "Registered Mixer recipe: 500L Liquid Oxygen + 500L Liquid Nitrogen -> 1000L Cryonitrox Oxidizer.");
    }

    public static void registerNitrogenRocketFuelUpgradeRecipe() {
        FluidStack rp1Fuel = FluidLookup
            .getFirstAvailableFluid(1000, "rp1fuel", "rocketfuelmixb", "RP1Fuel", "RocketFuelMixB");
        FluidStack nitrogen = Materials.Nitrogen.getGas(1000L);
        FluidStack oxygen = Materials.Oxygen.getGas(500L);
        FluidStack upgradedRocketFuel = FluidLookup.getFirstAvailableFluid(750, "rocketfuelmixc", "RocketFuelMixC");

        if (rp1Fuel == null || nitrogen == null || oxygen == null || upgradedRocketFuel == null) {
            MyMod.logInfo(
                "Skipped nitrogen RP-1 upgrade recipe: RP-1, Nitrogen, Oxygen, or upgraded rocket fuel fluid is unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(22))
            .fluidInputs(rp1Fuel, nitrogen, oxygen)
            .fluidOutputs(upgradedRocketFuel)
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo("Registered LCR recipe: 1000L RP-1 + 1000L Nitrogen + 500L Oxygen -> 750L CN3H7O3 Rocket Fuel.");
    }

    public static void registerJetFuelRocketFuelRecipe() {
        FluidStack jetFuel = FluidLookup.getFirstAvailableFluid(
            1000,
            "jetfuela",
            "jetfuel_a",
            "jet_fuel_a",
            "jetfuel",
            "jet_fuel",
            "jet fuel a",
            "jet fuel",
            "JetFuelA",
            "JetFuel",
            "Jet Fuel A",
            "Jet Fuel");

        if (jetFuel == null) {
            MyMod.logInfo("Skipped Jet Fuel A rocket fuel recipe: Jet Fuel A fluid is unavailable.");
            return;
        }

        if (hasRocketFuelRecipe(jetFuel)) {
            MyMod.logInfo("Skipped Jet Fuel A rocket fuel recipe: fuel already registered.");
            return;
        }

        addRocketFuelRecipe(jetFuel, 512);
        MyMod.logInfo("Registered rocket fuel: 1000L Jet Fuel A -> rocket fuel value 512.");
    }

    private static boolean hasRocketFuelRecipe(FluidStack fuel) {
        if (fuel == null || GTPPRecipeMaps.rocketFuels == null) {
            return false;
        }
        for (GTRecipe recipe : GTPPRecipeMaps.rocketFuels.getAllRecipes()) {
            if (recipe == null || recipe.mFluidInputs == null) {
                continue;
            }
            for (FluidStack input : recipe.mFluidInputs) {
                if (input != null && input.isFluidEqual(fuel)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void addRocketFuelRecipe(FluidStack fuel, int fuelValue) {
        if (fuel == null || fuel.getFluid() == null || GTPPRecipeMaps.rocketFuels == null) {
            return;
        }
        GTPPRecipeMaps.rocketFuels.add(
            new GTRecipe(
                false,
                GTValues.emptyItemStackArray,
                GTValues.emptyItemStackArray,
                null,
                GTValues.emptyIntArray,
                new FluidStack[] { new FluidStack(fuel.getFluid(), 1000) },
                GTValues.emptyFluidStackArray,
                0,
                0,
                fuelValue));
    }

}
