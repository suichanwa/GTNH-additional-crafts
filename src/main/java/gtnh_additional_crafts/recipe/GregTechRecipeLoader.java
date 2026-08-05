package gtnh_additional_crafts.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import goodgenerator.loader.Loaders;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsKevlar;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.metadata.PCBFactoryTierKey;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTUtility;
import gregtech.api.util.PCBFactoryManager;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.material.nuclear.MaterialsFluorides;
import gtPlusPlus.core.material.nuclear.MaterialsNuclides;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.fluid.ModFluids;
import gtnh_additional_crafts.item.ModItems;

public final class GregTechRecipeLoader {

    private GregTechRecipeLoader() {}

    private static final int CRUDE_BIO_TAR_BASE_DISTILLATION_DURATION = 20 * GTRecipeBuilder.SECONDS;
    private static final int CRUDE_BIO_TAR_MIDDLE_DISTILLATION_DURATION = scaleDurationByPercent(
        CRUDE_BIO_TAR_BASE_DISTILLATION_DURATION,
        65);
    private static final int CRUDE_BIO_TAR_LIGHT_DISTILLATION_DURATION = scaleDurationForSpeedBoost(
        scaleDurationByPercent(CRUDE_BIO_TAR_BASE_DISTILLATION_DURATION, 35),
        70);
    private static final int BEESWAX_FURNACE_BURN_TICKS = 500;

    public static void registerRecipes() {
        registerSodiumBatteryX16Recipe();
        registerPotassiumOxygenToPotashRecipe();
        registerNitricOxideLargeChemicalReactorRecipe();
        registerAlgaeBiomassToCompostRecipe();
        registerAlgaeProcessingChainRecipes();
        registerCelluloseFiberBiomassRecipe();
        registerGlycerolNitrationRecipe();
        registerGlycerolHydrogenCrackingRecipe();
        registerGlycerolFermentationRecipe();
        registerPhenolFormaldehydeResinRecipe();
        registerPhenolHydrogenToCyclohexaneRecipe();
        registerPhenolHydrogenToBenzeneRecipe();
        registerPhenolNitrationRecipe();
        registerKeroseneHydrocrackingRecipe();
        registerKeroseneSulfuricLightFuelRecipe();
        registerBiomassCrudeBioTarCokeOvenRecipe();
        registerLogCokeOvenWoodVinegarRecipe();
        registerCrudeBioTarDistillationRecipe();
        registerCrudeBioTarMiddleFractionDistillationRecipe();
        registerCrudeBioTarLightFractionDistillationRecipe();
        registerWoodVinegarCalciumAcetateRecipe();
        registerNaphthaToNaphthaleneRecipe();
        registerXyleneHydrodealkylationRecipes();
        registerCarbonDisulfideAlternativeRecipes();
        registerCalciumCyanamideRecipes();
        registerCalciumHypochloriteRecipe();
        registerCoalGasWaterGasShiftRecipe();
        registerCoalGasBoudouardCarbonDepositionRecipe();
        registerPropeneHydrogenationPropaneRecipe();
        registerPropaneDehydrogenationPropeneRecipe();
        registerCryonitroxOxidizerRecipe();
        registerNitrogenRocketFuelUpgradeRecipe();
        registerJetFuelRocketFuelRecipe();
        registerAcetaldehydeHydrogenationRecipe();
        registerMethanolCarbonMonoxideHydrogenToEthanolRecipe();
        registerEthyleneDirectHydrationEthanolRecipe();
        registerSyngasFischerTropschEthanolRecipe();
        registerMethaneToAcetyleneDehydratorRecipe();
        registerSolderingAlloyIronAntimonyRecipe();
        registerSuperFuelBinderBeeswaxRecipes();
        registerSuperFuelBinderCreosoteBeeswaxRecipe();
        registerBeeswaxFurnaceFuel();
        registerMagicSuperFuelBinderVoidMetalRecipes();
        registerLftrThoriumPlutoniumFuelRecipes();
        registerNaquadahDustFuelRodRecipes();
        registerCrimsonCultArmorSalvageRecipes();
        registerRadioactiveWasteNoveltyRecipes();
        registerFiberglassBoardAlternateFoilRecipes();
        registerFiberglassBoardCopperPcbFactoryRecipe();
        removeNitricOxideRegularChemicalReactorRecipe();
    }

    private static void registerSodiumBatteryX16Recipe() {
        ItemStack quadSodiumBattery = GregtechItemList.Battery_RE_EV_Sodium.get(1L);
        if (quadSodiumBattery == null) {
            MyMod.logInfo("Skipped 16x Sodium Battery recipe: Quad Cell Sodium Battery is missing.");
            return;
        }

        GTModHandler.addCraftingRecipe(
            ModItems.sodiumBatteryX16IV(),
            GTModHandler.RecipeBits.BUFFERED,
            new Object[] { "BWB", "CTC", "BWB", 'B', quadSodiumBattery, 'W',
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Aluminium, 1L), 'C', "circuitData", 'T',
                ItemList.Transformer_EV_HV.get(1L) });
    }

    private static void registerPotassiumOxygenToPotashRecipe() {
        ItemStack potassiumDust = GTOreDictUnificator.get(OrePrefixes.dust, Materials.Potassium, 2L);
        FluidStack oxygen = Materials.Oxygen.getGas(1000L);
        ItemStack potashDust = GTOreDictUnificator.get(OrePrefixes.dust, Materials.Potash, 1L);

        if (potassiumDust == null || oxygen == null || potashDust == null) {
            MyMod.logInfo("Skipped Potassium + Oxygen -> Potash recipe: required items/fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(potassiumDust)
            .itemOutputs(potashDust)
            .fluidInputs(oxygen)
            .duration(5 * GTRecipeBuilder.SECONDS)
            .eut(30)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        MyMod.logInfo("Registered Chemical Reactor recipe: 2x Potassium Dust + 1000L Oxygen -> 1x Potash Dust.");
    }

    private static void registerNitricOxideLargeChemicalReactorRecipe() {
        FluidStack oxygen = Materials.Oxygen.getGas(1000L);
        FluidStack nitrogen = Materials.Nitrogen.getGas(1000L);
        FluidStack nitricOxide = Materials.NitricOxide.getGas(1000L);

        if (oxygen == null || nitrogen == null || nitricOxide == null) {
            MyMod.logInfo("Skipped Nitric Oxide recipe: one or more required fluids are unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(9))
            .fluidInputs(oxygen, nitrogen)
            .fluidOutputs(nitricOxide)
            .duration(6 * GTRecipeBuilder.SECONDS)
            .eut(2048)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered Large Chemical Reactor recipe (EV): IC-9 + 1000L Oxygen + 1000L Nitrogen -> 1000L Nitric Oxide.");
    }

    private static void registerAlgaeBiomassToCompostRecipe() {
        Item basicAgrichemItem = GameRegistry.findItem("miscutils", "item.BasicAgrichemItem");
        if (basicAgrichemItem == null) {
            MyMod.logInfo("Skipped algae biomass -> compost recipe: GT++ BasicAgrichem item is unavailable.");
            return;
        }

        ItemStack algaeBiomass = new ItemStack(basicAgrichemItem, 8, 0);
        ItemStack compost = new ItemStack(basicAgrichemItem, 1, 8);
        if (algaeBiomass.getItem() == null || compost.getItem() == null) {
            MyMod.logInfo("Skipped algae biomass -> compost recipe: invalid GT++ item stacks.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(algaeBiomass)
            .itemOutputs(compost)
            .duration(5 * GTRecipeBuilder.SECONDS)
            .eut(2)
            .addTo(RecipeMaps.compressorRecipes);

        MyMod.logInfo("Registered Compressor recipe: 8x Algae Biomass -> 1x Compost.");
    }

    private static void registerAlgaeProcessingChainRecipes() {
        Item basicAgrichemItem = GameRegistry.findItem("miscutils", "item.BasicAgrichemItem");
        if (basicAgrichemItem == null) {
            MyMod.logInfo("Skipped algae processing chain: GT++ BasicAgrichem item is unavailable.");
            return;
        }

        Item basicAlgaeItem = GameRegistry.findItem("miscutils", "item.BasicAlgaeItem");
        ItemStack algaeBiomass = new ItemStack(basicAgrichemItem, 1, 1);
        ItemStack crushedAlgae = basicAlgaeItem == null ? new ItemStack(basicAgrichemItem, 1, 1)
            : new ItemStack(basicAlgaeItem, 1, 0);
        ItemStack organicResidue = new ItemStack(basicAgrichemItem, 1, 8);
        if (algaeBiomass.getItem() == null || crushedAlgae.getItem() == null || organicResidue.getItem() == null) {
            MyMod.logInfo("Skipped algae processing chain: invalid GT++ item stacks.");
            return;
        }

        FluidStack water = getFluidOrGas(Materials.Water, 1000L);
        FluidStack carbonDioxide = getFluidOrGas(Materials.CarbonDioxide, 1000L);
        FluidStack biomass = getFirstAvailableFluid(1000, "biomass", "Biomass");
        FluidStack hydrogen = getFluidOrGas(Materials.Hydrogen, 1000L);
        FluidStack oxygen = getFluidOrGas(Materials.Oxygen, 1000L);

        if (water == null || carbonDioxide == null || biomass == null || hydrogen == null || oxygen == null) {
            MyMod.logInfo("Skipped algae processing chain: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(algaeBiomass)
            .itemOutputs(crushedAlgae)
            .duration(5 * GTRecipeBuilder.SECONDS)
            .eut(8)
            .addTo(RecipeMaps.fermentingRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(crushedAlgae, GTUtility.getIntegratedCircuit(6))
            .fluidInputs(getFluidOrGas(Materials.Water, 140L))
            .fluidOutputs(getFirstAvailableFluid(140, "biomass", "Biomass"))
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(24)
            .addTo(RecipeMaps.brewingRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(7))
            .itemOutputs(organicResidue)
            .fluidInputs(biomass)
            .fluidOutputs(water, carbonDioxide)
            .duration(8 * GTRecipeBuilder.SECONDS)
            .eut(30)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(organicResidue, GTUtility.getIntegratedCircuit(8), ItemList.Cell_Empty.get(2))
            .itemOutputs(Materials.Hydrogen.getCells(1), Materials.Oxygen.getCells(1))
            .fluidInputs(water, carbonDioxide)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(60)
            .addTo(RecipeMaps.electrolyzerRecipes);

        MyMod.logInfo(
            "Registered algae processing chain recipes (macerator -> mixer -> chemical reactor -> electrolyzer). "
                + "Electrolyzer step outputs Hydrogen/Oxygen as cells (single-block electrolyzer can't handle two fluid outputs).");
    }

    private static void registerNitrogenRocketFuelUpgradeRecipe() {
        FluidStack rp1Fuel = getFirstAvailableFluid(1000, "rp1fuel", "rocketfuelmixb", "RP1Fuel", "RocketFuelMixB");
        FluidStack nitrogen = Materials.Nitrogen.getGas(1000L);
        FluidStack oxygen = Materials.Oxygen.getGas(500L);
        FluidStack upgradedRocketFuel = getFirstAvailableFluid(750, "rocketfuelmixc", "RocketFuelMixC");

        if (rp1Fuel == null || nitrogen == null || oxygen == null || upgradedRocketFuel == null) {
            MyMod.logInfo(
                "Skipped nitrogen RP-1 upgrade recipe: RP-1, Nitrogen, Oxygen, or upgraded rocket fuel fluid is unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(22))
            .fluidInputs(rp1Fuel, nitrogen, oxygen)
            .fluidOutputs(upgradedRocketFuel)
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo("Registered LCR recipe: 1000L RP-1 + 1000L Nitrogen + 500L Oxygen -> 750L CN3H7O3 Rocket Fuel.");
    }

    private static void registerCryonitroxOxidizerRecipe() {
        FluidStack liquidOxygen = getMaterialFluidOrFallback(
            Materials.LiquidOxygen,
            500L,
            "liquidoxygen",
            "liquid_oxygen",
            "liquid.oxygen");
        FluidStack liquidNitrogen = getMaterialFluidOrFallback(
            Materials.LiquidNitrogen,
            500L,
            "liquidnitrogen",
            "liquid_nitrogen",
            "liquid.nitrogen");
        if (liquidOxygen == null) {
            liquidOxygen = getFluidOrGas(Materials.Oxygen, 500L);
        }
        if (liquidNitrogen == null) {
            liquidNitrogen = getFluidOrGas(Materials.Nitrogen, 500L);
        }
        FluidStack cryonitroxOxidizer = ModFluids.getCryonitroxOxidizer(1000);

        if (liquidOxygen == null || liquidNitrogen == null || cryonitroxOxidizer == null) {
            MyMod.logInfo(
                "Skipped Cryonitrox Oxidizer recipe: liquid oxygen, liquid nitrogen, or Cryonitrox fluid is unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(21))
            .fluidInputs(liquidOxygen, liquidNitrogen)
            .fluidOutputs(cryonitroxOxidizer)
            .duration(8 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.mixerRecipes);

        MyMod.logInfo(
            "Registered Mixer recipe: 500L Liquid Oxygen + 500L Liquid Nitrogen -> 1000L Cryonitrox Oxidizer.");
    }

    private static void registerBiomassCrudeBioTarCokeOvenRecipe() {
        FluidStack biomass = getFirstAvailableFluid(1000, "ic2biomass", "biomass", "Biomass");
        FluidStack nitrogen = getFluidOrGas(Materials.Nitrogen, 250L);
        FluidStack bioTar = ModFluids.getBioTar(500);
        ItemStack carbonDust = Materials.Carbon.getDust(2);

        if (biomass == null || nitrogen == null
            || bioTar == null
            || carbonDust == null
            || carbonDust.getItem() == null) {
            MyMod.logInfo("Skipped Industrial Coke Oven Crude Bio-tar recipe: required item or fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .itemOutputs(carbonDust)
            .fluidInputs(biomass, nitrogen)
            .fluidOutputs(bioTar)
            .duration(250)
            .eut(120)
            .addTo(GTPPRecipeMaps.cokeOvenRecipes);

        MyMod.logInfo(
            "Registered Industrial Coke Oven recipe: IC-4 + 1000L Biomass + 250L Nitrogen -> 500L Crude Bio-tar + 2x Carbon Dust.");
    }

    private static void registerLogCokeOvenWoodVinegarRecipe() {
        // Real destructive distillation of wood (same reaction vanilla's Pyrolyse Oven already runs on
        // logs), just carbonized in the Industrial Coke Oven multiblock instead: wood -> charcoal +
        // pyroligneous acid vapor (Wood Vinegar), under an inert Nitrogen atmosphere to keep it from
        // just burning to ash.
        ItemStack logs = resolveFirstOreDictStack(16, "logWood");
        FluidStack nitrogen = getFluidOrGas(Materials.Nitrogen, 1000L);
        ItemStack charcoal = Materials.Charcoal.getGems(20);
        FluidStack woodVinegar = Materials.WoodVinegar.getFluid(3000L);

        if (logs == null || logs.getItem() == null
            || nitrogen == null
            || charcoal == null
            || charcoal.getItem() == null
            || woodVinegar == null) {
            MyMod.logInfo("Skipped Industrial Coke Oven Wood Vinegar recipe: required item or fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(logs)
            .itemOutputs(charcoal)
            .fluidInputs(nitrogen)
            .fluidOutputs(woodVinegar)
            .duration(240)
            .eut(120)
            .addTo(GTPPRecipeMaps.cokeOvenRecipes);

        MyMod.logInfo(
            "Registered Industrial Coke Oven recipe: 16x Log + 1000L Nitrogen -> 20x Charcoal + 3000L Wood Vinegar.");
    }

    private static void registerCrudeBioTarDistillationRecipe() {
        FluidStack crudeBioTar = ModFluids.getBioTar(1000);
        FluidStack anthracene = GTPPFluids.Anthracene == null ? getFirstAvailableFluid(300, "anthracene", "Anthracene")
            : new FluidStack(GTPPFluids.Anthracene, 300);
        FluidStack naphthalene = GTPPFluids.Naphthalene == null
            ? getFirstAvailableFluid(150, "naphthalene", "Naphthalene")
            : new FluidStack(GTPPFluids.Naphthalene, 150);
        FluidStack heavyFuel = Materials.HeavyFuel.getFluid(400L);

        if (crudeBioTar == null || anthracene == null || naphthalene == null || heavyFuel == null) {
            MyMod.logInfo("Skipped Distillation Tower Crude Bio-tar recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(3))
            .fluidInputs(crudeBioTar)
            .fluidOutputs(anthracene, naphthalene, heavyFuel)
            .duration(CRUDE_BIO_TAR_BASE_DISTILLATION_DURATION)
            .eut(120)
            .addTo(RecipeMaps.distillationTowerRecipes);

        MyMod.logInfo(
            "Registered Distillation Tower recipe: IC-3 + 1000L Crude Bio-tar -> 300L Anthracene + 150L Naphthalene + 400L Heavy Fuel.");
    }

    private static void registerCrudeBioTarMiddleFractionDistillationRecipe() {
        FluidStack crudeBioTar = ModFluids.getBioTar(1000);
        FluidStack kerosene = GTPPFluids.Kerosene == null ? getFirstAvailableFluid(320, "kerosene", "Kerosene")
            : new FluidStack(GTPPFluids.Kerosene, 320);
        FluidStack naphthenicAcid = MaterialsKevlar.NaphthenicAcid == null
            ? getFirstAvailableFluid(220, "naphthenicacid", "naphthenic_acid", "NaphthenicAcid", "Naphthenic Acid")
            : MaterialsKevlar.NaphthenicAcid.getFluid(220);
        FluidStack phenol = Materials.Phenol.getFluid(160L);
        FluidStack toluene = Materials.Toluene.getFluid(110L);
        FluidStack benzene = Materials.Benzene.getFluid(90L);

        if (crudeBioTar == null || kerosene == null
            || naphthenicAcid == null
            || phenol == null
            || toluene == null
            || benzene == null) {
            MyMod.logInfo(
                "Skipped Distillation Tower middle-fraction Crude Bio-tar recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(crudeBioTar)
            .fluidOutputs(kerosene, naphthenicAcid, phenol, toluene, benzene)
            .duration(CRUDE_BIO_TAR_MIDDLE_DISTILLATION_DURATION)
            .eut(120)
            .addTo(RecipeMaps.distillationTowerRecipes);

        MyMod.logInfo(
            "Registered Distillation Tower recipe: IC-2 + 1000L Crude Bio-tar -> 320L Kerosene + 220L Naphthenic Acid + 160L Phenol + 110L Toluene + 90L Benzene.");
    }

    private static void registerCrudeBioTarLightFractionDistillationRecipe() {
        FluidStack crudeBioTar = ModFluids.getBioTar(1000);
        FluidStack biogas = getFirstAvailableFluid(400, "ic2biogas", "biogas", "Biogas", "BioGas");
        FluidStack water = getFluidOrGas(Materials.Water, 100L);
        FluidStack woodVinegar = Materials.WoodVinegar.getFluid(200L);
        FluidStack lightFuel = Materials.LightFuel.getFluid(130L);
        FluidStack acetone = Materials.Acetone.getFluid(70L);
        FluidStack aceticAcid = Materials.AceticAcid.getFluid(50L);

        if (crudeBioTar == null || biogas == null
            || water == null
            || woodVinegar == null
            || lightFuel == null
            || acetone == null
            || aceticAcid == null) {
            MyMod.logInfo(
                "Skipped Distillation Tower light-fraction Crude Bio-tar recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(crudeBioTar)
            .fluidOutputs(biogas, water, woodVinegar, lightFuel, acetone, aceticAcid)
            .duration(CRUDE_BIO_TAR_LIGHT_DISTILLATION_DURATION)
            .eut(120)
            .addTo(RecipeMaps.distillationTowerRecipes);

        MyMod.logInfo(
            "Registered Distillation Tower recipe: IC-1 + 1000L Crude Bio-tar -> 400L Biogas + 100L Water + 200L Wood Vinegar + 130L Light Fuel + 70L Acetone + 50L Acetic Acid.");
    }

    private static void registerWoodVinegarCalciumAcetateRecipe() {
        // Real "gray acetate of lime" process: crude pyroligneous acid (wood vinegar) neutralized with
        // quicklime, CaO + 2 CH3COOH -> Ca(CH3COO)2 + H2O. Same reaction vanilla already registers from
        // pure Acetic Acid; wood vinegar is dilute, so it needs more volume for the same acid content.
        ItemStack quicklime = Materials.Quicklime.getDust(2);
        FluidStack woodVinegar = Materials.WoodVinegar.getFluid(3000L);
        FluidStack calciumAcetateSolution = Materials.CalciumAcetateSolution.getFluid(1000L);

        if (quicklime == null || quicklime.getItem() == null || woodVinegar == null || calciumAcetateSolution == null) {
            MyMod.logInfo("Skipped Wood Vinegar Calcium Acetate recipe: required item or fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(quicklime)
            .fluidInputs(woodVinegar)
            .fluidOutputs(calciumAcetateSolution)
            .duration(4 * GTRecipeBuilder.SECONDS)
            .eut(16)
            .addTo(RecipeMaps.mixerRecipes);

        MyMod.logInfo(
            "Registered Mixer recipe: 2x Quicklime dust + 3000L Wood Vinegar -> 1000L Calcium Acetate Solution.");
    }

    private static void registerNaphthaToNaphthaleneRecipe() {
        FluidStack naphtha = Materials.Naphtha.getFluid(1000L);
        ItemStack platinumCatalyst = GTUtility.copyAmount(0, Materials.Platinum.getDust(1));
        FluidStack naphthalene = GTPPFluids.Naphthalene == null
            ? getFirstAvailableFluid(400, "naphthalene", "Naphthalene")
            : new FluidStack(GTPPFluids.Naphthalene, 400);
        FluidStack hydrogen = getFluidOrGas(Materials.Hydrogen, 300L);
        FluidStack methane = getFluidOrGas(Materials.Methane, 200L);

        if (naphtha == null || platinumCatalyst == null
            || platinumCatalyst.getItem() == null
            || naphthalene == null
            || hydrogen == null
            || methane == null) {
            MyMod.logInfo("Skipped LCR Naphtha -> Naphthalene recipe: required catalyst or fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(platinumCatalyst)
            .fluidInputs(naphtha)
            .fluidOutputs(naphthalene, hydrogen, methane)
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(2048)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered LCR recipe: 1000L Naphtha + Platinum Dust catalyst -> 400L Naphthalene + 300L Hydrogen + 200L Methane.");
    }

    private static void registerXyleneHydrodealkylationRecipes() {
        registerXyleneHydrodealkylationRecipe(
            getFluidOrGas(Materials.Dimethylbenzene, 1000L),
            "1,2-Dimethylbenzene",
            11);
        registerXyleneHydrodealkylationRecipe(
            getFluidOrGas(MaterialsKevlar.IIIDimethylbenzene, 1000L),
            "1,3-Dimethylbenzene",
            13);
    }

    private static void registerXyleneHydrodealkylationRecipe(FluidStack xylene, String xyleneName, int circuit) {
        FluidStack hydrogen = getFluidOrGas(Materials.Hydrogen, 4000L);
        FluidStack benzene = getFluidOrGas(Materials.Benzene, 1000L);
        FluidStack methane = getFluidOrGas(Materials.Methane, 2000L);
        ItemStack chromeCatalyst = GTUtility.copyAmount(0, Materials.Chrome.getDust(1));

        if (xylene == null || hydrogen == null
            || benzene == null
            || methane == null
            || chromeCatalyst == null
            || chromeCatalyst.getItem() == null) {
            MyMod.logInfo(
                "Skipped " + xyleneName + " hydrodealkylation recipe: required catalyst or fluids unavailable.");
            return;
        }

        // Hydrodealkylation: C8H10 + 2 H2 -> C6H6 + 2 CH4 (Cr2O3 catalyst, not consumed)
        GTValues.RA.stdBuilder()
            .itemInputs(chromeCatalyst, GTUtility.getIntegratedCircuit(circuit))
            .fluidInputs(xylene, hydrogen)
            .fluidOutputs(benzene, methane)
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered LCR recipe: 1000L " + xyleneName
                + " + 4000L Hydrogen + Chrome Dust catalyst -> 1000L Benzene + 2000L Methane.");
    }

    private static void registerCarbonDisulfideAlternativeRecipes() {
        registerCarbonDisulfideMethaneSulfurRecipe();
        registerCarbonDisulfideHydrogenSulfideCarbonRecipe();
        registerCarbonylSulfideChainRecipes();
        registerCarbonDisulfideCharcoalBlastFurnaceRecipe();
    }

    private static void registerCarbonDisulfideMethaneSulfurRecipe() {
        FluidStack methane = getFluidOrGas(Materials.Methane, 1000L);
        FluidStack carbonDisulfide = getCarbonDisulfide(1000);
        FluidStack hydrogenSulfide = getFluidOrGas(Materials.HydricSulfide, 2000L);
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
        GTValues.RA.stdBuilder()
            .itemInputs(sulfurDust, silicaCatalyst, GTUtility.getIntegratedCircuit(4))
            .fluidInputs(methane)
            .fluidOutputs(carbonDisulfide, hydrogenSulfide)
            .duration(15 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered LCR recipe: IC-4 + 1000L Methane + 4x Sulfur Dust + Silicon Dioxide catalyst -> 1000L Carbon Disulfide + 2000L Hydrogen Sulfide.");
    }

    private static void registerCarbonDisulfideHydrogenSulfideCarbonRecipe() {
        FluidStack hydrogenSulfide = getFluidOrGas(Materials.HydricSulfide, 2000L);
        FluidStack carbonDisulfide = getCarbonDisulfide(1000);
        FluidStack hydrogen = getFluidOrGas(Materials.Hydrogen, 2000L);
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
        GTValues.RA.stdBuilder()
            .itemInputs(carbonDust, GTUtility.getIntegratedCircuit(5))
            .fluidInputs(hydrogenSulfide)
            .fluidOutputs(carbonDisulfide, hydrogen)
            .duration(20 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered LCR recipe: IC-5 + 2000L Hydrogen Sulfide + 1x Carbon Dust -> 1000L Carbon Disulfide + 2000L Hydrogen.");
    }

    private static void registerCarbonylSulfideChainRecipes() {
        FluidStack carbonMonoxide = getFluidOrGas(Materials.CarbonMonoxide, 1000L);
        FluidStack carbonylSulfide = ModFluids.getCarbonylSulfide(1000);
        FluidStack carbonylSulfideInput = ModFluids.getCarbonylSulfide(2000);
        FluidStack carbonDisulfide = getCarbonDisulfide(1000);
        FluidStack carbonDioxide = getFluidOrGas(Materials.CarbonDioxide, 1000L);
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
        GTValues.RA.stdBuilder()
            .itemInputs(sulfurDust, GTUtility.getIntegratedCircuit(6))
            .fluidInputs(carbonMonoxide)
            .fluidOutputs(carbonylSulfide)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1L),
                GTUtility.getIntegratedCircuit(6))
            .fluidInputs(getFluidOrGas(Materials.CarbonMonoxide, 1000L))
            .fluidOutputs(ModFluids.getCarbonylSulfide(1000))
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        // Step 2: 2 COS -> CS2 + CO2 (disproportionation over alumina, not consumed)
        GTValues.RA.stdBuilder()
            .itemInputs(aluminaCatalyst, GTUtility.getIntegratedCircuit(7))
            .fluidInputs(carbonylSulfideInput)
            .fluidOutputs(carbonDisulfide, carbonDioxide)
            .duration(16 * GTRecipeBuilder.SECONDS)
            .eut(240)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered Carbonyl Sulfide chain: IC-6 CO + Sulfur -> COS (CR/LCR); IC-7 2000L COS + Alumina catalyst -> 1000L Carbon Disulfide + 1000L Carbon Dioxide (LCR).");
    }

    private static void registerCalciumCyanamideRecipes() {
        // Frank-Caro nitrogen-fixation process, step 1: CaC2 + N2 -> CaCN2 + C, needs the same
        // ~1000-1100C furnace tier that forms Calcium Carbide itself.
        ItemStack calciumCarbide = MaterialsKevlar.CalciumCarbide.getDust(3);
        FluidStack nitrogenForCyanamide = getFluidOrGas(Materials.Nitrogen, 1000L);
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
        FluidStack waterForHydrolysis = getFluidOrGas(Materials.Water, 1500L);
        ItemStack calcite = Materials.Calcite.getDust(5);
        FluidStack ammonia = Materials.Ammonia.getGas(2000L);

        if (calciumCyanamideInput == null || waterForHydrolysis == null
            || calcite == null
            || calcite.getItem() == null
            || ammonia == null) {
            MyMod.logInfo("Skipped Calcium Cyanamide hydrolysis recipe: required item or fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemOutputs(calcite)
            .fluidInputs(calciumCyanamideInput, waterForHydrolysis)
            .fluidOutputs(ammonia)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(30)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered LCR recipe: 1000L Calcium Cyanamide + 1500L Water -> 2000L Ammonia + 5x Calcite Dust.");
    }

    private static void registerCalciumHypochloriteRecipe() {
        // Bleaching powder: 2 Ca(OH)2 + 2 Cl2 -> Ca(ClO)2 + CaCl2 + 2 H2O.
        Item hydratedLimeItem = gtPlusPlus.core.item.ModItems.dustCalciumHydroxide;
        if (hydratedLimeItem == null) {
            MyMod.logInfo("Skipped Calcium Hypochlorite recipe: Hydrated Lime Dust is missing.");
            return;
        }
        ItemStack hydratedLime = new ItemStack(hydratedLimeItem, 2);
        FluidStack chlorine = getFluidOrGas(Materials.Chlorine, 2000L);
        FluidStack calciumHypochlorite = ModFluids.getCalciumHypochlorite(1000);
        ItemStack calciumChloride = bartworks.system.material.WerkstoffLoader.CalciumChloride.get(OrePrefixes.dust, 3);
        FluidStack water = getFluidOrGas(Materials.Water, 1000L);

        if (chlorine == null || calciumHypochlorite == null
            || calciumChloride == null
            || calciumChloride.getItem() == null
            || water == null) {
            MyMod.logInfo("Skipped Calcium Hypochlorite recipe: required item or fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(hydratedLime)
            .itemOutputs(calciumChloride)
            .fluidInputs(chlorine)
            .fluidOutputs(calciumHypochlorite, water)
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(30)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered LCR recipe: 2x Hydrated Lime Dust + 2000L Chlorine -> 1000L Calcium Hypochlorite + 1000L Water + 3x Calcium Chloride Dust.");
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
        return getFirstAvailableFluid(amount, "carbondisulfide", "CarbonDisulfide", "carbon_disulfide");
    }

    private static FluidStack getCoalGas(int amount) {
        if (GTPPFluids.CoalGas != null) {
            return new FluidStack(GTPPFluids.CoalGas, amount);
        }
        return getFirstAvailableFluid(amount, "coalgas", "CoalGas", "coal_gas");
    }

    private static void registerCoalGasWaterGasShiftRecipe() {
        FluidStack coalGas = getCoalGas(1000);
        FluidStack steam = getFluidOrGas(Materials.Steam, 1000L);
        FluidStack hydrogen = getFluidOrGas(Materials.Hydrogen, 2000L);
        FluidStack carbonDioxide = getFluidOrGas(Materials.CarbonDioxide, 1000L);
        ItemStack hematiteCatalyst = GTUtility.copyAmount(0, Materials.Hematite.getDust(1));

        if (coalGas == null || steam == null
            || hydrogen == null
            || carbonDioxide == null
            || hematiteCatalyst == null
            || hematiteCatalyst.getItem() == null) {
            MyMod.logInfo("Skipped Coal Gas water-gas shift recipe: required catalyst or fluids unavailable.");
            return;
        }

        // Water-gas shift: CO (from Coal Gas) + H2O -> CO2 + H2, over Fe2O3 high-temperature shift
        // catalyst (not consumed) - real industrial route to enrich Hydrogen from Coal Gas.
        GTValues.RA.stdBuilder()
            .itemInputs(hematiteCatalyst, GTUtility.getIntegratedCircuit(9))
            .fluidInputs(coalGas, steam)
            .fluidOutputs(hydrogen, carbonDioxide)
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered LCR recipe: IC-9 + 1000L Coal Gas + 1000L Steam + Hematite catalyst -> 2000L Hydrogen + 1000L Carbon Dioxide.");
    }

    private static void registerCoalGasBoudouardCarbonDepositionRecipe() {
        FluidStack coalGas = getCoalGas(2000);
        FluidStack carbonDioxide = getFluidOrGas(Materials.CarbonDioxide, 1000L);
        ItemStack coalLump = Materials.Coal.getGems(1);
        ItemStack ironCatalyst = GTUtility.copyAmount(0, Materials.Iron.getDust(1));

        if (coalGas == null || carbonDioxide == null
            || coalLump == null
            || coalLump.getItem() == null
            || ironCatalyst == null
            || ironCatalyst.getItem() == null) {
            MyMod.logInfo(
                "Skipped Coal Gas Boudouard carbon deposition recipe: required catalyst or fluids unavailable.");
            return;
        }

        // Boudouard reaction: 2 CO -> C(s) + CO2, over an Iron catalyst (not consumed). Exploits
        // the CO fraction of Coal Gas to deposit solid carbon - real industrial "coking out" side
        // reaction, run here on-purpose to reclaim Coal out of Coal Gas.
        GTValues.RA.stdBuilder()
            .itemInputs(ironCatalyst)
            .itemOutputs(coalLump)
            .fluidInputs(coalGas)
            .fluidOutputs(carbonDioxide)
            .duration(16 * GTRecipeBuilder.SECONDS)
            .eut(60)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod
            .logInfo("Registered LCR recipe: 2000L Coal Gas + Iron catalyst -> 1x Coal + 1000L Carbon Dioxide.");
    }

    private static void registerPropeneHydrogenationPropaneRecipe() {
        FluidStack propene = getFluidOrGas(Materials.Propene, 1000L);
        FluidStack hydrogen = getFluidOrGas(Materials.Hydrogen, 1000L);
        FluidStack propane = getFluidOrGas(Materials.Propane, 1000L);
        ItemStack nickelCatalyst = GTUtility.copyAmount(0, Materials.Nickel.getDust(1));

        if (propene == null || hydrogen == null
            || propane == null
            || nickelCatalyst == null
            || nickelCatalyst.getItem() == null) {
            MyMod.logInfo("Skipped Propene hydrogenation -> Propane recipe: required catalyst or fluids unavailable.");
            return;
        }

        // Catalytic hydrogenation: C3H6 + H2 -> C3H8, over a Nickel catalyst (not consumed).
        // Real industrial route to synthesize Propane, alternative to cracking it out of oil/LPG.
        GTValues.RA.stdBuilder()
            .itemInputs(nickelCatalyst, GTUtility.getIntegratedCircuit(3))
            .fluidInputs(propene, hydrogen)
            .fluidOutputs(propane)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(30)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        MyMod.logInfo(
            "Registered Chemical Reactor recipe: 1000L Propene + 1000L Hydrogen + Nickel catalyst -> 1000L Propane.");
    }

    private static void registerPropaneDehydrogenationPropeneRecipe() {
        FluidStack propane = getFluidOrGas(Materials.Propane, 1000L);
        FluidStack propene = getFluidOrGas(Materials.Propene, 1000L);
        FluidStack hydrogen = getFluidOrGas(Materials.Hydrogen, 1000L);
        ItemStack platinumCatalyst = GTUtility.copyAmount(0, Materials.Platinum.getDust(1));

        if (propane == null || propene == null
            || hydrogen == null
            || platinumCatalyst == null
            || platinumCatalyst.getItem() == null) {
            MyMod
                .logInfo("Skipped Propane dehydrogenation -> Propene recipe: required catalyst or fluids unavailable.");
            return;
        }

        // Catalytic dehydrogenation (PDH process): C3H8 -> C3H6 + H2, over a Platinum catalyst
        // (not consumed). Endothermic, real on-purpose industrial route to Propene (UOP Oleflex-style),
        // the mirror reaction of the Propene hydrogenation recipe above.
        GTValues.RA.stdBuilder()
            .itemInputs(platinumCatalyst, GTUtility.getIntegratedCircuit(4))
            .fluidInputs(propane)
            .fluidOutputs(propene, hydrogen)
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        MyMod.logInfo(
            "Registered Chemical Reactor recipe: 1000L Propane + Platinum catalyst -> 1000L Propene + 1000L Hydrogen.");
    }

    private static void registerRadioactiveWasteNoveltyRecipes() {
        registerRadioactiveWasteFireworkStarRecipe();
        registerRadioactiveWasteLimeDyeRecipe();
    }

    private static void registerRadioactiveWasteFireworkStarRecipe() {
        ItemStack radioactiveWaste = new ItemStack(Loaders.radioactiveWaste, 1);
        ItemStack gunpowder = new ItemStack(Items.gunpowder, 2);

        if (radioactiveWaste.getItem() == null || gunpowder.getItem() == null) {
            MyMod.logInfo("Skipped Radioactive Waste -> Firework Star recipe: required items unavailable.");
            return;
        }

        // Novelty item, not real chemistry: a glowing, twinkling green firework star. No stat value,
        // just a "Chernobyl fireworks" meme sink for the Neutron Activator's accidental byproduct.
        NBTTagCompound explosion = new NBTTagCompound();
        explosion.setByte("Type", (byte) 0);
        explosion.setIntArray("Colors", new int[] { 0x39FF14 });
        explosion.setBoolean("Flicker", true);
        explosion.setBoolean("Trail", true);
        NBTTagCompound fireworkTag = new NBTTagCompound();
        fireworkTag.setTag("Explosion", explosion);
        ItemStack radioactiveFireworkStar = new ItemStack(Items.firework_charge, 1);
        radioactiveFireworkStar.setTagCompound(fireworkTag);

        GTValues.RA.stdBuilder()
            .itemInputs(radioactiveWaste, gunpowder)
            .itemOutputs(radioactiveFireworkStar)
            .duration(5 * GTRecipeBuilder.SECONDS)
            .eut(16)
            .addTo(RecipeMaps.assemblerRecipes);

        MyMod.logInfo(
            "Registered Assembler recipe: 1x Radioactive Waste + 2x Gunpowder -> 1x glowing green Firework Star (novelty, no stats).");
    }

    private static void registerRadioactiveWasteLimeDyeRecipe() {
        ItemStack radioactiveWaste = new ItemStack(Loaders.radioactiveWaste, 1);
        ItemStack boneMeal = new ItemStack(Items.dye, 1, 15);
        ItemStack limeDye = new ItemStack(Items.dye, 2, 10);

        if (radioactiveWaste.getItem() == null || boneMeal.getItem() == null || limeDye.getItem() == null) {
            MyMod.logInfo("Skipped Radioactive Waste -> Lime Dye recipe: required items unavailable.");
            return;
        }

        // Novelty item, not real chemistry: irradiated Bone Meal bleaches into a sickly glow-green
        // dye. Cosmetic only - tints wool/glass, "old radium paint" meme.
        GTValues.RA.stdBuilder()
            .itemInputs(radioactiveWaste, boneMeal)
            .itemOutputs(limeDye)
            .duration(5 * GTRecipeBuilder.SECONDS)
            .eut(16)
            .addTo(RecipeMaps.mixerRecipes);

        MyMod.logInfo(
            "Registered Mixer recipe: 1x Radioactive Waste + 1x Bone Meal -> 2x Lime Dye (novelty, cosmetic only).");
    }

    private static void registerCelluloseFiberBiomassRecipe() {
        ItemStack celluloseFiber = GregtechItemList.CelluloseFiber.get(2L, new Object[0]);
        FluidStack water = getFluidOrGas(Materials.Water, 1000L);
        FluidStack biomass = getFirstAvailableFluid(500, "ic2biomass", "biomass", "Biomass");

        if (celluloseFiber == null || celluloseFiber.getItem() == null || water == null || biomass == null) {
            MyMod.logInfo("Skipped Cellulose Fiber -> Biomass brewery recipe: required item or fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(celluloseFiber)
            .fluidInputs(water)
            .fluidOutputs(biomass)
            .duration(170)
            .eut(4)
            .addTo(RecipeMaps.brewingRecipes);

        MyMod.logInfo("Registered Brewery recipe: 2x Cellulose Fiber + 1000L Water -> 500L Biomass.");
    }

    private static void registerGlycerolNitrationRecipe() {
        FluidStack glycerol = getFluidOrGas(Materials.Glycerol, 500L);
        FluidStack nitrogenDioxide = getFluidOrGas(Materials.NitrogenDioxide, 500L);
        FluidStack glycerylTrinitrate = getFluidOrGas(Materials.Glyceryl, 750L);

        if (glycerol == null || nitrogenDioxide == null || glycerylTrinitrate == null) {
            MyMod.logInfo("Skipped Glycerol nitration recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(glycerol, nitrogenDioxide)
            .fluidOutputs(glycerylTrinitrate)
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(getFluidOrGas(Materials.Glycerol, 500L), getFluidOrGas(Materials.NitrogenDioxide, 500L))
            .fluidOutputs(getFluidOrGas(Materials.Glyceryl, 750L))
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered Chemical Reactor and LCR recipe: IC-1 + 500L Glycerol + 500L Nitrogen Dioxide -> 750L Glyceryl Trinitrate.");
    }

    private static void registerGlycerolHydrogenCrackingRecipe() {
        FluidStack glycerol = getFluidOrGas(Materials.Glycerol, 1000L);
        FluidStack hydrogen = getFluidOrGas(Materials.Hydrogen, 500L);
        FluidStack methane = getFluidOrGas(Materials.Methane, 600L);
        FluidStack water = getFluidOrGas(Materials.Water, 400L);

        if (glycerol == null || hydrogen == null || methane == null || water == null) {
            MyMod.logInfo("Skipped Glycerol cracking recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(glycerol, hydrogen)
            .fluidOutputs(methane, water)
            .duration(16 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo("Registered LCR recipe: IC-2 + 1000L Glycerol + 500L Hydrogen -> 600L Methane + 400L Water.");
    }

    private static void registerGlycerolFermentationRecipe() {
        FluidStack glycerol = getFluidOrGas(Materials.Glycerol, 1000L);
        FluidStack vinegar = getFluidOrGas(Materials.Vinegar, 700L);

        if (glycerol == null || vinegar == null) {
            MyMod.logInfo("Skipped Glycerol fermentation recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(glycerol)
            .fluidOutputs(vinegar)
            .duration(30 * GTRecipeBuilder.SECONDS)
            .eut(30)
            .addTo(RecipeMaps.fermentingRecipes);

        MyMod.logInfo("Registered Fermenter recipe: IC-1 + 1000L Glycerol -> 700L Vinegar.");
    }

    private static void registerPhenolFormaldehydeResinRecipe() {
        FluidStack phenol = getFluidOrGas(Materials.Phenol, 500L);
        FluidStack formaldehyde = GTPPFluids.Formaldehyde == null
            ? getFirstAvailableFluid(500, "formaldehyde", "Formaldehyde")
            : new FluidStack(GTPPFluids.Formaldehyde, 500);
        FluidStack liquidResin = GTPPFluids.LiquidResin == null
            ? getFirstAvailableFluid(750, "liquidresin", "Liquid Resin")
            : new FluidStack(GTPPFluids.LiquidResin, 750);

        if (phenol == null || formaldehyde == null || liquidResin == null) {
            MyMod.logInfo("Skipped Phenol + Formaldehyde -> Liquid Resin recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(phenol, formaldehyde)
            .fluidOutputs(liquidResin)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        MyMod.logInfo(
            "Registered Chemical Reactor recipe: IC-1 + 500L Phenol + 500L Formaldehyde -> 750L Liquid Resin.");
    }

    private static void registerPhenolHydrogenToCyclohexaneRecipe() {
        FluidStack phenol = getFluidOrGas(Materials.Phenol, 1000L);
        FluidStack hydrogen = getFluidOrGas(Materials.Hydrogen, 500L);
        FluidStack cyclohexane = GTPPFluids.Cyclohexane == null
            ? getFirstAvailableFluid(850, "cyclohexane", "Cyclohexane")
            : new FluidStack(GTPPFluids.Cyclohexane, 850);

        if (phenol == null || hydrogen == null || cyclohexane == null) {
            MyMod.logInfo("Skipped Phenol + Hydrogen -> Cyclohexane recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(phenol, hydrogen)
            .fluidOutputs(cyclohexane)
            .duration(15 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        MyMod.logInfo("Registered Chemical Reactor recipe: IC-2 + 1000L Phenol + 500L Hydrogen -> 850L Cyclohexane.");
    }

    private static void registerPhenolHydrogenToBenzeneRecipe() {
        // Catalytic hydrodeoxygenation: C6H5OH + H2 -> C6H6 + H2O (Pd catalyst, not consumed)
        FluidStack phenol = getFluidOrGas(Materials.Phenol, 1000L);
        FluidStack hydrogen = getFluidOrGas(Materials.Hydrogen, 500L);
        FluidStack water = getFluidOrGas(Materials.Water, 200L);
        FluidStack benzene = getFluidOrGas(Materials.Benzene, 800L);
        ItemStack palladiumCatalyst = GTUtility.copyAmount(0, Materials.Palladium.getDust(1));

        if (phenol == null || hydrogen == null
            || water == null
            || benzene == null
            || palladiumCatalyst == null
            || palladiumCatalyst.getItem() == null) {
            MyMod.logInfo("Skipped Phenol + Hydrogen -> Benzene recipe: required catalyst or fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(palladiumCatalyst, GTUtility.getIntegratedCircuit(2))
            .fluidInputs(phenol, hydrogen)
            .fluidOutputs(benzene, water)
            .duration(15 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered LCR recipe: IC-2 + Palladium Dust catalyst + 1000L Phenol + 500L Hydrogen -> 800L Benzene + 200L Water.");
    }

    private static void registerPhenolNitrationRecipe() {
        FluidStack phenol = getFluidOrGas(Materials.Phenol, 500L);
        FluidStack nitrationMixture = getFluidOrGas(Materials.NitrationMixture, 500L);
        FluidStack nitrobenzene = GTPPFluids.Nitrobenzene == null
            ? getFirstAvailableFluid(750, "nitrobenzene", "Nitrobenzene")
            : new FluidStack(GTPPFluids.Nitrobenzene, 750);

        if (phenol == null || nitrationMixture == null || nitrobenzene == null) {
            MyMod.logInfo("Skipped Phenol + Nitration Mixture -> Nitrobenzene recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(phenol, nitrationMixture)
            .fluidOutputs(nitrobenzene)
            .duration(20 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo("Registered LCR recipe: IC-1 + 500L Phenol + 500L Nitration Mixture -> 750L Nitrobenzene.");
    }

    private static void registerKeroseneHydrocrackingRecipe() {
        FluidStack kerosene = GTPPFluids.Kerosene == null ? getFirstAvailableFluid(1000, "kerosene", "Kerosene")
            : new FluidStack(GTPPFluids.Kerosene, 1000);
        FluidStack hydrogen = getFluidOrGas(Materials.Hydrogen, 300L);
        FluidStack lightFuel = getFluidOrGas(Materials.LightFuel, 700L);
        FluidStack methane = getFluidOrGas(Materials.Methane, 400L);

        if (kerosene == null || hydrogen == null || lightFuel == null || methane == null) {
            MyMod.logInfo("Skipped Kerosene hydrocracking recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(kerosene, hydrogen)
            .fluidOutputs(lightFuel, methane)
            .duration(20 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod
            .logInfo("Registered LCR recipe: IC-2 + 1000L Kerosene + 300L Hydrogen -> 700L Light Fuel + 400L Methane.");
    }

    private static void registerKeroseneSulfuricLightFuelRecipe() {
        FluidStack kerosene = GTPPFluids.Kerosene == null ? getFirstAvailableFluid(1000, "kerosene", "Kerosene")
            : new FluidStack(GTPPFluids.Kerosene, 1000);
        FluidStack sulfuricAcid = getFluidOrGas(Materials.SulfuricAcid, 100L);
        FluidStack sulfuricLightFuel = getFluidOrGas(Materials.SulfuricLightFuel, 900L);

        if (kerosene == null || sulfuricAcid == null || sulfuricLightFuel == null) {
            MyMod.logInfo(
                "Skipped Kerosene + Sulfuric Acid -> Sulfuric Light Fuel recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(3))
            .fluidInputs(kerosene, sulfuricAcid)
            .fluidOutputs(sulfuricLightFuel)
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        MyMod.logInfo(
            "Registered Chemical Reactor recipe: IC-3 + 1000L Kerosene + 100L Sulfuric Acid -> 900L Sulfuric Light Fuel.");
    }

    private static void registerJetFuelRocketFuelRecipe() {
        FluidStack jetFuel = getFirstAvailableFluid(
            1000,
            "jetfuela",
            "jetfuel_a",
            "jet_fuel_a",
            "jetfuel",
            "jet_fuel",
            "jet fuel a",
            "jet fuel",
            "JetFuelA",
            "JetFuel",
            "Jet Fuel A",
            "Jet Fuel");

        if (jetFuel == null) {
            MyMod.logInfo("Skipped Jet Fuel A rocket fuel recipe: Jet Fuel A fluid is unavailable.");
            return;
        }

        if (hasRocketFuelRecipe(jetFuel)) {
            MyMod.logInfo("Skipped Jet Fuel A rocket fuel recipe: fuel already registered.");
            return;
        }

        addRocketFuelRecipe(jetFuel, 512);
        MyMod.logInfo("Registered rocket fuel: 1000L Jet Fuel A -> rocket fuel value 512.");
    }

    private static void registerAcetaldehydeHydrogenationRecipe() {
        FluidStack acetaldehyde = getFirstAvailableFluid(1000, "acetaldehyde", "Acetaldehyde");
        FluidStack hydrogen = Materials.Hydrogen.getGas(1000L);
        if (hydrogen == null) {
            hydrogen = Materials.Hydrogen.getFluid(1000L);
        }
        FluidStack ethanol = getFirstAvailableFluid(1000, "ethanol", "Ethanol");
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

    private static void registerMethanolCarbonMonoxideHydrogenToEthanolRecipe() {
        FluidStack methanol = getFluidOrGas(Materials.Methanol, 1000L);
        FluidStack carbonMonoxide = getFluidOrGas(Materials.CarbonMonoxide, 1000L);
        FluidStack hydrogen = getFluidOrGas(Materials.Hydrogen, 1000L);
        FluidStack ethanol = getFluidOrGas(Materials.Ethanol, 1000L);
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
                getFluidOrGas(Materials.Methanol, 9000L),
                getFluidOrGas(Materials.CarbonMonoxide, 9000L),
                getFluidOrGas(Materials.Hydrogen, 9000L))
            .fluidOutputs(getFluidOrGas(Materials.Ethanol, 9000L))
            .duration(90 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered LCR recipes: Cobalt Dust catalyst + IC-1 1000L Methanol + 1000L CO + 1000L H2 -> 1000L Ethanol; IC-24 9x batch.");
    }

    private static void registerEthyleneDirectHydrationEthanolRecipe() {
        // C2H4 + H2O -> C2H6O (acid-catalyzed direct hydration, industrial ethanol synthesis route)
        FluidStack ethylene = getFluidOrGas(Materials.Ethylene, 1000L);
        FluidStack steam = getFluidOrGas(Materials.Steam, 1000L);
        FluidStack ethanol = getFluidOrGas(Materials.Ethanol, 1000L);
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

    private static void registerSyngasFischerTropschEthanolRecipe() {
        // 2 CO + 4 H2 -> C2H6O + H2O (Fischer-Tropsch style syngas-to-ethanol synthesis)
        FluidStack carbonMonoxide = getFluidOrGas(Materials.CarbonMonoxide, 2000L);
        FluidStack hydrogen = getFluidOrGas(Materials.Hydrogen, 4000L);
        FluidStack ethanol = getFluidOrGas(Materials.Ethanol, 1000L);
        FluidStack water = getFluidOrGas(Materials.Water, 1000L);
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

    private static void registerMethaneToAcetyleneDehydratorRecipe() {
        FluidStack methane = getFluidOrGas(Materials.Methane, 2000L);
        FluidStack acetylene = getFirstAvailableFluid(1000, "acetylene", "Acetylene");
        FluidStack hydrogen = getFluidOrGas(Materials.Hydrogen, 3000L);

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

    private static void registerSolderingAlloyIronAntimonyRecipe() {
        ItemStack ironIngots = GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Iron, 7L);
        ItemStack antimonyIngots = GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Antimony, 3L);
        ItemStack solderingAlloyIngots = GTOreDictUnificator.get(OrePrefixes.ingot, Materials.SolderingAlloy, 9L);

        if (ironIngots == null || antimonyIngots == null || solderingAlloyIngots == null) {
            MyMod.logInfo(
                "Skipped Alloy Smelter soldering alloy recipe: iron, antimony, or soldering alloy ingot stack is unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(ironIngots, antimonyIngots)
            .itemOutputs(solderingAlloyIngots)
            .duration(16 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.alloySmelterRecipes);

        MyMod
            .logInfo("Registered Alloy Smelter recipe: 7x Iron Ingot + 3x Antimony Ingot -> 9x Soldering Alloy Ingot.");
    }

    private static void registerSuperFuelBinderBeeswaxRecipes() {
        ItemStack beeswax = resolveForestryBeeswax(8);
        FluidStack advancedGlue = Materials.AdvancedGlue.getFluid(100L);

        if (beeswax == null || advancedGlue == null) {
            MyMod.logInfo(
                "Skipped beeswax Super Fuel Binder recipes: Forestry beeswax or Advanced Glue is unavailable.");
            return;
        }

        registerSuperFuelBinderBeeswaxRecipe(beeswax, Materials.Sodium, 4);
        registerSuperFuelBinderBeeswaxRecipe(beeswax, Materials.Lithium, 8);
        registerSuperFuelBinderBeeswaxRecipe(beeswax, Materials.Caesium, 12);

        MyMod.logInfo(
            "Registered Mixer recipes: 8x Forestry Beeswax + 100L Advanced Glue alternate Super Fuel Binder route.");
    }

    private static void registerSuperFuelBinderBeeswaxRecipe(ItemStack beeswax, Materials metal, int outputAmount) {
        ItemStack sulfurDust = GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1L);
        ItemStack metalDust = GTOreDictUnificator.get(OrePrefixes.dust, metal, 1L);
        ItemStack woodDust = GTOreDictUnificator.get(OrePrefixes.dust, Materials.Wood, 4L);
        ItemStack output = ItemList.SFMixture.get(outputAmount);
        FluidStack advancedGlue = Materials.AdvancedGlue.getFluid(100L);

        if (sulfurDust == null || metalDust == null || woodDust == null || output == null || advancedGlue == null) {
            MyMod.logInfo(
                "Skipped beeswax Super Fuel Binder recipe for " + metal + ": required input or output is unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(sulfurDust, metalDust, woodDust, beeswax.copy(), GTUtility.getIntegratedCircuit(2))
            .itemOutputs(output)
            .fluidInputs(advancedGlue)
            .duration(40 * GTRecipeBuilder.SECONDS)
            .eut(16)
            .addTo(RecipeMaps.mixerRecipes);
    }

    private static void registerSuperFuelBinderCreosoteBeeswaxRecipe() {
        ItemStack sulfurDust = GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1L);
        ItemStack woodDust = GTOreDictUnificator.get(OrePrefixes.dust, Materials.Wood, 4L);
        ItemStack beeswax = resolveForestryBeeswax(16);
        ItemStack output = ItemList.SFMixture.get(6);
        FluidStack creosote = getFirstAvailableFluid(2000, "creosote", "creosoteoil", "Creosote", "Creosote Oil");

        if (sulfurDust == null || woodDust == null || beeswax == null || output == null || creosote == null) {
            MyMod.logInfo(
                "Skipped creosote beeswax Super Fuel Binder recipe: sulfur, wood dust, Forestry beeswax, creosote, or output is unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(sulfurDust, woodDust, beeswax, GTUtility.getIntegratedCircuit(3))
            .itemOutputs(output)
            .fluidInputs(creosote)
            .duration(40 * GTRecipeBuilder.SECONDS)
            .eut(16)
            .addTo(RecipeMaps.mixerRecipes);

        MyMod.logInfo("Registered Mixer recipe: 16x Forestry Beeswax + 2000L Creosote -> 6x Super Fuel Binder.");
    }

    private static void registerBeeswaxFurnaceFuel() {
        if (OreDictionary.getOres("itemBeeswax")
            .isEmpty()) {
            MyMod.logInfo("Skipped Beeswax furnace fuel: no itemBeeswax ore dictionary entries found.");
            return;
        }

        GameRegistry.registerFuelHandler(new IFuelHandler() {

            @Override
            public int getBurnTime(ItemStack fuel) {
                if (fuel == null) {
                    return 0;
                }
                for (ItemStack ore : OreDictionary.getOres("itemBeeswax")) {
                    if (OreDictionary.itemMatches(ore, fuel, false)) {
                        return BEESWAX_FURNACE_BURN_TICKS;
                    }
                }
                return 0;
            }
        });

        MyMod.logInfo(
            "Registered Beeswax as a furnace fuel: burns for " + BEESWAX_FURNACE_BURN_TICKS
                + " ticks ("
                + (BEESWAX_FURNACE_BURN_TICKS / 20)
                + "s), any itemBeeswax ore-dict entry.");
    }

    private static void registerMagicSuperFuelBinderVoidMetalRecipes() {
        FluidStack moltenVoidMetal = Materials.Void.getMolten(36L);
        if (moltenVoidMetal == null) {
            MyMod.logInfo("Skipped Void Metal Magic Super Fuel Binder recipes: molten Void Metal is unavailable.");
            return;
        }

        registerMagicSuperFuelBinderVoidMetalRecipe(Materials.InfusedAir);
        registerMagicSuperFuelBinderVoidMetalRecipe(Materials.InfusedEarth);
        registerMagicSuperFuelBinderVoidMetalRecipe(Materials.InfusedEntropy);
        registerMagicSuperFuelBinderVoidMetalRecipe(Materials.InfusedFire);
        registerMagicSuperFuelBinderVoidMetalRecipe(Materials.InfusedOrder);
        registerMagicSuperFuelBinderVoidMetalRecipe(Materials.InfusedWater);

        MyMod.logInfo(
            "Registered Mixer recipes: 24x Super Fuel Binder + infused Thaumcraft material + 36 mB Void Metal -> 24x Magic Super Fuel Binder.");
    }

    private static void registerMagicSuperFuelBinderVoidMetalRecipe(Materials infusedMaterial) {
        ItemStack superFuelBinder = ItemList.SFMixture.get(24);
        ItemStack infusedDust = GTOreDictUnificator.get(OrePrefixes.dust, infusedMaterial, 1L);
        ItemStack magicSuperFuelBinder = ItemList.MSFMixture.get(24);
        FluidStack moltenVoidMetal = Materials.Void.getMolten(36L);

        if (superFuelBinder == null || infusedDust == null || magicSuperFuelBinder == null || moltenVoidMetal == null) {
            MyMod.logInfo(
                "Skipped Void Metal Magic Super Fuel Binder recipe for " + infusedMaterial
                    + ": required input or output is unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(superFuelBinder, infusedDust, GTUtility.getIntegratedCircuit(1))
            .itemOutputs(magicSuperFuelBinder)
            .fluidInputs(moltenVoidMetal)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(64)
            .addTo(RecipeMaps.mixerRecipes);
    }

    private static void registerLftrThoriumPlutoniumFuelRecipes() {
        FluidStack lftrFuelBase = MaterialsNuclides.LiFBeF2UF4.getFluidStack(1000);
        FluidStack lftrThoriumFuel = MaterialsNuclides.LiFBeF2ThF4UF4.getFluidStack(1000);
        FluidStack lftrHybridFuel = MaterialsNuclides.LiFBeF2ZrF4UF4.getFluidStack(1000);
        FluidStack moltenSaltBlanket = MaterialsNuclides.Li2BeF4.getFluidStack(200);
        FluidStack thoriumFluoride = MaterialsFluorides.THORIUM_TETRAFLUORIDE.getFluidStack(120);
        FluidStack zirconiumFluoride = MaterialsFluorides.ZIRCONIUM_TETRAFLUORIDE.getFluidStack(120);
        FluidStack uraniumTetrafluoride = MaterialsFluorides.URANIUM_TETRAFLUORIDE.getFluidStack(80);
        FluidStack uraniumHexafluoride = MaterialsFluorides.URANIUM_HEXAFLUORIDE.getFluidStack(1);
        FluidStack plutonium = getMaterialFluidOrFallback(
            Materials.Plutonium,
            100L,
            "molten.plutonium",
            "plutonium",
            "moltenplutonium");

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
                getMaterialFluidOrFallback(
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
                getMaterialFluidOrFallback(
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

    private static void registerNaquadahDustFuelRodRecipes() {
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

    private static void registerCrimsonCultArmorSalvageRecipes() {
        registerCrimsonCultMaceratorRecipe(
            "Crimson Cult Hood",
            resolveCrimsonCultArmor("ItemHelmetCultistRobe"),
            2,
            3000,
            1,
            150,
            800);

        registerCrimsonCultMaceratorRecipe(
            "Crimson Cult Robe",
            resolveCrimsonCultArmor("ItemChestplateCultistRobe"),
            5,
            4000,
            2,
            300,
            1200);

        registerCrimsonCultMaceratorRecipe(
            "Crimson Cult Leggings",
            resolveCrimsonCultArmor("ItemLeggingsCultistRobe"),
            4,
            3500,
            2,
            200,
            1000);

        registerCrimsonCultMaceratorRecipe(
            "Crimson Cult Helm",
            resolveCrimsonCultArmor("ItemHelmetCultistPlate"),
            3,
            3000,
            1,
            150,
            800);

        registerCrimsonCultMaceratorRecipe(
            "Crimson Cult Chestplate",
            resolveCrimsonCultArmor("ItemChestplateCultistPlate"),
            6,
            4000,
            2,
            300,
            1200);

        registerCrimsonCultMaceratorRecipe(
            "Crimson Cult Greaves",
            resolveCrimsonCultArmor("ItemLeggingsCultistPlate"),
            5,
            3500,
            2,
            200,
            1000);

        registerCrimsonCultMaceratorRecipe(
            "Crimson Cult Boots",
            resolveCrimsonCultArmor("ItemBootsCultist"),
            2,
            3000,
            1,
            150,
            800);
    }

    private static void registerCrimsonCultMaceratorRecipe(String armorName, ItemStack armorStack, int shadowNuggets,
        int thaumiumIngotChance, int thaumiumIngots, int voidMetalNuggetChance, int enchantedFabricChance) {
        if (armorStack == null) {
            MyMod.logInfo("Skipped " + armorName + " salvage recipe: source armor item is unavailable.");
            return;
        }

        ItemStack shadowNuggetStack = resolveShadowMetalNuggets(shadowNuggets);
        ItemStack thaumiumIngotStack = GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Thaumium, thaumiumIngots);
        ItemStack voidMetalNuggetStack = resolveVoidMetalNuggets(1);
        ItemStack enchantedFabricStack = resolveEnchantedFabric();

        if (shadowNuggetStack == null || thaumiumIngotStack == null
            || voidMetalNuggetStack == null
            || enchantedFabricStack == null) {
            MyMod.logInfo("Skipped " + armorName + " salvage recipe: required GT/Thaumcraft outputs are unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(armorStack.copy())
            .itemOutputs(shadowNuggetStack, thaumiumIngotStack, voidMetalNuggetStack, enchantedFabricStack)
            .outputChances(
                10000,
                clampChance(thaumiumIngotChance),
                clampChance(voidMetalNuggetChance),
                clampChance(enchantedFabricChance))
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(24)
            .addTo(RecipeMaps.maceratorRecipes);

        MyMod.logInfo(
            "Registered Macerator salvage recipe for " + armorName
                + ": "
                + shadowNuggets
                + "x Shadow Nugget, "
                + thaumiumIngots
                + "x Thaumium Ingot @ "
                + (thaumiumIngotChance / 100)
                + "%, "
                + (voidMetalNuggetChance / 100)
                + "% Void Metal Nugget, "
                + (enchantedFabricChance / 100)
                + "% Enchanted Fabric.");
    }

    private static int clampChance(int chance) {
        return Math.max(1, Math.min(10000, chance));
    }

    private static ItemStack resolveCrimsonCultArmor(String registryName) {
        Item item = findItem("Thaumcraft", registryName);
        if (item == null) {
            item = findItem("thaumcraft", registryName);
        }
        return item == null ? null : new ItemStack(item, 1, 0);
    }

    private static ItemStack getThaumcraftItem(String registryName, int meta, int amount) {
        Item item = findItem("Thaumcraft", registryName);
        if (item == null) {
            item = findItem("thaumcraft", registryName);
        }
        return item == null ? null : new ItemStack(item, amount, meta);
    }

    private static ItemStack resolveForestryBeeswax(int amount) {
        Item beeswax = findItem("Forestry", "beeswax");
        if (beeswax == null) {
            beeswax = findItem("forestry", "beeswax");
        }
        if (beeswax != null) {
            return new ItemStack(beeswax, amount, 0);
        }
        return resolveFirstOreDictStack(amount, "itemBeeswax");
    }

    private static ItemStack resolveShadowMetalNuggets(int amount) {
        ItemStack stack = resolveFirstOreDictStack(
            amount,
            "nuggetShadow",
            "nuggetShadowmetal",
            "nuggetShadowMetal",
            "nuggetShadowium");
        if (stack != null) {
            return stack;
        }
        return resolveFirstOreDictMatchByTokens(amount, "nugget", "shadow");
    }

    private static ItemStack resolveVoidMetalNuggets(int amount) {
        ItemStack stack = resolveFirstOreDictStack(
            amount,
            "nuggetVoid",
            "nuggetVoidmetal",
            "nuggetVoidMetal",
            "nuggetVoidMetalThaumcraft");
        if (stack != null) {
            return stack;
        }
        return resolveFirstOreDictMatchByTokens(amount, "nugget", "void");
    }

    private static ItemStack resolveEnchantedFabric() {
        ItemStack stack = resolveFirstOreDictStack(1, "clothEnchanted", "fabricEnchanted", "itemEnchantedFabric");
        if (stack != null) {
            return stack;
        }

        Item resource = findItem("Thaumcraft", "ItemResource");
        if (resource == null) {
            resource = findItem("thaumcraft", "ItemResource");
        }
        if (resource == null) {
            return null;
        }

        for (int meta = 0; meta <= 31; meta++) {
            ItemStack probe = new ItemStack(resource, 1, meta);
            String unlocalized = resource.getUnlocalizedName(probe);
            if (unlocalized != null && unlocalized.toLowerCase()
                .contains("fabric")) {
                return probe;
            }
        }

        return new ItemStack(resource, 1, 7);
    }

    private static ItemStack resolveFirstOreDictStack(int amount, String... oreNames) {
        for (String oreName : oreNames) {
            List<ItemStack> stacks = OreDictionary.getOres(oreName);
            if (stacks == null || stacks.isEmpty()) {
                continue;
            }
            ItemStack first = stacks.get(0);
            if (first != null && first.getItem() != null) {
                return GTUtility.copyAmount(amount, first);
            }
        }
        return null;
    }

    private static ItemStack resolveFirstOreDictMatchByTokens(int amount, String... requiredTokens) {
        for (String oreName : OreDictionary.getOreNames()) {
            String normalized = oreName == null ? "" : oreName.toLowerCase();
            boolean matches = true;
            for (String token : requiredTokens) {
                if (!normalized.contains(token.toLowerCase())) {
                    matches = false;
                    break;
                }
            }
            if (!matches) {
                continue;
            }

            List<ItemStack> stacks = OreDictionary.getOres(oreName);
            if (stacks == null || stacks.isEmpty()) {
                continue;
            }
            ItemStack first = stacks.get(0);
            if (first != null && first.getItem() != null) {
                return GTUtility.copyAmount(amount, first);
            }
        }
        return null;
    }

    private static ItemStack resolveLargeTungstensteelFluidCell() {
        ItemStack stack = resolveFirstOreDictStack(
            1,
            "cellLargeTungstenSteel",
            "cellLargeTungstensteel",
            "largeFluidCellTungstenSteel",
            "largeFluidCellTungstensteel");
        if (stack != null) {
            return stack;
        }
        return resolveFirstOreDictMatchByTokens(1, "cell", "large", "tungstensteel");
    }

    private static Item findItem(String modId, String itemName) {
        return GameRegistry.findItem(modId, itemName);
    }

    private static void registerFiberglassBoardAlternateFoilRecipes() {
        registerFiberglassBoardCopperFoilLuVRecipe();
        registerFiberglassBoardSilverFoilZpmRecipe();
        registerFiberglassBoardGoldFoilUvRecipe();
    }

    private static void registerFiberglassBoardCopperFoilLuVRecipe() {
        ItemStack resinPlate = Materials.EpoxidFiberReinforced.getPlates(1);
        ItemStack copperFoil = GTOreDictUnificator.get(OrePrefixes.foil, Materials.Copper, 20L);
        FluidStack sulfuricAcid = getFluidOrGas(Materials.SulfuricAcid, 1000L);
        ItemStack fiberglassBoard = ItemList.Circuit_Board_Fiberglass.get(1);

        if (resinPlate == null || resinPlate.getItem() == null
            || copperFoil == null
            || copperFoil.getItem() == null
            || sulfuricAcid == null
            || fiberglassBoard == null
            || fiberglassBoard.getItem() == null) {
            MyMod.logInfo("Skipped Copper Foil Fiberglass Board (LuV) recipe: required items or fluid unavailable.");
            return;
        }

        // Copper is the real industry-standard PCB conductor (Aluminium-clad boards are a niche exception).
        // Cheaper/more abundant than Aluminium by LuV, but copper oxidizes faster than aluminium's native
        // oxide layer, so it needs a longer passivation step before it bonds cleanly to the resin.
        GTValues.RA.stdBuilder()
            .itemInputs(resinPlate, copperFoil)
            .itemOutputs(fiberglassBoard)
            .fluidInputs(sulfuricAcid)
            .duration(50)
            .eut(1920)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        MyMod.logInfo(
            "Registered Chemical Reactor recipe: Resin Plate + 20x Copper Foil + 1000L Sulfuric Acid -> 1x Fiberglass Circuit Board.");
    }

    private static void registerFiberglassBoardSilverFoilZpmRecipe() {
        ItemStack resinPlate = Materials.EpoxidFiberReinforced.getPlates(1);
        ItemStack silverFoil = GTOreDictUnificator.get(OrePrefixes.foil, Materials.Silver, 16L);
        FluidStack sulfuricAcid = getFluidOrGas(Materials.SulfuricAcid, 500L);
        ItemStack fiberglassBoard = ItemList.Circuit_Board_Fiberglass.get(1);

        if (resinPlate == null || resinPlate.getItem() == null
            || silverFoil == null
            || silverFoil.getItem() == null
            || sulfuricAcid == null
            || fiberglassBoard == null
            || fiberglassBoard.getItem() == null) {
            MyMod.logInfo("Skipped Silver Foil Fiberglass Board (ZPM) recipe: required items or fluid unavailable.");
            return;
        }

        // Silver's higher conductivity than Copper/Aluminium means fewer foil sheets give equivalent trace
        // performance - real-world grounding for premium RF/hybrid-circuit silver traces. Net cheaper than
        // the Copper path despite silver's higher unit cost, once ZPM-tier materials are flowing.
        GTValues.RA.stdBuilder()
            .itemInputs(resinPlate, silverFoil)
            .itemOutputs(fiberglassBoard)
            .fluidInputs(sulfuricAcid)
            .duration(16)
            .eut(7680)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered LCR recipe: Resin Plate + 16x Silver Foil + 500L Sulfuric Acid -> 1x Fiberglass Circuit Board.");
    }

    private static void registerFiberglassBoardGoldFoilUvRecipe() {
        ItemStack resinPlate = Materials.EpoxidFiberReinforced.getPlates(1);
        ItemStack goldFoil = GTOreDictUnificator.get(OrePrefixes.foil, Materials.Gold, 8L);
        FluidStack sulfuricAcid = getFluidOrGas(Materials.SulfuricAcid, 1000L);
        ItemStack fiberglassBoards = ItemList.Circuit_Board_Fiberglass.get(2);

        if (resinPlate == null || resinPlate.getItem() == null
            || goldFoil == null
            || goldFoil.getItem() == null
            || sulfuricAcid == null
            || fiberglassBoards == null
            || fiberglassBoards.getItem() == null) {
            MyMod.logInfo("Skipped Gold Foil Fiberglass Board (UV) recipe: required items or fluid unavailable.");
            return;
        }

        // Gold-plated/gold-traced boards are real aerospace and satellite-grade PCB practice (ENIG-style
        // corrosion immunity for ultra-reliability electronics). UV-tier batch scale doubles board output
        // per craft.
        GTValues.RA.stdBuilder()
            .itemInputs(resinPlate, goldFoil)
            .itemOutputs(fiberglassBoards)
            .fluidInputs(sulfuricAcid)
            .duration(8)
            .eut(30720)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered LCR recipe: Resin Plate + 8x Gold Foil + 1000L Sulfuric Acid -> 2x Fiberglass Circuit Board.");
    }

    private static void registerFiberglassBoardCopperPcbFactoryRecipe() {
        // Budget sibling of GT's "More Advanced Circuit Board" PCB Factory loop (tier 3+, IC1/2/3 ->
        // Fiberglass_Advanced via Aluminium+EnergeticAlloy foils). No circuit gate here - Copper+AnnealedCopper
        // foils (the same cheap conductor pair GT already uses for base Plastic boards) already disambiguate
        // this recipe from GT's own -> plain Fiberglass board.
        // Shares the tier-3 EpoxidFiberReinforced/etc plastic slot, no new plastic tier registered.
        for (int tier = 3; tier <= PCBFactoryManager.mTiersOfPlastics; tier++) {
            Materials plasticMaterial = PCBFactoryManager.getPlasticMaterialFromTier(tier);
            ItemStack resinPlate = plasticMaterial == null ? null : plasticMaterial.getPlates(1);
            ItemStack annealedCopperFoil = GTOreDictUnificator
                .get(OrePrefixes.foil, Materials.AnnealedCopper, (long) (16 * (Math.sqrt(tier - 2))));
            ItemStack copperFoil = GTOreDictUnificator
                .get(OrePrefixes.foil, Materials.Copper, (long) (16 * (Math.sqrt(tier - 2))));
            FluidStack sulfuricAcid = getFluidOrGas(Materials.SulfuricAcid, (long) (500 * (Math.sqrt(tier - 2))));
            FluidStack ironIIIChloride = getFluidOrGas(
                Materials.IronIIIChloride,
                (long) (1000 * (Math.sqrt(tier - 2))));

            if (resinPlate == null || resinPlate.getItem() == null
                || annealedCopperFoil == null
                || annealedCopperFoil.getItem() == null
                || copperFoil == null
                || copperFoil.getItem() == null
                || sulfuricAcid == null
                || ironIIIChloride == null) {
                MyMod.logInfo(
                    "Skipped Copper Fiberglass Board PCB Factory recipe at tier " + tier
                        + ": required inputs unavailable.");
                continue;
            }

            int amountOfBoards = (int) Math.ceil(8 * (Math.sqrt(Math.pow(2, tier - 3))));
            List<ItemStack> boards = new ArrayList<>();
            for (int remaining = amountOfBoards; remaining > 64; remaining -= 64) {
                boards.add(ItemList.Circuit_Board_Fiberglass.get(64));
            }
            boards.add(ItemList.Circuit_Board_Fiberglass.get(amountOfBoards % 64 == 0 ? 64 : amountOfBoards % 64));

            GTValues.RA.stdBuilder()
                .itemInputs(resinPlate, annealedCopperFoil, copperFoil)
                .fluidInputs(sulfuricAcid, ironIIIChloride)
                .itemOutputs(boards.toArray(new ItemStack[0]))
                .duration((int) Math.ceil(600 / Math.sqrt(Math.pow(1.5, tier - 3.5))))
                .eut((int) GTValues.VP[tier] * 3 / 4)
                .metadata(PCBFactoryTierKey.INSTANCE, 1)
                .addTo(RecipeMaps.pcbFactoryRecipes);
        }

        MyMod.logInfo(
            "Registered PCB Factory budget recipe line: Copper+AnnealedCopper foils -> plain Fiberglass Circuit Board (tier 3+).");
    }

    private static void removeNitricOxideRegularChemicalReactorRecipe() {
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

    private static boolean hasRocketFuelRecipe(FluidStack fuel) {
        if (fuel == null || GTPPRecipeMaps.rocketFuels == null) {
            return false;
        }
        for (GTRecipe recipe : GTPPRecipeMaps.rocketFuels.getAllRecipes()) {
            if (recipe == null || recipe.mFluidInputs == null) {
                continue;
            }
            for (FluidStack input : recipe.mFluidInputs) {
                if (input != null && input.isFluidEqual(fuel)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void addRocketFuelRecipe(FluidStack fuel, int fuelValue) {
        if (fuel == null || fuel.getFluid() == null || GTPPRecipeMaps.rocketFuels == null) {
            return;
        }
        GTPPRecipeMaps.rocketFuels.add(
            new GTRecipe(
                false,
                GTValues.emptyItemStackArray,
                GTValues.emptyItemStackArray,
                null,
                GTValues.emptyIntArray,
                new FluidStack[] { new FluidStack(fuel.getFluid(), 1000) },
                GTValues.emptyFluidStackArray,
                0,
                0,
                fuelValue));
    }

    private static FluidStack getFirstAvailableFluid(int amount, String... names) {
        for (String name : names) {
            FluidStack stack = FluidRegistry.getFluidStack(name, amount);
            if (stack != null) {
                return stack;
            }
        }
        return null;
    }

    private static FluidStack getFluidOrGas(Materials material, long amount) {
        if (material == null) {
            return null;
        }
        FluidStack fluid = material.getFluid(amount);
        if (fluid != null) {
            return fluid;
        }
        return material.getGas(amount);
    }

    private static FluidStack getMaterialFluidOrFallback(Materials material, long amount, String... fallbackNames) {
        FluidStack primary = getFluidOrGas(material, amount);
        if (primary != null) {
            return primary;
        }
        return getFirstAvailableFluid((int) amount, fallbackNames);
    }

    private static int scaleDurationByPercent(int baseDuration, int durationPercent) {
        return GTUtility.safeInt((long) baseDuration * Math.max(1, durationPercent) / 100L);
    }

    private static int scaleDurationForSpeedBoost(int baseDuration, int speedBoostPercent) {
        return Math.max(1, GTUtility.safeInt((long) baseDuration * 100L / (100L + Math.max(0, speedBoostPercent))));
    }

}
