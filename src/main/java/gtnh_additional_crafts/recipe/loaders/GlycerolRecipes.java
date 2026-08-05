package gtnh_additional_crafts.recipe.loaders;

import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.recipe.util.FluidLookup;

public final class GlycerolRecipes {

    private GlycerolRecipes() {}

    public static void register() {
        registerGlycerolNitrationRecipe();
        registerGlycerolHydrogenCrackingRecipe();
        registerGlycerolFermentationRecipe();
    }

    public static void registerGlycerolNitrationRecipe() {
        FluidStack glycerol = FluidLookup.getFluidOrGas(Materials.Glycerol, 500L);
        FluidStack nitrogenDioxide = FluidLookup.getFluidOrGas(Materials.NitrogenDioxide, 500L);
        FluidStack glycerylTrinitrate = FluidLookup.getFluidOrGas(Materials.Glyceryl, 750L);

        if (glycerol == null || nitrogenDioxide == null || glycerylTrinitrate == null) {
            MyMod.logInfo("Skipped Glycerol nitration recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(glycerol, nitrogenDioxide)
            .fluidOutputs(glycerylTrinitrate)
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(
                FluidLookup.getFluidOrGas(Materials.Glycerol, 500L),
                FluidLookup.getFluidOrGas(Materials.NitrogenDioxide, 500L))
            .fluidOutputs(FluidLookup.getFluidOrGas(Materials.Glyceryl, 750L))
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered Chemical Reactor and LCR recipe: IC-1 + 500L Glycerol + 500L Nitrogen Dioxide -> 750L Glyceryl Trinitrate.");
    }

    public static void registerGlycerolHydrogenCrackingRecipe() {
        FluidStack glycerol = FluidLookup.getFluidOrGas(Materials.Glycerol, 1000L);
        FluidStack hydrogen = FluidLookup.getFluidOrGas(Materials.Hydrogen, 500L);
        FluidStack methane = FluidLookup.getFluidOrGas(Materials.Methane, 600L);
        FluidStack water = FluidLookup.getFluidOrGas(Materials.Water, 400L);

        if (glycerol == null || hydrogen == null || methane == null || water == null) {
            MyMod.logInfo("Skipped Glycerol cracking recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(glycerol, hydrogen)
            .fluidOutputs(methane, water)
            .duration(16 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo("Registered LCR recipe: IC-2 + 1000L Glycerol + 500L Hydrogen -> 600L Methane + 400L Water.");
    }

    public static void registerGlycerolFermentationRecipe() {
        FluidStack glycerol = FluidLookup.getFluidOrGas(Materials.Glycerol, 1000L);
        FluidStack vinegar = FluidLookup.getFluidOrGas(Materials.Vinegar, 700L);

        if (glycerol == null || vinegar == null) {
            MyMod.logInfo("Skipped Glycerol fermentation recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(glycerol)
            .fluidOutputs(vinegar)
            .duration(30 * GTRecipeBuilder.SECONDS)
            .eut(30)
            .addTo(RecipeMaps.fermentingRecipes);

        MyMod.logInfo("Registered Fermenter recipe: IC-1 + 1000L Glycerol -> 700L Vinegar.");
    }

}
