package com.myname.mymodid.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.myname.mymodid.MyMod;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public final class GregTechRecipeLoader {

    private GregTechRecipeLoader() {}

    public static void registerRecipes() {
        registerNitricOxideLargeChemicalReactorRecipe();
        registerAlgaeBiomassToCompostRecipe();
        registerAlgaeProcessingChainRecipes();
        registerNitrogenRocketFuelUpgradeRecipe();
        registerJetFuelRocketFuelRecipe();
        registerAcetaldehydeHydrogenationRecipe();
        registerMethanolCarbonMonoxideHydrogenToEthanolRecipe();
        registerMethaneToAcetyleneDehydratorRecipe();
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

    private static void registerAlgaeProcessingChainRecipes() {
        Item basicAgrichemItem = GameRegistry.findItem("miscutils", "item.BasicAgrichemItem");
        if (basicAgrichemItem == null) {
            MyMod.logInfo("Skipped algae processing chain: GT++ BasicAgrichem item is unavailable.");
            return;
        }

        Item basicAlgaeItem = GameRegistry.findItem("miscutils", "item.BasicAlgaeItem");
        ItemStack algaeBiomass = new ItemStack(basicAgrichemItem, 1, 1);
        ItemStack crushedAlgae = basicAlgaeItem == null ? new ItemStack(basicAgrichemItem, 1, 1)
            : new ItemStack(basicAlgaeItem, 1, 0);
        ItemStack organicResidue = new ItemStack(basicAgrichemItem, 1, 8);
        if (algaeBiomass.getItem() == null || crushedAlgae.getItem() == null || organicResidue.getItem() == null) {
            MyMod.logInfo("Skipped algae processing chain: invalid GT++ item stacks.");
            return;
        }

        FluidStack water = getFluidOrGas(Materials.Water, 1000L);
        FluidStack carbonDioxide = getFluidOrGas(Materials.CarbonDioxide, 1000L);
        FluidStack biomass = getFirstAvailableFluid(1000, "biomass", "Biomass");
        FluidStack hydrogen = getFluidOrGas(Materials.Hydrogen, 1000L);
        FluidStack oxygen = getFluidOrGas(Materials.Oxygen, 1000L);

        if (water == null || carbonDioxide == null || biomass == null || hydrogen == null || oxygen == null) {
            MyMod.logInfo("Skipped algae processing chain: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(algaeBiomass)
            .itemOutputs(crushedAlgae)
            .duration(5 * GTRecipeBuilder.SECONDS)
            .eut(8)
            .addTo(RecipeMaps.fermentingRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(crushedAlgae, GTUtility.getIntegratedCircuit(6))
            .fluidInputs(getFluidOrGas(Materials.Water, 140L))
            .fluidOutputs(getFirstAvailableFluid(140, "biomass", "Biomass"))
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(24)
            .addTo(RecipeMaps.brewingRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(7))
            .itemOutputs(organicResidue)
            .fluidInputs(biomass)
            .fluidOutputs(water, carbonDioxide)
            .duration(8 * GTRecipeBuilder.SECONDS)
            .eut(30)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(organicResidue, GTUtility.getIntegratedCircuit(8))
            .fluidInputs(water, carbonDioxide)
            .fluidOutputs(hydrogen, oxygen)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(60)
            .addTo(RecipeMaps.electrolyzerRecipes);

        MyMod.logInfo(
            "Registered algae processing chain recipes (macerator -> mixer -> chemical reactor -> electrolyzer).");
    }

    private static void registerNitrogenRocketFuelUpgradeRecipe() {
        FluidStack rp1Fuel = getFirstAvailableFluid(1000, "rp1fuel", "rocketfuelmixb", "RP1Fuel", "RocketFuelMixB");
        FluidStack nitrogen = Materials.Nitrogen.getGas(1000L);
        FluidStack oxygen = Materials.Oxygen.getGas(500L);
        FluidStack upgradedRocketFuel = getFirstAvailableFluid(750, "rocketfuelmixc", "RocketFuelMixC");

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

    private static void registerJetFuelRocketFuelRecipe() {
        FluidStack jetFuel = getFirstAvailableFluid(
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

    private static void registerAcetaldehydeHydrogenationRecipe() {
        FluidStack acetaldehyde = getFirstAvailableFluid(1000, "acetaldehyde", "Acetaldehyde");
        FluidStack hydrogen = Materials.Hydrogen.getGas(1000L);
        if (hydrogen == null) {
            hydrogen = Materials.Hydrogen.getFluid(1000L);
        }
        FluidStack ethanol = getFirstAvailableFluid(1000, "ethanol", "Ethanol");

        if (acetaldehyde == null || hydrogen == null || ethanol == null) {
            MyMod.logInfo("Skipped Acetaldehyde + Hydrogen -> Ethanol recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(acetaldehyde, hydrogen)
            .fluidOutputs(ethanol)
            .duration(6 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        MyMod.logInfo("Registered Chemical Reactor recipe: 1000L Acetaldehyde + 1000L Hydrogen -> 1000L Ethanol.");
    }

    private static void registerMethanolCarbonMonoxideHydrogenToEthanolRecipe() {
        FluidStack methanol = getFluidOrGas(Materials.Methanol, 1000L);
        FluidStack carbonMonoxide = getFluidOrGas(Materials.CarbonMonoxide, 1000L);
        FluidStack hydrogen = getFluidOrGas(Materials.Hydrogen, 1000L);
        FluidStack ethanol = getFluidOrGas(Materials.Ethanol, 1000L);

        if (methanol == null || carbonMonoxide == null || hydrogen == null || ethanol == null) {
            MyMod.logInfo("Skipped Methanol + CO + H2 -> Ethanol recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(methanol, carbonMonoxide, hydrogen)
            .fluidOutputs(ethanol)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(24))
            .fluidInputs(
                getFluidOrGas(Materials.Methanol, 9000L),
                getFluidOrGas(Materials.CarbonMonoxide, 9000L),
                getFluidOrGas(Materials.Hydrogen, 9000L))
            .fluidOutputs(getFluidOrGas(Materials.Ethanol, 9000L))
            .duration(90 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered LCR recipes: IC-1 1000L Methanol + 1000L CO + 1000L H2 -> 1000L Ethanol; IC-24 9x batch.");
    }

    private static void registerMethaneToAcetyleneDehydratorRecipe() {
        FluidStack methane = getFluidOrGas(Materials.Methane, 2000L);
        FluidStack acetylene = getFirstAvailableFluid(1000, "acetylene", "Acetylene");
        FluidStack hydrogen = getFluidOrGas(Materials.Hydrogen, 3000L);

        if (methane == null || acetylene == null || hydrogen == null) {
            MyMod.logInfo("Skipped Dehydrator recipe: required fluids for 2 CH4 -> C2H2 + 3 H2 unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .fluidInputs(methane)
            .fluidOutputs(acetylene, hydrogen)
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(GTPPRecipeMaps.chemicalDehydratorRecipes);

        MyMod.logInfo("Registered Dehydrator recipe: 2000L Methane -> 1000L Acetylene + 3000L Hydrogen.");
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

    private static FluidStack getFirstAvailableFluid(int amount, String... names) {
        for (String name : names) {
            FluidStack stack = FluidRegistry.getFluidStack(name, amount);
            if (stack != null) {
                return stack;
            }
        }
        return null;
    }

    private static FluidStack getFluidOrGas(Materials material, long amount) {
        if (material == null) {
            return null;
        }
        FluidStack fluid = material.getFluid(amount);
        if (fluid != null) {
            return fluid;
        }
        return material.getGas(amount);
    }

}
