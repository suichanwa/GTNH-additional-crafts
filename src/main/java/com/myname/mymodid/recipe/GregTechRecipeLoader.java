package com.myname.mymodid.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fluids.FluidStack;

import com.myname.mymodid.MyMod;
import com.myname.mymodid.fluid.ModFluids;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeBuilder;

public final class GregTechRecipeLoader {

    private GregTechRecipeLoader() {}

    public static void registerRecipes() {
        registerNitricOxideLargeChemicalReactorRecipe();
        registerDinitrogenPentoxideLargeChemicalReactorRecipe();
        removeNitricOxideRegularChemicalReactorRecipe();
    }

    private static void registerNitricOxideLargeChemicalReactorRecipe() {
        FluidStack oxygen = Materials.Oxygen.getGas(1000L);
        FluidStack nitrogen = Materials.Nitrogen.getGas(1000L);
        FluidStack nitricOxide = Materials.NitricOxide.getGas(1000L);

        if (oxygen == null || nitrogen == null || nitricOxide == null) {
            MyMod.logInfo("Skipped Nitric Oxide recipe: one or more required fluids are unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .fluidInputs(oxygen, nitrogen)
            .fluidOutputs(nitricOxide)
            .duration(6 * GTRecipeBuilder.SECONDS)
            .eut(2048)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered Large Chemical Reactor recipe (EV): 1000L Oxygen + 1000L Nitrogen -> 1000L Nitric Oxide.");
    }

    private static void registerDinitrogenPentoxideLargeChemicalReactorRecipe() {
        FluidStack nitricAcid = Materials.NitricAcid.getFluid(2000L);
        if (nitricAcid == null) {
            nitricAcid = Materials.NitricAcid.getGas(2000L);
        }
        FluidStack dinitrogenPentoxide = ModFluids.getDinitrogenPentoxide(1000L);
        FluidStack hydrogen = Materials.Hydrogen.getGas(1000L);

        if (nitricAcid == null || dinitrogenPentoxide == null || hydrogen == null) {
            MyMod.logInfo(
                "Skipped Dinitrogen Pentoxide recipe: Nitric Acid, Dinitrogen Pentoxide, or Hydrogen fluid is unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .fluidInputs(nitricAcid)
            .fluidOutputs(dinitrogenPentoxide, hydrogen)
            .duration(6 * GTRecipeBuilder.SECONDS)
            .eut(2048)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered Large Chemical Reactor recipe (EV): 2000L Nitric Acid -> 1000L Dinitrogen Pentoxide + 1000L Hydrogen.");
    }

    private static void removeNitricOxideRegularChemicalReactorRecipe() {
        List<GTRecipe> recipesToRemove = new ArrayList<>();
        for (GTRecipe recipe : RecipeMaps.chemicalReactorRecipes.getAllRecipes()) {
            if (isExactNitricOxideRecipe(recipe)) {
                recipesToRemove.add(recipe);
            }
        }

        if (recipesToRemove.isEmpty()) {
            return;
        }

        RecipeMaps.chemicalReactorRecipes.getBackend()
            .removeRecipes(recipesToRemove);
        MyMod.logInfo("Removed " + recipesToRemove.size() + " matching Nitric Oxide recipe(s) from Chemical Reactor.");
    }

    private static boolean isExactNitricOxideRecipe(GTRecipe recipe) {
        if (recipe == null || recipe.mFluidInputs == null || recipe.mFluidOutputs == null) {
            return false;
        }
        if (recipe.mInputs != null && recipe.mInputs.length > 0) {
            return false;
        }
        if (recipe.mFluidInputs.length != 2 || recipe.mFluidOutputs.length != 1) {
            return false;
        }

        FluidStack output = recipe.mFluidOutputs[0];
        if (output == null || !output.isFluidEqual(Materials.NitricOxide.getGas(1L)) || output.amount != 1000) {
            return false;
        }

        return hasFluidAmount(recipe.mFluidInputs, Materials.Oxygen.getGas(1L), 1000)
            && hasFluidAmount(recipe.mFluidInputs, Materials.Nitrogen.getGas(1L), 1000);
    }

    private static boolean hasFluidAmount(FluidStack[] fluidInputs, FluidStack fluidType, int amount) {
        if (fluidType == null) {
            return false;
        }
        for (FluidStack input : fluidInputs) {
            if (input != null && input.isFluidEqual(fluidType) && input.amount == amount) {
                return true;
            }
        }
        return false;
    }
}
