package com.myname.mymodid.compat.kekztech;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import kekztech.common.tileentities.MTESOFuelCellMK1;

public class PatchedMTESOFuelCellMK1 extends MTESOFuelCellMK1 {

    private static final int OXYGEN_PER_SEC = 100;
    private static final int EU_PER_TICK = 2048;
    private static final int STEAM_PER_SEC = 20000;

    private static final int HELIUM_PER_SEC = 20;
    private static final int OXYGEN_REDUCTION_PERCENT = 30;
    private static final int FUEL_REDUCTION_PERCENT = 5;
    private static final int REDUCED_OXYGEN_PER_SEC = Math.max(1, OXYGEN_PER_SEC * (100 - OXYGEN_REDUCTION_PERCENT) / 100);

    public PatchedMTESOFuelCellMK1(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public PatchedMTESOFuelCellMK1(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity var1) {
        return new PatchedMTESOFuelCellMK1(super.mName);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Gas Turbine")
            .addInfo("Oxidizes gas fuels to generate electricity without polluting the environment")
            .addInfo(
                "Consumes up to " + GTUtility.formatNumbers((long) EU_PER_TICK * 20L)
                    + "EU worth of fuel with up to 100% efficiency each second")
            .addInfo("Steam production requires the SOFC to heat up completely first")
            .addInfo("Outputs " + EU_PER_TICK + "EU/t and " + STEAM_PER_SEC + "L/s Steam")
            .addInfo("Additionally, requires " + OXYGEN_PER_SEC + "L/s Oxygen gas")
            .addInfo(
                "Optionally, consumes " + HELIUM_PER_SEC + "L/s Helium to reduce Oxygen usage by "
                    + OXYGEN_REDUCTION_PERCENT + "% and fuel usage by " + FUEL_REDUCTION_PERCENT + "%")
            .beginStructureBlock(3, 3, 5, false)
            .addController("Front center")
            .addCasingInfoMin("Clean Stainless Steel Casing", 12, false)
            .addOtherStructurePart("YSZ Ceramic Electrolyte Unit", "3x, Center 1x1x3")
            .addOtherStructurePart("Reinforced Glass", "6x, touching the electrolyte units on the horizontal sides")
            .addDynamoHatch("Back center", 2)
            .addMaintenanceHatch("Any casing", 1)
            .addInputHatch("Fuel, any casing", 1)
            .addInputHatch("Oxygen, any casing", 1)
            .addInputHatch("Optional Helium, any casing", 1)
            .addOutputHatch("Steam, any casing", 1)
            .toolTipFinisher();
        return tt;
    }

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing() {
        final ArrayList<FluidStack> storedFluids = super.getStoredFluids();
        Collection<GTRecipe> recipeList = RecipeMaps.gasTurbineFuels.getAllRecipes();

        final FluidStack heliumPerSecond = Materials.Helium.getGas(HELIUM_PER_SEC);
        final FluidStack oxygenNormalPerSecond = Materials.Oxygen.getGas(OXYGEN_PER_SEC);
        final FluidStack oxygenReducedPerSecond = Materials.Oxygen.getGas(REDUCED_OXYGEN_PER_SEC);

        for (FluidStack hatchFluid : storedFluids) {
            for (GTRecipe aFuel : recipeList) {
                FluidStack liquid;
                if ((liquid = GTUtility.getFluidForFilledItem(aFuel.getRepresentativeInput(0), true)) != null
                    && hatchFluid.isFluidEqual(liquid)) {

                    int baseFuelPerSecond = (EU_PER_TICK * 20) / aFuel.mSpecialValue;
                    int boostedFuelPerSecond = Math.max(
                        1,
                        (baseFuelPerSecond * (100 - FUEL_REDUCTION_PERCENT) + 99) / 100);

                    boolean canUseHeliumBoost = heliumPerSecond != null && oxygenReducedPerSecond != null
                        && hasFluid(heliumPerSecond)
                        && hasFluid(oxygenReducedPerSecond);

                    FluidStack fuelToConsume = liquid.copy();
                    fuelToConsume.amount = canUseHeliumBoost ? boostedFuelPerSecond : baseFuelPerSecond;
                    if (!super.depleteInput(fuelToConsume)) {
                        continue;
                    }

                    if (canUseHeliumBoost && !super.depleteInput(heliumPerSecond)) {
                        super.mEUt = 0;
                        super.mEfficiency = 0;
                        return CheckRecipeResultRegistry.NO_FUEL_FOUND;
                    }

                    FluidStack oxygenToConsume = canUseHeliumBoost ? oxygenReducedPerSecond : oxygenNormalPerSecond;
                    if (oxygenToConsume == null || !super.depleteInput(oxygenToConsume)) {
                        super.mEUt = 0;
                        super.mEfficiency = 0;
                        return CheckRecipeResultRegistry.NO_FUEL_FOUND;
                    }

                    super.mEUt = EU_PER_TICK;
                    super.mMaxProgresstime = 20;
                    super.mEfficiencyIncrease = 40;
                    if (super.mEfficiency == getMaxEfficiency(null)) {
                        super.addOutput(Materials.Steam.getGas(STEAM_PER_SEC));
                    }
                    return CheckRecipeResultRegistry.GENERATING;
                }
            }
        }

        super.mEUt = 0;
        super.mEfficiency = 0;
        return CheckRecipeResultRegistry.NO_FUEL_FOUND;
    }

    private boolean hasFluid(FluidStack requiredFluid) {
        if (requiredFluid == null) {
            return false;
        }
        int totalAmount = 0;
        for (FluidStack storedFluid : super.getStoredFluids()) {
            if (storedFluid != null && storedFluid.isFluidEqual(requiredFluid)) {
                totalAmount += storedFluid.amount;
                if (totalAmount >= requiredFluid.amount) {
                    return true;
                }
            }
        }
        return false;
    }
}
