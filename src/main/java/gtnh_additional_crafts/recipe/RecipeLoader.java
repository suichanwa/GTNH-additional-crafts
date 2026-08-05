package gtnh_additional_crafts.recipe;

import gtnh_additional_crafts.recipe.loaders.BioTarRecipes;
import gtnh_additional_crafts.recipe.loaders.BiomassRecipes;
import gtnh_additional_crafts.recipe.loaders.CircuitBoardEliteRecipes;
import gtnh_additional_crafts.recipe.loaders.CircuitBoardEpoxyRecipes;
import gtnh_additional_crafts.recipe.loaders.CircuitBoardFiberglassAdvancedRecipes;
import gtnh_additional_crafts.recipe.loaders.CircuitBoardFiberglassRecipes;
import gtnh_additional_crafts.recipe.loaders.CoalGasRecipes;
import gtnh_additional_crafts.recipe.loaders.CrimsonCultRecipes;
import gtnh_additional_crafts.recipe.loaders.EthanolRecipes;
import gtnh_additional_crafts.recipe.loaders.GlycerolRecipes;
import gtnh_additional_crafts.recipe.loaders.KeroseneRecipes;
import gtnh_additional_crafts.recipe.loaders.MiscRecipes;
import gtnh_additional_crafts.recipe.loaders.NaphthaXyleneRecipes;
import gtnh_additional_crafts.recipe.loaders.NitricOxideRecipes;
import gtnh_additional_crafts.recipe.loaders.NuclearFuelRecipes;
import gtnh_additional_crafts.recipe.loaders.PhenolRecipes;
import gtnh_additional_crafts.recipe.loaders.PropaneRecipes;
import gtnh_additional_crafts.recipe.loaders.RadioactiveWasteRecipes;
import gtnh_additional_crafts.recipe.loaders.RocketFuelRecipes;
import gtnh_additional_crafts.recipe.loaders.SolderingFuelBinderRecipes;
import gtnh_additional_crafts.recipe.loaders.SulfurChemistryRecipes;

public final class RecipeLoader {

    private RecipeLoader() {}

    public static void registerRecipes() {
        MiscRecipes.register();
        NitricOxideRecipes.registerAddition();
        BiomassRecipes.register();
        GlycerolRecipes.register();
        PhenolRecipes.register();
        KeroseneRecipes.register();
        BioTarRecipes.register();
        NaphthaXyleneRecipes.register();
        SulfurChemistryRecipes.register();
        CoalGasRecipes.register();
        PropaneRecipes.register();
        RocketFuelRecipes.register();
        EthanolRecipes.register();
        SolderingFuelBinderRecipes.register();
        NuclearFuelRecipes.register();
        CrimsonCultRecipes.register();
        RadioactiveWasteRecipes.register();
        CircuitBoardFiberglassRecipes.register();
        CircuitBoardFiberglassAdvancedRecipes.register();
        CircuitBoardEliteRecipes.register();
        CircuitBoardEpoxyRecipes.register();
        NitricOxideRecipes.removeLegacy();
    }
}
