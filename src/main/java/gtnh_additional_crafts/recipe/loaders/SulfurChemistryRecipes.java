package gtnh_additional_crafts.recipe.loaders;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsKevlar;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.fluid.ModFluids;
import gtnh_additional_crafts.recipe.util.FluidLookup;
import gtnh_additional_crafts.recipe.util.MachineRecipes;

public final class SulfurChemistryRecipes {

    private SulfurChemistryRecipes() {}

    public static void register() {
        registerCarbonDisulfideAlternativeRecipes();
        registerCalciumCyanamideRecipes();
        registerCalciumHypochloriteRecipe();
    }

    public static void registerCarbonDisulfideAlternativeRecipes() {
        registerCarbonDisulfideMethaneSulfurRecipe();
        registerCarbonDisulfideHydrogenSulfideCarbonRecipe();
        registerCarbonylSulfideChainRecipes();
        registerCarbonDisulfideCharcoalBlastFurnaceRecipe();
    }

    public static void registerCalciumCyanamideRecipes() {
        // Frank-Caro nitrogen-fixation process, step 1: CaC2 + N2 -> CaCN2 + C, needs the same
        // ~1000-1100C furnace tier that forms Calcium Carbide itself.
        ItemStack calciumCarbide = MaterialsKevlar.CalciumCarbide.getDust(3);
        FluidStack nitrogenForCyanamide = FluidLookup.getFluidOrGas(Materials.Nitrogen, 1000L);
        FluidStack calciumCyanamideOutput = ModFluids.getCalciumCyanamide(1000);
        ItemStack carbonByproduct = Materials.Carbon.getDust(1);

        if (calciumCarbide == null || calciumCarbide.getItem() == null
            || nitrogenForCyanamide == null
            || calciumCyanamideOutput == null
            || carbonByproduct == null
            || carbonByproduct.getItem() == null) {
            MyMod.logInfo("Skipped Calcium Cyanamide EBF recipe: required item or fluids unavailable.");
        } else {
            GTValues.RA.stdBuilder()
                .itemInputs(calciumCarbide)
                .itemOutputs(carbonByproduct)
                .fluidInputs(nitrogenForCyanamide)
                .fluidOutputs(calciumCyanamideOutput)
                .duration(15 * GTRecipeBuilder.SECONDS)
                .eut(120)
                .metadata(GTRecipeConstants.COIL_HEAT, 1200)
                .addTo(RecipeMaps.blastFurnaceRecipes);

            MyMod.logInfo(
                "Registered EBF recipe: 3x Calcium Carbide Dust + 1000L Nitrogen -> 1000L Calcium Cyanamide + 1x Carbon Dust.");
        }

        // Step 2: hydrolysis, CaCN2 + 3 H2O -> CaCO3 + 2 NH3 -- regenerates Calcite and releases Ammonia.
        FluidStack calciumCyanamideInput = ModFluids.getCalciumCyanamide(1000);
        FluidStack waterForHydrolysis = FluidLookup.getFluidOrGas(Materials.Water, 1500L);
        ItemStack calcite = Materials.Calcite.getDust(5);
        FluidStack ammonia = Materials.Ammonia.getGas(2000L);

        if (calciumCyanamideInput == null || waterForHydrolysis == null
            || calcite == null
            || calcite.getItem() == null
            || ammonia == null) {
            MyMod.logInfo("Skipped Calcium Cyanamide hydrolysis recipe: required item or fluids unavailable.");
            return;
        }

        MachineRecipes.largeChemicalReactor()
            .itemOutputs(calcite)
            .fluidInputs(calciumCyanamideInput, waterForHydrolysis)
            .fluidOutputs(ammonia)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(30)
            .register(
                "Skipped Calcium Cyanamide hydrolysis recipe: required item or fluids unavailable.",
                "Registered LCR recipe: 1000L Calcium Cyanamide + 1500L Water -> 2000L Ammonia + 5x Calcite Dust.");
    }

    public static void registerCalciumHypochloriteRecipe() {
        // Bleaching powder: 2 Ca(OH)2 + 2 Cl2 -> Ca(ClO)2 + CaCl2 + 2 H2O.
        Item hydratedLimeItem = gtPlusPlus.core.item.ModItems.dustCalciumHydroxide;
        if (hydratedLimeItem == null) {
            MyMod.logInfo("Skipped Calcium Hypochlorite recipe: Hydrated Lime Dust is missing.");
            return;
        }
        ItemStack hydratedLime = new ItemStack(hydratedLimeItem, 2);
        FluidStack chlorine = FluidLookup.getFluidOrGas(Materials.Chlorine, 2000L);
        FluidStack calciumHypochlorite = ModFluids.getCalciumHypochlorite(1000);
        ItemStack calciumChloride = bartworks.system.material.WerkstoffLoader.CalciumChloride.get(OrePrefixes.dust, 3);
        FluidStack water = FluidLookup.getFluidOrGas(Materials.Water, 1000L);

        if (chlorine == null || calciumHypochlorite == null
            || calciumChloride == null
            || calciumChloride.getItem() == null
            || water == null) {
            MyMod.logInfo("Skipped Calcium Hypochlorite recipe: required item or fluids unavailable.");
            return;
        }

        MachineRecipes.largeChemicalReactor()
            .itemInputs(hydratedLime)
            .itemOutputs(calciumChloride)
            .fluidInputs(chlorine)
            .fluidOutputs(calciumHypochlorite, water)
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(30)
            .register(
                "Skipped Calcium Hypochlorite recipe: required item or fluids unavailable.",
                "Registered LCR recipe: 2x Hydrated Lime Dust + 2000L Chlorine -> 1000L Calcium Hypochlorite + 1000L Water + 3x Calcium Chloride Dust.");
    }

    private static void registerCarbonDisulfideMethaneSulfurRecipe() {
        FluidStack methane = FluidLookup.getFluidOrGas(Materials.Methane, 1000L);
        FluidStack carbonDisulfide = getCarbonDisulfide(1000);
        FluidStack hydrogenSulfide = FluidLookup.getFluidOrGas(Materials.HydricSulfide, 2000L);
        ItemStack sulfurDust = GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 4L);
        ItemStack silicaCatalyst = GTUtility.copyAmount(0, Materials.SiliconDioxide.getDust(1));

        if (methane == null || carbonDisulfide == null
            || hydrogenSulfide == null
            || sulfurDust == null
            || silicaCatalyst == null
            || silicaCatalyst.getItem() == null) {
            MyMod.logInfo(
                "Skipped Methane + Sulfur -> Carbon Disulfide recipe: required catalyst, items, or fluids unavailable.");
            return;
        }

        // Modern industrial route: CH4 + 4 S -> CS2 + 2 H2S (~600 C, silica catalyst, not consumed)
        MachineRecipes.largeChemicalReactor()
            .itemInputs(sulfurDust, silicaCatalyst, GTUtility.getIntegratedCircuit(4))
            .fluidInputs(methane)
            .fluidOutputs(carbonDisulfide, hydrogenSulfide)
            .duration(15 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .register(
                "Skipped Methane + Sulfur -> Carbon Disulfide recipe: required catalyst, items, or fluids unavailable.",
                "Registered LCR recipe: IC-4 + 1000L Methane + 4x Sulfur Dust + Silicon Dioxide catalyst -> 1000L Carbon Disulfide + 2000L Hydrogen Sulfide.");
    }

    private static void registerCarbonDisulfideHydrogenSulfideCarbonRecipe() {
        FluidStack hydrogenSulfide = FluidLookup.getFluidOrGas(Materials.HydricSulfide, 2000L);
        FluidStack carbonDisulfide = getCarbonDisulfide(1000);
        FluidStack hydrogen = FluidLookup.getFluidOrGas(Materials.Hydrogen, 2000L);
        ItemStack carbonDust = Materials.Carbon.getDust(1);

        if (hydrogenSulfide == null || carbonDisulfide == null
            || hydrogen == null
            || carbonDust == null
            || carbonDust.getItem() == null) {
            MyMod.logInfo(
                "Skipped Hydrogen Sulfide + Carbon -> Carbon Disulfide recipe: required items or fluids unavailable.");
            return;
        }

        // H2S sink: 2 H2S + C -> CS2 + 2 H2 (high temperature, endothermic)
        MachineRecipes.largeChemicalReactor()
            .itemInputs(carbonDust, GTUtility.getIntegratedCircuit(5))
            .fluidInputs(hydrogenSulfide)
            .fluidOutputs(carbonDisulfide, hydrogen)
            .duration(20 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .register(
                "Skipped Hydrogen Sulfide + Carbon -> Carbon Disulfide recipe: required items or fluids unavailable.",
                "Registered LCR recipe: IC-5 + 2000L Hydrogen Sulfide + 1x Carbon Dust -> 1000L Carbon Disulfide + 2000L Hydrogen.");
    }

    private static void registerCarbonylSulfideChainRecipes() {
        FluidStack carbonMonoxide = FluidLookup.getFluidOrGas(Materials.CarbonMonoxide, 1000L);
        FluidStack carbonylSulfide = ModFluids.getCarbonylSulfide(1000);
        FluidStack carbonylSulfideInput = ModFluids.getCarbonylSulfide(2000);
        FluidStack carbonDisulfide = getCarbonDisulfide(1000);
        FluidStack carbonDioxide = FluidLookup.getFluidOrGas(Materials.CarbonDioxide, 1000L);
        ItemStack sulfurDust = GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1L);
        ItemStack aluminaCatalyst = GTUtility.copyAmount(0, Materials.Aluminiumoxide.getDust(1));

        if (carbonMonoxide == null || carbonylSulfide == null
            || carbonylSulfideInput == null
            || carbonDisulfide == null
            || carbonDioxide == null
            || sulfurDust == null
            || aluminaCatalyst == null
            || aluminaCatalyst.getItem() == null) {
            MyMod.logInfo("Skipped Carbonyl Sulfide chain: required catalyst, items, or fluids unavailable.");
            return;
        }

        // Step 1: CO + S -> COS
        MachineRecipes.chemicalReactor()
            .itemInputs(sulfurDust, GTUtility.getIntegratedCircuit(6))
            .fluidInputs(carbonMonoxide)
            .fluidOutputs(carbonylSulfide)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .register("", "");

        MachineRecipes.largeChemicalReactor()
            .itemInputs(sulfurDust, GTUtility.getIntegratedCircuit(6))
            .fluidInputs(carbonMonoxide)
            .fluidOutputs(carbonylSulfide)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .register("", "");

        // Step 2: 2 COS -> CS2 + CO2 (disproportionation over alumina, not consumed)
        MachineRecipes.largeChemicalReactor()
            .itemInputs(aluminaCatalyst, GTUtility.getIntegratedCircuit(7))
            .fluidInputs(carbonylSulfideInput)
            .fluidOutputs(carbonDisulfide, carbonDioxide)
            .duration(16 * GTRecipeBuilder.SECONDS)
            .eut(240)
            .register(
                "",
                "Registered Carbonyl Sulfide chain: IC-6 CO + Sulfur -> COS (CR/LCR); IC-7 2000L COS + Alumina catalyst -> 1000L Carbon Disulfide + 1000L Carbon Dioxide (LCR).");
    }

    private static void registerCarbonDisulfideCharcoalBlastFurnaceRecipe() {
        ItemStack charcoal = GTOreDictUnificator.get(OrePrefixes.gem, Materials.Charcoal, 8L);
        ItemStack sulfurDust = GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 16L);
        ItemStack darkAsh = GTOreDictUnificator.get(OrePrefixes.dust, Materials.DarkAsh, 2L);
        FluidStack carbonDisulfide = getCarbonDisulfide(3000);

        if (charcoal == null || sulfurDust == null || darkAsh == null || carbonDisulfide == null) {
            MyMod.logInfo(
                "Skipped Charcoal + Sulfur -> Carbon Disulfide blast furnace recipe: required items or fluid unavailable.");
            return;
        }

        // Historic retort route: C + 2 S -> CS2, lower yield than the coal coke recipe
        GTValues.RA.stdBuilder()
            .itemInputs(charcoal, sulfurDust, GTUtility.getIntegratedCircuit(11))
            .itemOutputs(darkAsh)
            .fluidOutputs(carbonDisulfide)
            .duration(10 * GTRecipeBuilder.MINUTES)
            .eut(120)
            .metadata(GTRecipeConstants.COIL_HEAT, 1500)
            .addTo(RecipeMaps.blastFurnaceRecipes);

        MyMod.logInfo(
            "Registered EBF recipe: IC-11 + 8x Charcoal + 16x Sulfur Dust -> 3000L Carbon Disulfide + 2x Dark Ash.");
    }

    private static FluidStack getCarbonDisulfide(int amount) {
        if (GTPPFluids.CarbonDisulfide != null) {
            return new FluidStack(GTPPFluids.CarbonDisulfide, amount);
        }
        return FluidLookup.getFirstAvailableFluid(amount, "carbondisulfide", "CarbonDisulfide", "carbon_disulfide");
    }

}
