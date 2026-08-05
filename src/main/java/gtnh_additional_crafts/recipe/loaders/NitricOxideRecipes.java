package gtnh_additional_crafts.recipe.loaders;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.recipe.util.MachineRecipes;

public final class NitricOxideRecipes {

    private NitricOxideRecipes() {}

    public static void registerAddition() {
        FluidStack oxygen = Materials.Oxygen.getGas(1000L);
        FluidStack nitrogen = Materials.Nitrogen.getGas(1000L);
        FluidStack nitricOxide = Materials.NitricOxide.getGas(1000L);

        MachineRecipes.largeChemicalReactor()
            .itemInputs(GTUtility.getIntegratedCircuit(9))
            .fluidInputs(oxygen, nitrogen)
            .fluidOutputs(nitricOxide)
            .duration(6 * GTRecipeBuilder.SECONDS)
            .eut(2048)
            .register(
                "Skipped Nitric Oxide recipe: one or more required fluids are unavailable.",
                "Registered Large Chemical Reactor recipe (EV): IC-9 + 1000L Oxygen + 1000L Nitrogen -> 1000L Nitric Oxide.");
    }

    public static void removeLegacy() {
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
