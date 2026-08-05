package gtnh_additional_crafts.recipe.loaders;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsKevlar;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.fluid.ModFluids;
import gtnh_additional_crafts.recipe.util.DurationMath;
import gtnh_additional_crafts.recipe.util.FluidLookup;
import gtnh_additional_crafts.recipe.util.ItemLookup;

public final class BioTarRecipes {

    private BioTarRecipes() {}

    public static void register() {
        registerBiomassCrudeBioTarCokeOvenRecipe();
        registerLogCokeOvenWoodVinegarRecipe();
        registerCrudeBioTarDistillationRecipe();
        registerCrudeBioTarMiddleFractionDistillationRecipe();
        registerCrudeBioTarLightFractionDistillationRecipe();
        registerWoodVinegarCalciumAcetateRecipe();
    }

    private static final int CRUDE_BIO_TAR_BASE_DISTILLATION_DURATION = 20 * GTRecipeBuilder.SECONDS;
    private static final int CRUDE_BIO_TAR_MIDDLE_DISTILLATION_DURATION = DurationMath
        .scaleDurationByPercent(CRUDE_BIO_TAR_BASE_DISTILLATION_DURATION, 65);
    private static final int CRUDE_BIO_TAR_LIGHT_DISTILLATION_DURATION = DurationMath.scaleDurationForSpeedBoost(
        DurationMath.scaleDurationByPercent(CRUDE_BIO_TAR_BASE_DISTILLATION_DURATION, 35),
        70);

    public static void registerBiomassCrudeBioTarCokeOvenRecipe() {
        FluidStack biomass = FluidLookup.getFirstAvailableFluid(1000, "ic2biomass", "biomass", "Biomass");
        FluidStack nitrogen = FluidLookup.getFluidOrGas(Materials.Nitrogen, 250L);
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

    public static void registerLogCokeOvenWoodVinegarRecipe() {
        // Real destructive distillation of wood (same reaction vanilla's Pyrolyse Oven already runs on
        // logs), just carbonized in the Industrial Coke Oven multiblock instead: wood -> charcoal +
        // pyroligneous acid vapor (Wood Vinegar), under an inert Nitrogen atmosphere to keep it from
        // just burning to ash.
        ItemStack logs = ItemLookup.resolveFirstOreDictStack(16, "logWood");
        FluidStack nitrogen = FluidLookup.getFluidOrGas(Materials.Nitrogen, 1000L);
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

    public static void registerCrudeBioTarDistillationRecipe() {
        FluidStack crudeBioTar = ModFluids.getBioTar(1000);
        FluidStack anthracene = GTPPFluids.Anthracene == null
            ? FluidLookup.getFirstAvailableFluid(300, "anthracene", "Anthracene")
            : new FluidStack(GTPPFluids.Anthracene, 300);
        FluidStack naphthalene = GTPPFluids.Naphthalene == null
            ? FluidLookup.getFirstAvailableFluid(150, "naphthalene", "Naphthalene")
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

    public static void registerCrudeBioTarMiddleFractionDistillationRecipe() {
        FluidStack crudeBioTar = ModFluids.getBioTar(1000);
        FluidStack kerosene = GTPPFluids.Kerosene == null
            ? FluidLookup.getFirstAvailableFluid(320, "kerosene", "Kerosene")
            : new FluidStack(GTPPFluids.Kerosene, 320);
        FluidStack naphthenicAcid = MaterialsKevlar.NaphthenicAcid == null
            ? FluidLookup
                .getFirstAvailableFluid(220, "naphthenicacid", "naphthenic_acid", "NaphthenicAcid", "Naphthenic Acid")
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

    public static void registerCrudeBioTarLightFractionDistillationRecipe() {
        FluidStack crudeBioTar = ModFluids.getBioTar(1000);
        FluidStack biogas = FluidLookup.getFirstAvailableFluid(400, "ic2biogas", "biogas", "Biogas", "BioGas");
        FluidStack water = FluidLookup.getFluidOrGas(Materials.Water, 100L);
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

    public static void registerWoodVinegarCalciumAcetateRecipe() {
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

}
