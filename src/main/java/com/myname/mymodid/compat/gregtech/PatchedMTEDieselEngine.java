package com.myname.mymodid.compat.gregtech;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.tileentities.machines.multi.MTEDieselEngine;

public class PatchedMTEDieselEngine extends MTEDieselEngine {

    private static final int DEFAULT_MAX_EFFICIENCY = 10000;
    private static final int OXYGEN_BOOSTED_MAX_EFFICIENCY = 30000;
    private static final int DINITROGEN_TETROXIDE_BOOSTED_MAX_EFFICIENCY = 40000;
    private static final int OVERCLOCKED_MAX_EFFICIENCY_NUMERATOR = 4;
    private static final int OVERCLOCKED_MAX_EFFICIENCY_DENOMINATOR = 5;

    private static final int OVERCLOCKED_NOMINAL_OUTPUT = 5120;

    private static final int DINITROGEN_TETROXIDE_CONSUMPTION_PER_TICK = 1;
    private static final String NBT_KEY_OVERCLOCK_MODE = "OverclockMode";

    private boolean boostedByDinitrogenTetroxide = false;
    private boolean overclockMode = false;

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
            .addInfo("Supply 40L/s Oxygen or 20L/s Dinitrogen Tetroxide to boost output (optional)")
            .addInfo("Default: Produces 2048EU/t at 100% fuel efficiency")
            .addInfo("Boosted: Produces 6144EU/t at 150% fuel efficiency")
            .addInfo("N2O4 boost can reach 400% max efficiency (up to 8192EU/t)")
            .addInfo("Screwdriver right-click toggles overclock mode (2x output, lower fuel efficiency)")
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
    protected int getNominalOutput() {
        if (overclockMode) {
            return OVERCLOCKED_NOMINAL_OUTPUT;
        }
        return super.getNominalOutput();
    }

    @Override
    public int getMaxEfficiency(ItemStack itemStack) {
        int maxEfficiency;
        if (!super.boostEu) {
            maxEfficiency = DEFAULT_MAX_EFFICIENCY;
        } else if (boostedByDinitrogenTetroxide) {
            maxEfficiency = DINITROGEN_TETROXIDE_BOOSTED_MAX_EFFICIENCY;
        } else {
            maxEfficiency = OXYGEN_BOOSTED_MAX_EFFICIENCY;
        }
        return applyOverclockEfficiencyPenalty(maxEfficiency);
    }

    @Override
    public void onScrewdriverRightClick(
        ForgeDirection side,
        EntityPlayer player,
        float x,
        float y,
        float z,
        ItemStack tool) {
        IGregTechTileEntity baseMetaTileEntity = getBaseMetaTileEntity();
        if (baseMetaTileEntity != null && baseMetaTileEntity.isClientSide()) {
            return;
        }

        overclockMode = !overclockMode;

        if (baseMetaTileEntity != null) {
            baseMetaTileEntity.issueTextureUpdate();
        }

        if (player != null) {
            GTUtility.sendChatToPlayer(
                player,
                overclockMode ? "Overclock mode enabled: 2x output, lower fuel efficiency."
                    : "Overclock mode disabled: normal output and fuel efficiency.");
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        nbt.setBoolean(NBT_KEY_OVERCLOCK_MODE, overclockMode);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        overclockMode = nbt.getBoolean(NBT_KEY_OVERCLOCK_MODE);
    }

    private boolean isOxygenRequest(FluidStack fluidStack) {
        if (fluidStack == null) {
            return false;
        }
        FluidStack oxygenGas = Materials.Oxygen.getGas(1L);
        return oxygenGas != null && fluidStack.isFluidEqual(oxygenGas);
    }

    private boolean depleteDinitrogenTetroxideForBoost() {
        FluidStack dinitrogenTetroxideGas = Materials.DinitrogenTetroxide
            .getGas(DINITROGEN_TETROXIDE_CONSUMPTION_PER_TICK);
        if (dinitrogenTetroxideGas != null && super.depleteInput(dinitrogenTetroxideGas)) {
            return true;
        }

        FluidStack dinitrogenTetroxideFluid = Materials.DinitrogenTetroxide
            .getFluid(DINITROGEN_TETROXIDE_CONSUMPTION_PER_TICK);
        return dinitrogenTetroxideFluid != null && super.depleteInput(dinitrogenTetroxideFluid);
    }

    private int applyOverclockEfficiencyPenalty(int maxEfficiency) {
        if (!overclockMode) {
            return maxEfficiency;
        }
        return maxEfficiency * OVERCLOCKED_MAX_EFFICIENCY_NUMERATOR / OVERCLOCKED_MAX_EFFICIENCY_DENOMINATOR;
    }
}
