package gtnh_additional_crafts.recipe.loaders;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.recipe.util.FluidLookup;

public final class EthanolRecipes {

    private EthanolRecipes() {}

    public static void register() {
        registerAcetaldehydeHydrogenationRecipe();
        registerMethanolCarbonMonoxideHydrogenToEthanolRecipe();
        registerEthyleneDirectHydrationEthanolRecipe();
        registerSyngasFischerTropschEthanolRecipe();
        registerMethaneToAcetyleneDehydratorRecipe();
    }

    public static void registerAcetaldehydeHydrogenationRecipe() {
        FluidStack acetaldehyde = FluidLookup.getFirstAvailableFluid(1000, "acetaldehyde", "Acetaldehyde");
        FluidStack hydrogen = Materials.Hydrogen.getGas(1000L);
        if (hydrogen == null) {
            hydrogen = Materials.Hydrogen.getFluid(1000L);
        }
        FluidStack ethanol = FluidLookup.getFirstAvailableFluid(1000, "ethanol", "Ethanol");
        // Raney Nickel catalyst, not consumed. Also disambiguates this recipe from unrelated bare-circuit
        // recipes in the same recipe map that could otherwise be satisfied by a subset of these fluids.
        ItemStack nickelCatalyst = GTUtility.copyAmount(0, Materials.Nickel.getDust(1));

        if (acetaldehyde == null || hydrogen == null
            || ethanol == null
            || nickelCatalyst == null
            || nickelCatalyst.getItem() == null) {
            MyMod
                .logInfo("Skipped Acetaldehyde + Hydrogen -> Ethanol recipe: required catalyst or fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(nickelCatalyst, GTUtility.getIntegratedCircuit(2))
            .fluidInputs(acetaldehyde, hydrogen)
            .fluidOutputs(ethanol)
            .duration(6 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        MyMod.logInfo(
            "Registered Chemical Reactor recipe: Nickel Dust catalyst + 1000L Acetaldehyde + 1000L Hydrogen -> 1000L Ethanol.");
    }

    public static void registerMethanolCarbonMonoxideHydrogenToEthanolRecipe() {
        FluidStack methanol = FluidLookup.getFluidOrGas(Materials.Methanol, 1000L);
        FluidStack carbonMonoxide = FluidLookup.getFluidOrGas(Materials.CarbonMonoxide, 1000L);
        FluidStack hydrogen = FluidLookup.getFluidOrGas(Materials.Hydrogen, 1000L);
        FluidStack ethanol = FluidLookup.getFluidOrGas(Materials.Ethanol, 1000L);
        // Cobalt catalyst (syngas/methanol homologation to higher alcohols), not consumed. Prevents this
        // recipe's fluid combo from colliding with unrelated bare-circuit recipes in the same LCR recipe map
        // (e.g. other IC-1/IC-24 recipes that only need a subset of Methanol/CO/Hydrogen).
        ItemStack cobaltCatalyst = GTUtility.copyAmount(0, Materials.Cobalt.getDust(1));

        if (methanol == null || carbonMonoxide == null
            || hydrogen == null
            || ethanol == null
            || cobaltCatalyst == null
            || cobaltCatalyst.getItem() == null) {
            MyMod.logInfo("Skipped Methanol + CO + H2 -> Ethanol recipe: required catalyst or fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(cobaltCatalyst, GTUtility.getIntegratedCircuit(1))
            .fluidInputs(methanol, carbonMonoxide, hydrogen)
            .fluidOutputs(ethanol)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(cobaltCatalyst, GTUtility.getIntegratedCircuit(24))
            .fluidInputs(
                FluidLookup.getFluidOrGas(Materials.Methanol, 9000L),
                FluidLookup.getFluidOrGas(Materials.CarbonMonoxide, 9000L),
                FluidLookup.getFluidOrGas(Materials.Hydrogen, 9000L))
            .fluidOutputs(FluidLookup.getFluidOrGas(Materials.Ethanol, 9000L))
            .duration(90 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered LCR recipes: Cobalt Dust catalyst + IC-1 1000L Methanol + 1000L CO + 1000L H2 -> 1000L Ethanol; IC-24 9x batch.");
    }

    public static void registerEthyleneDirectHydrationEthanolRecipe() {
        // C2H4 + H2O -> C2H6O (acid-catalyzed direct hydration, industrial ethanol synthesis route)
        FluidStack ethylene = FluidLookup.getFluidOrGas(Materials.Ethylene, 1000L);
        FluidStack steam = FluidLookup.getFluidOrGas(Materials.Steam, 1000L);
        FluidStack ethanol = FluidLookup.getFluidOrGas(Materials.Ethanol, 1000L);
        ItemStack phosphoricAcidCatalyst = GTUtility.copyAmount(0, Materials.PhosphoricAcid.getCells(1));

        if (ethylene == null || steam == null
            || ethanol == null
            || phosphoricAcidCatalyst == null
            || phosphoricAcidCatalyst.getItem() == null) {
            MyMod.logInfo("Skipped Ethylene + Steam -> Ethanol recipe: required catalyst or fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(phosphoricAcidCatalyst, GTUtility.getIntegratedCircuit(3))
            .fluidInputs(ethylene, steam)
            .fluidOutputs(ethanol)
            .duration(8 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        MyMod.logInfo(
            "Registered Chemical Reactor recipe: Phosphoric Acid catalyst + 1000L Ethylene + 1000L Steam -> 1000L Ethanol.");
    }

    public static void registerSyngasFischerTropschEthanolRecipe() {
        // 2 CO + 4 H2 -> C2H6O + H2O (Fischer-Tropsch style syngas-to-ethanol synthesis)
        FluidStack carbonMonoxide = FluidLookup.getFluidOrGas(Materials.CarbonMonoxide, 2000L);
        FluidStack hydrogen = FluidLookup.getFluidOrGas(Materials.Hydrogen, 4000L);
        FluidStack ethanol = FluidLookup.getFluidOrGas(Materials.Ethanol, 1000L);
        FluidStack water = FluidLookup.getFluidOrGas(Materials.Water, 1000L);
        ItemStack ironCatalyst = GTUtility.copyAmount(0, Materials.Iron.getDust(1));

        if (carbonMonoxide == null || hydrogen == null
            || ethanol == null
            || water == null
            || ironCatalyst == null
            || ironCatalyst.getItem() == null) {
            MyMod.logInfo(
                "Skipped Syngas -> Ethanol (Fischer-Tropsch) recipe: required catalyst or fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(ironCatalyst, GTUtility.getIntegratedCircuit(5))
            .fluidInputs(carbonMonoxide, hydrogen)
            .fluidOutputs(ethanol, water)
            .duration(14 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered LCR recipe: Iron Dust catalyst + IC-5 2000L CO + 4000L H2 -> 1000L Ethanol + 1000L Water.");
    }

    public static void registerMethaneToAcetyleneDehydratorRecipe() {
        FluidStack methane = FluidLookup.getFluidOrGas(Materials.Methane, 2000L);
        FluidStack acetylene = FluidLookup.getFirstAvailableFluid(1000, "acetylene", "Acetylene");
        FluidStack hydrogen = FluidLookup.getFluidOrGas(Materials.Hydrogen, 3000L);

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

}
