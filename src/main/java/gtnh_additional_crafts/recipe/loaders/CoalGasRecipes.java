package gtnh_additional_crafts.recipe.loaders;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.Materials;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtnh_additional_crafts.recipe.util.FluidLookup;
import gtnh_additional_crafts.recipe.util.MachineRecipes;

public final class CoalGasRecipes {

    private CoalGasRecipes() {}

    public static void register() {
        registerCoalGasWaterGasShiftRecipe();
        registerCoalGasBoudouardCarbonDepositionRecipe();
    }

    public static void registerCoalGasWaterGasShiftRecipe() {
        FluidStack coalGas = getCoalGas(1000);
        FluidStack steam = FluidLookup.getFluidOrGas(Materials.Steam, 1000L);
        FluidStack hydrogen = FluidLookup.getFluidOrGas(Materials.Hydrogen, 2000L);
        FluidStack carbonDioxide = FluidLookup.getFluidOrGas(Materials.CarbonDioxide, 1000L);
        ItemStack hematiteCatalyst = GTUtility.copyAmount(0, Materials.Hematite.getDust(1));

        // Water-gas shift: CO (from Coal Gas) + H2O -> CO2 + H2, over Fe2O3 high-temperature shift
        // catalyst (not consumed) - real industrial route to enrich Hydrogen from Coal Gas.
        MachineRecipes.largeChemicalReactor()
            .itemInputs(hematiteCatalyst, GTUtility.getIntegratedCircuit(9))
            .fluidInputs(coalGas, steam)
            .fluidOutputs(hydrogen, carbonDioxide)
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .register(
                "Skipped Coal Gas water-gas shift recipe: required catalyst or fluids unavailable.",
                "Registered LCR recipe: IC-9 + 1000L Coal Gas + 1000L Steam + Hematite catalyst -> 2000L Hydrogen + 1000L Carbon Dioxide.");
    }

    public static void registerCoalGasBoudouardCarbonDepositionRecipe() {
        FluidStack coalGas = getCoalGas(2000);
        FluidStack carbonDioxide = FluidLookup.getFluidOrGas(Materials.CarbonDioxide, 1000L);
        ItemStack coalLump = Materials.Coal.getGems(1);
        ItemStack ironCatalyst = GTUtility.copyAmount(0, Materials.Iron.getDust(1));

        // Boudouard reaction: 2 CO -> C(s) + CO2, over an Iron catalyst (not consumed). Exploits
        // the CO fraction of Coal Gas to deposit solid carbon - real industrial "coking out" side
        // reaction, run here on-purpose to reclaim Coal out of Coal Gas.
        MachineRecipes.largeChemicalReactor()
            .itemInputs(ironCatalyst)
            .itemOutputs(coalLump)
            .fluidInputs(coalGas)
            .fluidOutputs(carbonDioxide)
            .duration(16 * GTRecipeBuilder.SECONDS)
            .eut(60)
            .register(
                "Skipped Coal Gas Boudouard carbon deposition recipe: required catalyst or fluids unavailable.",
                "Registered LCR recipe: 2000L Coal Gas + Iron catalyst -> 1x Coal + 1000L Carbon Dioxide.");
    }

    private static FluidStack getCoalGas(int amount) {
        if (GTPPFluids.CoalGas != null) {
            return new FluidStack(GTPPFluids.CoalGas, amount);
        }
        return FluidLookup.getFirstAvailableFluid(amount, "coalgas", "CoalGas", "coal_gas");
    }

}
