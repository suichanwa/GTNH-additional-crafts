package com.myname.mymodid.fluid;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.registry.GameRegistry;

public final class ModFluids {

    public static Fluid bioTar;
    public static BlockBioTarFluid bioTarBlock;

    private ModFluids() {}

    public static void registerFluids() {
        Fluid existingBioTar = FluidRegistry.getFluid("bio_tar");
        if (existingBioTar != null) {
            bioTar = existingBioTar;
            return;
        }

        bioTar = new Fluid("bio_tar").setUnlocalizedName("bio_tar")
            .setDensity(1250)
            .setTemperature(330)
            .setViscosity(2400);
        FluidRegistry.registerFluid(bioTar);

        bioTarBlock = new BlockBioTarFluid(bioTar);
        GameRegistry.registerBlock(bioTarBlock, "bio_tar");
    }

    public static FluidStack getBioTar(int amount) {
        Fluid fluid = FluidRegistry.getFluid("bio_tar");
        return fluid == null ? null : new FluidStack(fluid, amount);
    }
}
