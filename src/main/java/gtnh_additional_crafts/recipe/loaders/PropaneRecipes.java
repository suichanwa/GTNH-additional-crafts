package gtnh_additional_crafts.recipe.loaders;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.Materials;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtnh_additional_crafts.recipe.util.FluidLookup;
import gtnh_additional_crafts.recipe.util.MachineRecipes;

public final class PropaneRecipes {

    private PropaneRecipes() {}

    public static void register() {
        registerPropeneHydrogenationPropaneRecipe();
        registerPropaneDehydrogenationPropeneRecipe();
    }

    public static void registerPropeneHydrogenationPropaneRecipe() {
        FluidStack propene = FluidLookup.getFluidOrGas(Materials.Propene, 1000L);
        FluidStack hydrogen = FluidLookup.getFluidOrGas(Materials.Hydrogen, 1000L);
        FluidStack propane = FluidLookup.getFluidOrGas(Materials.Propane, 1000L);
        ItemStack nickelCatalyst = GTUtility.copyAmount(0, Materials.Nickel.getDust(1));

        // Catalytic hydrogenation: C3H6 + H2 -> C3H8, over a Nickel catalyst (not consumed).
        // Real industrial route to synthesize Propane, alternative to cracking it out of oil/LPG.
        MachineRecipes.chemicalReactor()
            .itemInputs(nickelCatalyst, GTUtility.getIntegratedCircuit(3))
            .fluidInputs(propene, hydrogen)
            .fluidOutputs(propane)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(30)
            .register(
                "Skipped Propene hydrogenation -> Propane recipe: required catalyst or fluids unavailable.",
                "Registered Chemical Reactor recipe: 1000L Propene + 1000L Hydrogen + Nickel catalyst -> 1000L Propane.");
    }

    public static void registerPropaneDehydrogenationPropeneRecipe() {
        FluidStack propane = FluidLookup.getFluidOrGas(Materials.Propane, 1000L);
        FluidStack propene = FluidLookup.getFluidOrGas(Materials.Propene, 1000L);
        FluidStack hydrogen = FluidLookup.getFluidOrGas(Materials.Hydrogen, 1000L);
        ItemStack platinumCatalyst = GTUtility.copyAmount(0, Materials.Platinum.getDust(1));

        // Catalytic dehydrogenation (PDH process): C3H8 -> C3H6 + H2, over a Platinum catalyst
        // (not consumed). Endothermic, real on-purpose industrial route to Propene (UOP Oleflex-style),
        // the mirror reaction of the Propene hydrogenation recipe above.
        MachineRecipes.chemicalReactor()
            .itemInputs(platinumCatalyst, GTUtility.getIntegratedCircuit(4))
            .fluidInputs(propane)
            .fluidOutputs(propene, hydrogen)
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .register(
                "Skipped Propane dehydrogenation -> Propene recipe: required catalyst or fluids unavailable.",
                "Registered Chemical Reactor recipe: 1000L Propane + Platinum catalyst -> 1000L Propene + 1000L Hydrogen.");
    }

}
