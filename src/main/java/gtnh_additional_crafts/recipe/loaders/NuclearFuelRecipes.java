package gtnh_additional_crafts.recipe.loaders;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.material.nuclear.MaterialsFluorides;
import gtPlusPlus.core.material.nuclear.MaterialsNuclides;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.recipe.util.FluidLookup;
import gtnh_additional_crafts.recipe.util.ItemLookup;

public final class NuclearFuelRecipes {

    private NuclearFuelRecipes() {}

    public static void register() {
        registerLftrThoriumPlutoniumFuelRecipes();
        registerNaquadahDustFuelRodRecipes();
    }

    public static void registerLftrThoriumPlutoniumFuelRecipes() {
        FluidStack lftrFuelBase = MaterialsNuclides.LiFBeF2UF4.getFluidStack(1000);
        FluidStack lftrThoriumFuel = MaterialsNuclides.LiFBeF2ThF4UF4.getFluidStack(1000);
        FluidStack lftrHybridFuel = MaterialsNuclides.LiFBeF2ZrF4UF4.getFluidStack(1000);
        FluidStack moltenSaltBlanket = MaterialsNuclides.Li2BeF4.getFluidStack(200);
        FluidStack thoriumFluoride = MaterialsFluorides.THORIUM_TETRAFLUORIDE.getFluidStack(120);
        FluidStack zirconiumFluoride = MaterialsFluorides.ZIRCONIUM_TETRAFLUORIDE.getFluidStack(120);
        FluidStack uraniumTetrafluoride = MaterialsFluorides.URANIUM_TETRAFLUORIDE.getFluidStack(80);
        FluidStack uraniumHexafluoride = MaterialsFluorides.URANIUM_HEXAFLUORIDE.getFluidStack(1);
        FluidStack plutonium = FluidLookup
            .getMaterialFluidOrFallback(Materials.Plutonium, 100L, "molten.plutonium", "plutonium", "moltenplutonium");

        if (lftrFuelBase == null || lftrThoriumFuel == null
            || lftrHybridFuel == null
            || moltenSaltBlanket == null
            || thoriumFluoride == null
            || zirconiumFluoride == null
            || uraniumTetrafluoride == null
            || uraniumHexafluoride == null
            || plutonium == null) {
            MyMod.logInfo(
                "Skipped LFTR thorium/plutonium expansion: one or more required LFTR/plutonium fluids are unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(31))
            .fluidInputs(
                MaterialsNuclides.LiFBeF2UF4.getFluidStack(800),
                MaterialsFluorides.THORIUM_TETRAFLUORIDE.getFluidStack(120),
                MaterialsFluorides.URANIUM_TETRAFLUORIDE.getFluidStack(80))
            .fluidOutputs(MaterialsNuclides.LiFBeF2ThF4UF4.getFluidStack(1000))
            .duration(16 * GTRecipeBuilder.MINUTES)
            .eut(2048)
            .addTo(GTPPRecipeMaps.fissionFuelProcessingRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(32))
            .fluidInputs(
                MaterialsNuclides.LiFBeF2UF4.getFluidStack(780),
                MaterialsFluorides.ZIRCONIUM_TETRAFLUORIDE.getFluidStack(120),
                MaterialsFluorides.THORIUM_TETRAFLUORIDE.getFluidStack(40),
                FluidLookup.getMaterialFluidOrFallback(
                    Materials.Plutonium,
                    60L,
                    "molten.plutonium",
                    "plutonium",
                    "moltenplutonium"))
            .fluidOutputs(MaterialsNuclides.LiFBeF2ZrF4UF4.getFluidStack(1000))
            .duration(20 * GTRecipeBuilder.MINUTES)
            .eut(4096)
            .addTo(GTPPRecipeMaps.fissionFuelProcessingRecipes);

        GTValues.RA.stdBuilder()
            .fluidInputs(
                MaterialsNuclides.LiFBeF2ThF4UF4.getFluidStack(100),
                MaterialsNuclides.Li2BeF4.getFluidStack(200))
            .fluidOutputs(
                MaterialsNuclides.LiFBeF2UF4FP.getFluidStack(120),
                MaterialsNuclides.LiFBeF2ThF4.getFluidStack(160),
                MaterialsFluorides.URANIUM_HEXAFLUORIDE.getFluidStack(24))
            .duration(1 * GTRecipeBuilder.MINUTES + 40 * GTRecipeBuilder.SECONDS)
            .eut(0)
            .metadata(GTRecipeConstants.LFTR_OUTPUT_POWER, 40960)
            .addTo(GTPPRecipeMaps.liquidFluorineThoriumReactorRecipes);

        GTValues.RA.stdBuilder()
            .fluidInputs(
                MaterialsNuclides.LiFBeF2ZrF4UF4.getFluidStack(100),
                MaterialsNuclides.Li2BeF4.getFluidStack(200),
                FluidLookup.getMaterialFluidOrFallback(
                    Materials.Plutonium,
                    10L,
                    "molten.plutonium",
                    "plutonium",
                    "moltenplutonium"))
            .fluidOutputs(
                MaterialsNuclides.LiFBeF2UF4FP.getFluidStack(80),
                MaterialsNuclides.LiFBeF2ThF4.getFluidStack(120),
                MaterialsFluorides.URANIUM_HEXAFLUORIDE.getFluidStack(12))
            .duration(1 * GTRecipeBuilder.MINUTES + 40 * GTRecipeBuilder.SECONDS)
            .eut(0)
            .metadata(GTRecipeConstants.LFTR_OUTPUT_POWER, 24576)
            .addTo(GTPPRecipeMaps.liquidFluorineThoriumReactorRecipes);

        MyMod.logInfo(
            "Registered LFTR thorium/plutonium expansion: Reactor Fuel Processing Plant crafts (IC-31/IC-32) plus LFTR thorium-only and hybrid burn profiles.");
    }

    public static void registerNaquadahDustFuelRodRecipes() {
        ItemStack naquadahRod = ItemList.RodNaquadah.get(1L);
        ItemStack naquadahRodDual = ItemList.RodNaquadah2.get(1L);
        ItemStack naquadahRodQuad = ItemList.RodNaquadah4.get(1L);

        ItemStack depletedNaquadahRod = ItemList.DepletedRodNaquadah.get(1L);
        ItemStack depletedNaquadahRodDual = ItemList.DepletedRodNaquadah2.get(1L);
        ItemStack depletedNaquadahRodQuad = ItemList.DepletedRodNaquadah4.get(1L);

        ItemStack naquadahDust = GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 4L);
        ItemStack largeTungstensteelFluidCell = resolveLargeTungstensteelFluidCell();

        if (naquadahRod == null || naquadahRodDual == null
            || naquadahRodQuad == null
            || depletedNaquadahRod == null
            || depletedNaquadahRodDual == null
            || depletedNaquadahRodQuad == null
            || naquadahDust == null
            || largeTungstensteelFluidCell == null) {
            MyMod.logInfo(
                "Skipped Naquadah dust fuel rod integration: one or more rod, depleted-rod, dust, or large cell items are unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(naquadahDust, largeTungstensteelFluidCell.copy(), GTUtility.getIntegratedCircuit(1))
            .itemOutputs(naquadahRod.copy())
            .duration(20 * GTRecipeBuilder.SECONDS)
            .eut(1920)
            .addTo(RecipeMaps.assemblerRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(2, naquadahRod),
                largeTungstensteelFluidCell.copy(),
                GTUtility.getIntegratedCircuit(2))
            .itemOutputs(naquadahRodDual.copy())
            .duration(30 * GTRecipeBuilder.SECONDS)
            .eut(1920)
            .addTo(RecipeMaps.assemblerRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(2, naquadahRodDual),
                largeTungstensteelFluidCell.copy(),
                GTUtility.getIntegratedCircuit(4))
            .itemOutputs(naquadahRodQuad.copy())
            .duration(40 * GTRecipeBuilder.SECONDS)
            .eut(1920)
            .addTo(RecipeMaps.assemblerRecipes);

        replaceDepletedNaquadahFuelRodRecycleRecipes(
            depletedNaquadahRod,
            depletedNaquadahRodDual,
            depletedNaquadahRodQuad);

        MyMod.logInfo(
            "Registered Naquadah fuel rod chain from Naquadah Dust + Large Tungstensteel Fluid Cell (single/double/quad) and low-yield depleted recycling.");
    }

    private static void replaceDepletedNaquadahFuelRodRecycleRecipes(ItemStack depletedSingle, ItemStack depletedDual,
        ItemStack depletedQuad) {
        List<GTRecipe> recipesToRemove = new ArrayList<>();
        collectThermalCentrifugeRecipesByInput(recipesToRemove, depletedSingle);
        collectThermalCentrifugeRecipesByInput(recipesToRemove, depletedDual);
        collectThermalCentrifugeRecipesByInput(recipesToRemove, depletedQuad);

        if (!recipesToRemove.isEmpty()) {
            RecipeMaps.thermalCentrifugeRecipes.getBackend()
                .removeRecipes(recipesToRemove);
            MyMod.logInfo(
                "Removed " + recipesToRemove.size() + " default depleted Naquadah rod Thermal Centrifuge recipe(s).");
        }

        ItemStack ironDust = GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1L);
        ItemStack naquadahTinyDust = GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Naquadah, 1L);
        if (ironDust == null || naquadahTinyDust == null) {
            MyMod.logInfo("Skipped custom depleted Naquadah rod recycling: iron or naquadah dust outputs unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(depletedSingle.copy())
            .itemOutputs(ironDust.copy(), naquadahTinyDust.copy())
            .outputChances(10000, 500)
            .duration(25 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.thermalCentrifugeRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(depletedDual.copy())
            .itemOutputs(GTUtility.copyAmount(2, ironDust), naquadahTinyDust.copy())
            .outputChances(10000, 900)
            .duration(30 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.thermalCentrifugeRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(depletedQuad.copy())
            .itemOutputs(GTUtility.copyAmount(4, ironDust), naquadahTinyDust.copy())
            .outputChances(10000, 1600)
            .duration(35 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.thermalCentrifugeRecipes);
    }

    private static void collectThermalCentrifugeRecipesByInput(List<GTRecipe> out, ItemStack input) {
        if (input == null || input.getItem() == null) {
            return;
        }
        for (GTRecipe recipe : RecipeMaps.thermalCentrifugeRecipes.getAllRecipes()) {
            if (recipe == null || recipe.mInputs == null || recipe.mInputs.length == 0) {
                continue;
            }
            for (ItemStack stack : recipe.mInputs) {
                if (stack != null && stack.isItemEqual(input)) {
                    out.add(recipe);
                    break;
                }
            }
        }
    }

    private static ItemStack resolveLargeTungstensteelFluidCell() {
        ItemStack stack = ItemLookup.resolveFirstOreDictStack(
            1,
            "cellLargeTungstenSteel",
            "cellLargeTungstensteel",
            "largeFluidCellTungstenSteel",
            "largeFluidCellTungstensteel");
        if (stack != null) {
            return stack;
        }
        return ItemLookup.resolveFirstOreDictMatchByTokens(1, "cell", "large", "tungstensteel");
    }

}
