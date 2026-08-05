package gtnh_additional_crafts.recipe.loaders;

import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.recipe.util.FluidLookup;

public final class KeroseneRecipes {

    private KeroseneRecipes() {}

    public static void register() {
        registerKeroseneHydrocrackingRecipe();
        registerKeroseneSulfuricLightFuelRecipe();
    }

    public static void registerKeroseneHydrocrackingRecipe() {
        FluidStack kerosene = GTPPFluids.Kerosene == null
            ? FluidLookup.getFirstAvailableFluid(1000, "kerosene", "Kerosene")
            : new FluidStack(GTPPFluids.Kerosene, 1000);
        FluidStack hydrogen = FluidLookup.getFluidOrGas(Materials.Hydrogen, 300L);
        FluidStack lightFuel = FluidLookup.getFluidOrGas(Materials.LightFuel, 700L);
        FluidStack methane = FluidLookup.getFluidOrGas(Materials.Methane, 400L);

        if (kerosene == null || hydrogen == null || lightFuel == null || methane == null) {
            MyMod.logInfo("Skipped Kerosene hydrocracking recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(kerosene, hydrogen)
            .fluidOutputs(lightFuel, methane)
            .duration(20 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod
            .logInfo("Registered LCR recipe: IC-2 + 1000L Kerosene + 300L Hydrogen -> 700L Light Fuel + 400L Methane.");
    }

    public static void registerKeroseneSulfuricLightFuelRecipe() {
        FluidStack kerosene = GTPPFluids.Kerosene == null
            ? FluidLookup.getFirstAvailableFluid(1000, "kerosene", "Kerosene")
            : new FluidStack(GTPPFluids.Kerosene, 1000);
        FluidStack sulfuricAcid = FluidLookup.getFluidOrGas(Materials.SulfuricAcid, 100L);
        FluidStack sulfuricLightFuel = FluidLookup.getFluidOrGas(Materials.SulfuricLightFuel, 900L);

        if (kerosene == null || sulfuricAcid == null || sulfuricLightFuel == null) {
            MyMod.logInfo(
                "Skipped Kerosene + Sulfuric Acid -> Sulfuric Light Fuel recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(3))
            .fluidInputs(kerosene, sulfuricAcid)
            .fluidOutputs(sulfuricLightFuel)
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        MyMod.logInfo(
            "Registered Chemical Reactor recipe: IC-3 + 1000L Kerosene + 100L Sulfuric Acid -> 900L Sulfuric Light Fuel.");
    }

}
