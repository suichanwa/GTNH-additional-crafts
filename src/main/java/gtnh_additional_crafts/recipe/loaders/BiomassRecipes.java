package gtnh_additional_crafts.recipe.loaders;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.recipe.util.FluidLookup;

public final class BiomassRecipes {

    private BiomassRecipes() {}

    public static void register() {
        registerAlgaeBiomassToCompostRecipe();
        registerAlgaeProcessingChainRecipes();
        registerCelluloseFiberBiomassRecipe();
    }

    public static void registerAlgaeBiomassToCompostRecipe() {
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

    public static void registerAlgaeProcessingChainRecipes() {
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

        FluidStack water = FluidLookup.getFluidOrGas(Materials.Water, 1000L);
        FluidStack carbonDioxide = FluidLookup.getFluidOrGas(Materials.CarbonDioxide, 1000L);
        FluidStack biomass = FluidLookup.getFirstAvailableFluid(1000, "biomass", "Biomass");
        FluidStack hydrogen = FluidLookup.getFluidOrGas(Materials.Hydrogen, 1000L);
        FluidStack oxygen = FluidLookup.getFluidOrGas(Materials.Oxygen, 1000L);

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
            .fluidInputs(FluidLookup.getFluidOrGas(Materials.Water, 140L))
            .fluidOutputs(FluidLookup.getFirstAvailableFluid(140, "biomass", "Biomass"))
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
            .itemInputs(organicResidue, GTUtility.getIntegratedCircuit(8), ItemList.Cell_Empty.get(2))
            .itemOutputs(Materials.Hydrogen.getCells(1), Materials.Oxygen.getCells(1))
            .fluidInputs(water, carbonDioxide)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(60)
            .addTo(RecipeMaps.electrolyzerRecipes);

        MyMod.logInfo(
            "Registered algae processing chain recipes (macerator -> mixer -> chemical reactor -> electrolyzer). "
                + "Electrolyzer step outputs Hydrogen/Oxygen as cells (single-block electrolyzer can't handle two fluid outputs).");
    }

    public static void registerCelluloseFiberBiomassRecipe() {
        ItemStack celluloseFiber = GregtechItemList.CelluloseFiber.get(2L, new Object[0]);
        FluidStack water = FluidLookup.getFluidOrGas(Materials.Water, 1000L);
        FluidStack biomass = FluidLookup.getFirstAvailableFluid(500, "ic2biomass", "biomass", "Biomass");

        if (celluloseFiber == null || celluloseFiber.getItem() == null || water == null || biomass == null) {
            MyMod.logInfo("Skipped Cellulose Fiber -> Biomass brewery recipe: required item or fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(celluloseFiber)
            .fluidInputs(water)
            .fluidOutputs(biomass)
            .duration(170)
            .eut(4)
            .addTo(RecipeMaps.brewingRecipes);

        MyMod.logInfo("Registered Brewery recipe: 2x Cellulose Fiber + 1000L Water -> 500L Biomass.");
    }

}
