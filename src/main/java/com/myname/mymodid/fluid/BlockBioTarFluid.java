package com.myname.mymodid.fluid;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import com.myname.mymodid.MyMod;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBioTarFluid extends BlockFluidClassic {

    public BlockBioTarFluid(Fluid fluid) {
        super(fluid, Material.water);
        setBlockName("bio_tar");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        definedFluid.setIcons(
            iconRegister.registerIcon(MyMod.MODID + ":fluids/bio_tar_still"),
            iconRegister.registerIcon(MyMod.MODID + ":fluids/bio_tar_flow"));
    }
}
