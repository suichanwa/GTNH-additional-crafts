package com.myname.mymodid.compat.gregtech;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.common.tileentities.machines.multi.MTEVacuumFreezer;

public class PatchedMTEVacuumFreezer extends MTEVacuumFreezer {

    private static final int NITROGEN_CONSUMPTION_PER_SECOND = 250;
    private static final int TICKS_PER_SECOND = 20;
    private static final double SPEED_MULTIPLIER_WITH_NITROGEN = 0.92D;

    private boolean nitrogenBoostActive = false;
    private int nitrogenTickCounter = 0;

    public PatchedMTEVacuumFreezer(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    public PatchedMTEVacuumFreezer(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new PatchedMTEVacuumFreezer(super.mName);
    }

    @Override
    public CheckRecipeResult checkProcessing() {
        resetNitrogenBoostState();
        CheckRecipeResult result = super.checkProcessing();
        if (!result.wasSuccessful()) {
            return result;
        }

        if (consumeNitrogenSecond()) {
            nitrogenBoostActive = true;
            nitrogenTickCounter = 0;
            mMaxProgresstime = Math.max(1, (int) Math.floor(mMaxProgresstime * SPEED_MULTIPLIER_WITH_NITROGEN));
        }

        return result;
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        boolean running = super.onRunningTick(stack);
        if (!running || !nitrogenBoostActive || mMaxProgresstime <= 0) {
            return running;
        }

        nitrogenTickCounter++;
        if (nitrogenTickCounter >= TICKS_PER_SECOND) {
            nitrogenTickCounter = 0;
            if (!consumeNitrogenSecond()) {
                disableNitrogenBoostForCurrentRecipe();
            }
        }

        return true;
    }

    private void disableNitrogenBoostForCurrentRecipe() {
        if (!nitrogenBoostActive || mMaxProgresstime <= mProgresstime) {
            nitrogenBoostActive = false;
            return;
        }

        int remaining = mMaxProgresstime - mProgresstime;
        int revertedRemaining = Math.max(1, (int) Math.ceil(remaining / SPEED_MULTIPLIER_WITH_NITROGEN));
        mMaxProgresstime = mProgresstime + revertedRemaining;
        nitrogenBoostActive = false;
    }

    private void resetNitrogenBoostState() {
        nitrogenBoostActive = false;
        nitrogenTickCounter = 0;
    }

    private boolean consumeNitrogenSecond() {
        FluidStack nitrogenGas = Materials.Nitrogen.getGas(NITROGEN_CONSUMPTION_PER_SECOND);
        if (nitrogenGas != null && super.depleteInput(nitrogenGas)) {
            return true;
        }

        FluidStack nitrogenFluid = Materials.Nitrogen.getFluid(NITROGEN_CONSUMPTION_PER_SECOND);
        return nitrogenFluid != null && super.depleteInput(nitrogenFluid);
    }
}
