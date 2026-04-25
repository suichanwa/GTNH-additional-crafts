package gtnh_additional_crafts.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsKevlar;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.fluids.GTPPFluids;
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

    public static void registerRecipes() {
        registerSodiumBatteryX16Recipe();
        registerNitricOxideLargeChemicalReactorRecipe();
        registerAlgaeBiomassToCompostRecipe();
        registerAlgaeProcessingChainRecipes();
        registerCelluloseFiberBiomassRecipe();
        registerGlycerolNitrationRecipe();
        registerGlycerolHydrogenCrackingRecipe();
        registerGlycerolFermentationRecipe();
        registerPhenolFormaldehydeResinRecipe();
        registerPhenolHydrogenToCyclohexaneRecipe();
        registerPhenolNitrationRecipe();
        registerKeroseneHydrocrackingRecipe();
        registerKeroseneSulfuricLightFuelRecipe();
        registerBiomassCrudeBioTarCokeOvenRecipe();
        registerCrudeBioTarDistillationRecipe();
        registerCrudeBioTarMiddleFractionDistillationRecipe();
        registerCrudeBioTarLightFractionDistillationRecipe();
        registerNaphthaToNaphthaleneRecipe();
        registerNitrogenRocketFuelUpgradeRecipe();
        registerJetFuelRocketFuelRecipe();
        registerAcetaldehydeHydrogenationRecipe();
        registerMethanolCarbonMonoxideHydrogenToEthanolRecipe();
        registerMethaneToAcetyleneDehydratorRecipe();
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
            new Object[] { "BB", "BB", 'B', quadSodiumBattery });
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
            .itemInputs(organicResidue, GTUtility.getIntegratedCircuit(8))
            .fluidInputs(water, carbonDioxide)
            .fluidOutputs(hydrogen, oxygen)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(60)
            .addTo(RecipeMaps.electrolyzerRecipes);

        MyMod.logInfo(
            "Registered algae processing chain recipes (macerator -> mixer -> chemical reactor -> electrolyzer).");
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

        if (acetaldehyde == null || hydrogen == null || ethanol == null) {
            MyMod.logInfo("Skipped Acetaldehyde + Hydrogen -> Ethanol recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(acetaldehyde, hydrogen)
            .fluidOutputs(ethanol)
            .duration(6 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.chemicalReactorRecipes);

        MyMod.logInfo("Registered Chemical Reactor recipe: 1000L Acetaldehyde + 1000L Hydrogen -> 1000L Ethanol.");
    }

    private static void registerMethanolCarbonMonoxideHydrogenToEthanolRecipe() {
        FluidStack methanol = getFluidOrGas(Materials.Methanol, 1000L);
        FluidStack carbonMonoxide = getFluidOrGas(Materials.CarbonMonoxide, 1000L);
        FluidStack hydrogen = getFluidOrGas(Materials.Hydrogen, 1000L);
        FluidStack ethanol = getFluidOrGas(Materials.Ethanol, 1000L);

        if (methanol == null || carbonMonoxide == null || hydrogen == null || ethanol == null) {
            MyMod.logInfo("Skipped Methanol + CO + H2 -> Ethanol recipe: required fluids unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(methanol, carbonMonoxide, hydrogen)
            .fluidOutputs(ethanol)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(24))
            .fluidInputs(
                getFluidOrGas(Materials.Methanol, 9000L),
                getFluidOrGas(Materials.CarbonMonoxide, 9000L),
                getFluidOrGas(Materials.Hydrogen, 9000L))
            .fluidOutputs(getFluidOrGas(Materials.Ethanol, 9000L))
            .duration(90 * GTRecipeBuilder.SECONDS)
            .eut(480)
            .addTo(RecipeMaps.multiblockChemicalReactorRecipes);

        MyMod.logInfo(
            "Registered LCR recipes: IC-1 1000L Methanol + 1000L CO + 1000L H2 -> 1000L Ethanol; IC-24 9x batch.");
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

    private static int scaleDurationByPercent(int baseDuration, int durationPercent) {
        return GTUtility.safeInt((long) baseDuration * Math.max(1, durationPercent) / 100L);
    }

    private static int scaleDurationForSpeedBoost(int baseDuration, int speedBoostPercent) {
        return Math.max(1, GTUtility.safeInt((long) baseDuration * 100L / (100L + Math.max(0, speedBoostPercent))));
    }

}
