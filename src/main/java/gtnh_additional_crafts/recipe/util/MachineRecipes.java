package gtnh_additional_crafts.recipe.util;

import gregtech.api.recipe.RecipeMaps;

/**
 * Entry points for {@link MachineRecipeBuilder}, one per commonly-used {@code RecipeMap} in this
 * mod. Use these instead of hand-rolling {@code GTValues.RA.stdBuilder()...addTo(RecipeMaps.X)}
 * plus the null-check-and-skip boilerplate in every recipe registration method.
 */
public final class MachineRecipes {

    private MachineRecipes() {}

    public static MachineRecipeBuilder chemicalReactor() {
        return new MachineRecipeBuilder(RecipeMaps.chemicalReactorRecipes);
    }

    public static MachineRecipeBuilder largeChemicalReactor() {
        return new MachineRecipeBuilder(RecipeMaps.multiblockChemicalReactorRecipes);
    }

    public static MachineRecipeBuilder assembler() {
        return new MachineRecipeBuilder(RecipeMaps.assemblerRecipes);
    }
}
