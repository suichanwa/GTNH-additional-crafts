package gtnh_additional_crafts.recipe.loaders;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.Materials;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtnh_additional_crafts.recipe.util.FluidLookup;
import gtnh_additional_crafts.recipe.util.MachineRecipes;

public final class PhenolRecipes {

    private PhenolRecipes() {}

    public static void register() {
        registerPhenolFormaldehydeResinRecipe();
        registerPhenolHydrogenToCyclohexaneRecipe();
        registerPhenolHydrogenToBenzeneRecipe();
        registerPhenolNitrationRecipe();
    }

    public static void registerPhenolFormaldehydeResinRecipe() {
        FluidStack phenol = FluidLookup.getFluidOrGas(Materials.Phenol, 500L);
        FluidStack formaldehyde = GTPPFluids.Formaldehyde == null
            ? FluidLookup.getFirstAvailableFluid(500, "formaldehyde", "Formaldehyde")
            : new FluidStack(GTPPFluids.Formaldehyde, 500);
        FluidStack liquidResin = GTPPFluids.LiquidResin == null
            ? FluidLookup.getFirstAvailableFluid(750, "liquidresin", "Liquid Resin")
            : new FluidStack(GTPPFluids.LiquidResin, 750);

        MachineRecipes.chemicalReactor()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(phenol, formaldehyde)
            .fluidOutputs(liquidResin)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .register(
                "Skipped Phenol + Formaldehyde -> Liquid Resin recipe: required fluids unavailable.",
                "Registered Chemical Reactor recipe: IC-1 + 500L Phenol + 500L Formaldehyde -> 750L Liquid Resin.");
    }

    public static void registerPhenolHydrogenToCyclohexaneRecipe() {
        FluidStack phenol = FluidLookup.getFluidOrGas(Materials.Phenol, 1000L);
        FluidStack hydrogen = FluidLookup.getFluidOrGas(Materials.Hydrogen, 500L);
        FluidStack cyclohexane = GTPPFluids.Cyclohexane == null
            ? FluidLookup.getFirstAvailableFluid(850, "cyclohexane", "Cyclohexane")
            : new FluidStack(GTPPFluids.Cyclohexane, 850);

        MachineRecipes.chemicalReactor()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(phenol, hydrogen)
            .fluidOutputs(cyclohexane)
            .duration(15 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .register(
                "Skipped Phenol + Hydrogen -> Cyclohexane recipe: required fluids unavailable.",
                "Registered Chemical Reactor recipe: IC-2 + 1000L Phenol + 500L Hydrogen -> 850L Cyclohexane.");
    }

    public static void registerPhenolHydrogenToBenzeneRecipe() {
        // Catalytic hydrodeoxygenation: C6H5OH + H2 -> C6H6 + H2O (Pd catalyst, not consumed)
        FluidStack phenol = FluidLookup.getFluidOrGas(Materials.Phenol, 1000L);
        FluidStack hydrogen = FluidLookup.getFluidOrGas(Materials.Hydrogen, 500L);
        FluidStack water = FluidLookup.getFluidOrGas(Materials.Water, 200L);
        FluidStack benzene = FluidLookup.getFluidOrGas(Materials.Benzene, 800L);
        ItemStack palladiumCatalyst = GTUtility.copyAmount(0, Materials.Palladium.getDust(1));

        MachineRecipes.largeChemicalReactor()
            .itemInputs(palladiumCatalyst, GTUtility.getIntegratedCircuit(2))
            .fluidInputs(phenol, hydrogen)
            .fluidOutputs(benzene, water)
            .duration(15 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .register(
                "Skipped Phenol + Hydrogen -> Benzene recipe: required catalyst or fluids unavailable.",
                "Registered LCR recipe: IC-2 + Palladium Dust catalyst + 1000L Phenol + 500L Hydrogen -> 800L Benzene + 200L Water.");
    }

    public static void registerPhenolNitrationRecipe() {
        FluidStack phenol = FluidLookup.getFluidOrGas(Materials.Phenol, 500L);
        FluidStack nitrationMixture = FluidLookup.getFluidOrGas(Materials.NitrationMixture, 500L);
        FluidStack nitrobenzene = GTPPFluids.Nitrobenzene == null
            ? FluidLookup.getFirstAvailableFluid(750, "nitrobenzene", "Nitrobenzene")
            : new FluidStack(GTPPFluids.Nitrobenzene, 750);

        MachineRecipes.largeChemicalReactor()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(phenol, nitrationMixture)
            .fluidOutputs(nitrobenzene)
            .duration(20 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .register(
                "Skipped Phenol + Nitration Mixture -> Nitrobenzene recipe: required fluids unavailable.",
                "Registered LCR recipe: IC-1 + 500L Phenol + 500L Nitration Mixture -> 750L Nitrobenzene.");
    }

}
