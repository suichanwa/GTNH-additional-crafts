package com.myname.mymodid.compat.gregtech;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.tileentities.machines.multi.MTEDieselEngine;

public class PatchedMTEDieselEngine extends MTEDieselEngine {

    private static final int DEFAULT_MAX_EFFICIENCY = 10000;
    private static final int OXYGEN_BOOSTED_MAX_EFFICIENCY = 30000;
    private static final int DINITROGEN_TETROXIDE_BOOSTED_MAX_EFFICIENCY = 40000;

    private static final int DINITROGEN_TETROXIDE_BASE_CONSUMPTION_PER_TICK = 1;
    private static final int DINITROGEN_TETROXIDE_EXTRA_CONSUMPTION_INTERVAL_TICKS = 4;

    private boolean boostedByDinitrogenTetroxide = false;
    private int dinitrogenTetroxideTickCounter = 0;

    public PatchedMTEDieselEngine(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    public PatchedMTEDieselEngine(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new PatchedMTEDieselEngine(super.mName);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Combustion Generator, LCE")
            .addInfo("Supply Diesel Fuels and 1000L of Lubricant per hour to run")
            .addInfo("Supply 40L/s Oxygen or 25L/s Dinitrogen Tetroxide to boost output (optional)")
            .addInfo("Default: Produces 2048EU/t at 100% fuel efficiency")
            .addInfo("Boosted: Produces 6144EU/t at 150% fuel efficiency")
            .addInfo("N2O4 boost can reach 400% max efficiency (up to 8192EU/t)")
            .addPollutionAmount(getPollutionPerSecond(null))
            .beginStructureBlock(3, 3, 4, false)
            .addController("Front center")
            .addCasingInfoRange("Stable Titanium Machine Casing", 16, 22, false)
            .addOtherStructurePart("Titanium Gear Box Machine Casing", "Inner 2 blocks")
            .addOtherStructurePart("Engine Intake Machine Casing", "8x, ring around controller")
            .addStructureInfo("Engine Intake Casings must not be obstructed in front (only air blocks)")
            .addDynamoHatch("Back center", 2)
            .addMaintenanceHatch("One of the casings next to a Gear Box", 1)
            .addMufflerHatch("Top middle back, above the rear Gear Box", 1)
            .addInputHatch("Diesel Fuel, next to a Gear Box", 1)
            .addInputHatch("Lubricant, next to a Gear Box", 1)
            .addInputHatch("Oxygen or Dinitrogen Tetroxide, optional, next to a Gear Box", 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public boolean depleteInput(FluidStack fluidStack) {
        if (!isOxygenRequest(fluidStack)) {
            return super.depleteInput(fluidStack);
        }

        if (super.depleteInput(fluidStack)) {
            boostedByDinitrogenTetroxide = false;
            return true;
        }

        boolean consumedDinitrogenTetroxide = depleteDinitrogenTetroxideForBoost();
        boostedByDinitrogenTetroxide = consumedDinitrogenTetroxide;
        return consumedDinitrogenTetroxide;
    }

    @Override
    public int getMaxEfficiency(ItemStack itemStack) {
        if (!super.boostEu) {
            return DEFAULT_MAX_EFFICIENCY;
        }
        if (boostedByDinitrogenTetroxide) {
            return DINITROGEN_TETROXIDE_BOOSTED_MAX_EFFICIENCY;
        }
        return OXYGEN_BOOSTED_MAX_EFFICIENCY;
    }

    private boolean isOxygenRequest(FluidStack fluidStack) {
        if (fluidStack == null) {
            return false;
        }
        FluidStack oxygenGas = Materials.Oxygen.getGas(1L);
        return oxygenGas != null && fluidStack.isFluidEqual(oxygenGas);
    }

    private boolean depleteDinitrogenTetroxideForBoost() {
        int amountToConsume = DINITROGEN_TETROXIDE_BASE_CONSUMPTION_PER_TICK;
        dinitrogenTetroxideTickCounter++;
        if (dinitrogenTetroxideTickCounter >= DINITROGEN_TETROXIDE_EXTRA_CONSUMPTION_INTERVAL_TICKS) {
            amountToConsume++;
            dinitrogenTetroxideTickCounter = 0;
        }

        FluidStack dinitrogenTetroxideGas = Materials.DinitrogenTetroxide.getGas(amountToConsume);
        if (dinitrogenTetroxideGas != null && super.depleteInput(dinitrogenTetroxideGas)) {
            return true;
        }

        FluidStack dinitrogenTetroxideFluid = Materials.DinitrogenTetroxide.getFluid(amountToConsume);
        return dinitrogenTetroxideFluid != null && super.depleteInput(dinitrogenTetroxideFluid);
    }
}
