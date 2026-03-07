package com.myname.mymodid.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.registry.GameRegistry;

import com.myname.mymodid.MyMod;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;

public final class GregTechRecipeLoader {

    private GregTechRecipeLoader() {}

    public static void registerRecipes() {
        registerNitricOxideLargeChemicalReactorRecipe();
        registerAlgaeBiomassToCompostRecipe();
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
            .itemInputs(GTUtility.getIntegratedCircuit(9))
            .fluidInputs(oxygen, nitrogen)
            .fluidOutputs(nitricOxide)
            .duration(6 * GTRecipeBuilder.SECONDS)
            .eut(2048)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered Large Chemical Reactor recipe (EV): IC-9 + 1000L Oxygen + 1000L Nitrogen -> 1000L Nitric Oxide.");
    }

    private static void registerAlgaeBiomassToCompostRecipe() {
        Item basicAgrichemItem = GameRegistry.findItem("miscutils", "item.BasicAgrichemItem");
        if (basicAgrichemItem == null) {
            MyMod.logInfo("Skipped algae biomass -> compost recipe: GT++ BasicAgrichem item is unavailable.");
            return;
        }

        ItemStack algaeBiomass = new ItemStack(basicAgrichemItem, 8, 0);
        ItemStack compost = new ItemStack(basicAgrichemItem, 1, 8);
        if (algaeBiomass.getItem() == null || compost.getItem() == null) {
            MyMod.logInfo("Skipped algae biomass -> compost recipe: invalid GT++ item stacks.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(algaeBiomass)
            .itemOutputs(compost)
            .duration(5 * GTRecipeBuilder.SECONDS)
            .eut(2)
            .addTo(RecipeMaps.compressorRecipes);

        MyMod.logInfo("Registered Compressor recipe: 8x Algae Biomass -> 1x Compost.");
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
