package gtnh_additional_crafts.compat.gregtech;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.tileentities.machines.multi.MTEExtremeDieselEngine;
import gtnh_additional_crafts.Config;
import gtnh_additional_crafts.fluid.ModFluids;

public class PatchedMTEExtremeDieselEngine extends MTEExtremeDieselEngine {

    private BoostMode boostMode = BoostMode.NONE;

    private enum BoostMode {
        NONE,
        OXYGEN,
        CRYONITROX
    }

    public PatchedMTEExtremeDieselEngine(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    public PatchedMTEExtremeDieselEngine(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new PatchedMTEExtremeDieselEngine(super.mName);
    }

    @Override
    public boolean depleteInput(FluidStack fluidStack) {
        if (!isBoosterRequest(fluidStack)) {
            return super.depleteInput(fluidStack);
        }

        if (depleteCryonitroxForBoost(fluidStack.amount)) {
            boostMode = BoostMode.CRYONITROX;
            return true;
        }

        if (super.depleteInput(fluidStack)) {
            boostMode = BoostMode.OXYGEN;
            return true;
        }

        boostMode = BoostMode.NONE;
        return false;
    }

    @Override
    public int getMaxEfficiency(ItemStack itemStack) {
        if (!super.boostEu) {
            return Config.lceDefaultMaxEfficiency;
        }
        if (boostMode == BoostMode.CRYONITROX) {
            return Config.lceCryonitroxBoostedMaxEfficiency;
        }
        return Config.lceOxygenBoostedMaxEfficiency;
    }

    @Override
    public CheckRecipeResult checkProcessing() {
        boostMode = BoostMode.NONE;
        return super.checkProcessing();
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        long oxygenOutput = calculateOutput(getNominalOutput(), Config.lceOxygenBoostedMaxEfficiency);
        long cryonitroxOutput = calculateOutput(getNominalOutput(), Config.lceCryonitroxBoostedMaxEfficiency);
        long cryonitroxConsumptionPerSecond = (long) Config.lceCryonitroxConsumptionPerTick * 20L;

        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Combustion Generator, ECE")
            .addInfo("Supply high rating fuel and 8000L of Lubricant per hour to run")
            .addInfo("Supply 40L/s of Liquid Oxygen to boost output (optional)")
            .addInfo(
                "Cryonitrox boost uses " + GTUtility.formatNumbers(cryonitroxConsumptionPerSecond)
                    + "L/s and reaches "
                    + (Config.lceCryonitroxBoostedMaxEfficiency / 100)
                    + "% fuel efficiency")
            .addInfo(
                "Liquid Oxygen boost: up to " + GTUtility.formatNumbers(oxygenOutput)
                    + "EU/t at "
                    + (Config.lceOxygenBoostedMaxEfficiency / 100)
                    + "% fuel efficiency")
            .addInfo(
                "Cryonitrox boost: up to " + GTUtility.formatNumbers(cryonitroxOutput)
                    + "EU/t at "
                    + (Config.lceCryonitroxBoostedMaxEfficiency / 100)
                    + "% fuel efficiency")
            .addInfo("You need to wait for it to reach 300% to output full power")
            .addPollutionAmount(getPollutionPerSecond(null))
            .beginStructureBlock(3, 3, 4, false)
            .addController("Front center")
            .addCasingInfoRange("Robust Tungstensteel Machine Casing", 16, 22, false)
            .addOtherStructurePart("Titanium Gear Box Machine Casing", "Inner 2 blocks")
            .addOtherStructurePart("Extreme Engine Intake Machine Casing", "8x, ring around controller")
            .addStructureInfo("Extreme Engine Intake Casings must not be obstructed in front (only air blocks)")
            .addDynamoHatch("Back center", 2)
            .addMaintenanceHatch("One of the casings next to a Gear Box", 1)
            .addMufflerHatch("Top middle back, above the rear Gear Box", 1)
            .addInputHatch("HOG, next to a Gear Box", 1)
            .addInputHatch("Lubricant, next to a Gear Box", 1)
            .addInputHatch("Liquid Oxygen or Cryonitrox, optional, next to a Gear Box", 1)
            .toolTipFinisher();
        return tt;
    }

    private boolean isBoosterRequest(FluidStack fluidStack) {
        if (fluidStack == null) {
            return false;
        }
        Materials booster = getBooster();
        if (booster != null) {
            FluidStack boosterGas = booster.getGas(Math.max(1, fluidStack.amount));
            if (boosterGas != null && fluidStack.isFluidEqual(boosterGas)) {
                return true;
            }
            FluidStack boosterFluid = booster.getFluid(Math.max(1, fluidStack.amount));
            if (boosterFluid != null && fluidStack.isFluidEqual(boosterFluid)) {
                return true;
            }
        }
        return false;
    }

    private boolean depleteCryonitroxForBoost(int boosterRequestedPerTick) {
        int consumption = Math.max(boosterRequestedPerTick, Math.max(1, Config.lceCryonitroxConsumptionPerTick));
        FluidStack cryonitrox = ModFluids.getCryonitroxOxidizer(consumption);
        return cryonitrox != null && super.depleteInput(cryonitrox);
    }

    private static long calculateOutput(int nominalOutput, int maxEfficiency) {
        return (long) nominalOutput * maxEfficiency / 10000L;
    }
}
