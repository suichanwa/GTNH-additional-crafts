package com.myname.mymodid.compat.gregtech;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.util.GTUtility;
import gtPlusPlus.xmod.gregtech.common.tileentities.generators.MTERocketFuelGenerator;

public class PatchedMTERocketFuelGenerator extends MTERocketFuelGenerator {

    private static final String NBT_KEY_OVERCLOCK = "OverclockMode";
    private static final double OVERCLOCK_OUTPUT_MULTIPLIER = 2.0D;
    private static final double OVERCLOCK_EFFICIENCY_MULTIPLIER = 0.8D;

    private boolean overclockMode = false;

    public PatchedMTERocketFuelGenerator(int id, String name, String nameRegional, int tier) {
        super(id, name, nameRegional, tier);
    }

    public PatchedMTERocketFuelGenerator(String name, int tier, String[] description,
        gregtech.api.interfaces.ITexture[][][] textures) {
        super(name, tier, description, textures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new PatchedMTERocketFuelGenerator(super.mName, super.mTier, super.mDescriptionArray, super.mTextures);
    }

    @Override
    public long maxEUOutput() {
        long base = super.maxEUOutput();
        if (!overclockMode) {
            return base;
        }
        return GTUtility.safeInt((long) Math.floor(base * OVERCLOCK_OUTPUT_MULTIPLIER));
    }

    @Override
    public int getEfficiency() {
        int base = super.getEfficiency();
        if (!overclockMode) {
            return base;
        }
        return Math.max(1, (int) Math.floor(base * OVERCLOCK_EFFICIENCY_MULTIPLIER));
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer player, float x, float y, float z,
        ItemStack tool) {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base != null && base.isClientSide()) {
            return;
        }

        overclockMode = !overclockMode;
        if (base != null) {
            base.issueTextureUpdate();
        }
        if (player != null) {
            GTUtility.sendChatToPlayer(
                player,
                overclockMode ? "Overclock enabled: 2x output, reduced fuel efficiency."
                    : "Overclock disabled: normal output and efficiency.");
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        nbt.setBoolean(NBT_KEY_OVERCLOCK, overclockMode);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        overclockMode = nbt.getBoolean(NBT_KEY_OVERCLOCK);
    }
}
