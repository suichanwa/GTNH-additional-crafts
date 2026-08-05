package gtnh_additional_crafts.recipe.loaders;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsKevlar;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtnh_additional_crafts.recipe.util.FluidLookup;
import gtnh_additional_crafts.recipe.util.MachineRecipes;

public final class NaphthaXyleneRecipes {

    private NaphthaXyleneRecipes() {}

    public static void register() {
        registerNaphthaToNaphthaleneRecipe();
        registerXyleneHydrodealkylationRecipes();
    }

    public static void registerNaphthaToNaphthaleneRecipe() {
        FluidStack naphtha = Materials.Naphtha.getFluid(1000L);
        ItemStack platinumCatalyst = GTUtility.copyAmount(0, Materials.Platinum.getDust(1));
        FluidStack naphthalene = GTPPFluids.Naphthalene == null
            ? FluidLookup.getFirstAvailableFluid(400, "naphthalene", "Naphthalene")
            : new FluidStack(GTPPFluids.Naphthalene, 400);
        FluidStack hydrogen = FluidLookup.getFluidOrGas(Materials.Hydrogen, 300L);
        FluidStack methane = FluidLookup.getFluidOrGas(Materials.Methane, 200L);

        MachineRecipes.largeChemicalReactor()
            .itemInputs(platinumCatalyst)
            .fluidInputs(naphtha)
            .fluidOutputs(naphthalene, hydrogen, methane)
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(2048)
            .register(
                "Skipped LCR Naphtha -> Naphthalene recipe: required catalyst or fluids unavailable.",
                "Registered LCR recipe: 1000L Naphtha + Platinum Dust catalyst -> 400L Naphthalene + 300L Hydrogen + 200L Methane.");
    }

    public static void registerXyleneHydrodealkylationRecipes() {
        registerXyleneHydrodealkylationRecipe(
            FluidLookup.getFluidOrGas(Materials.Dimethylbenzene, 1000L),
            "1,2-Dimethylbenzene",
            11);
        registerXyleneHydrodealkylationRecipe(
            FluidLookup.getFluidOrGas(MaterialsKevlar.IIIDimethylbenzene, 1000L),
            "1,3-Dimethylbenzene",
            13);
    }

    private static void registerXyleneHydrodealkylationRecipe(FluidStack xylene, String xyleneName, int circuit) {
        FluidStack hydrogen = FluidLookup.getFluidOrGas(Materials.Hydrogen, 4000L);
        FluidStack benzene = FluidLookup.getFluidOrGas(Materials.Benzene, 1000L);
        FluidStack methane = FluidLookup.getFluidOrGas(Materials.Methane, 2000L);
        ItemStack chromeCatalyst = GTUtility.copyAmount(0, Materials.Chrome.getDust(1));

        // Hydrodealkylation: C8H10 + 2 H2 -> C6H6 + 2 CH4 (Cr2O3 catalyst, not consumed)
        MachineRecipes.largeChemicalReactor()
            .itemInputs(chromeCatalyst, GTUtility.getIntegratedCircuit(circuit))
            .fluidInputs(xylene, hydrogen)
            .fluidOutputs(benzene, methane)
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .register(
                "Skipped " + xyleneName + " hydrodealkylation recipe: required catalyst or fluids unavailable.",
                "Registered LCR recipe: 1000L " + xyleneName
                    + " + 4000L Hydrogen + Chrome Dust catalyst -> 1000L Benzene + 2000L Methane.");
    }

}
