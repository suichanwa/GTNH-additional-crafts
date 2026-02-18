package com.myname.mymodid.fluid;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.myname.mymodid.MyMod;

public final class ModFluids {

    public static final String DINITROGEN_PENTOXIDE_FLUID_NAME = "dinitrogenpentoxide";
    public static final String DINITROGEN_PENTOXIDE_STILL_ICON = "mymodid:fluids/dinitrogen_pentoxide_still";
    public static final String DINITROGEN_PENTOXIDE_FLOW_ICON = "mymodid:fluids/dinitrogen_pentoxide_flow";

    private static Fluid dinitrogenPentoxide;

    private ModFluids() {}

    public static void registerFluids() {
        Fluid existing = FluidRegistry.getFluid(DINITROGEN_PENTOXIDE_FLUID_NAME);
        if (existing != null) {
            dinitrogenPentoxide = existing;
            MyMod.logInfo("Using existing fluid registration for Dinitrogen Pentoxide.");
            return;
        }

        Fluid created = new Fluid(DINITROGEN_PENTOXIDE_FLUID_NAME).setDensity(1700)
            .setViscosity(1200)
            .setTemperature(298)
            .setGaseous(false);

        if (FluidRegistry.registerFluid(created)) {
            dinitrogenPentoxide = created;
            MyMod.logInfo("Registered custom fluid: Dinitrogen Pentoxide.");
        } else {
            dinitrogenPentoxide = FluidRegistry.getFluid(DINITROGEN_PENTOXIDE_FLUID_NAME);
            MyMod.logInfo("Failed to register custom fluid directly, using existing Dinitrogen Pentoxide entry.");
        }
    }

    public static FluidStack getDinitrogenPentoxide(long amount) {
        if (dinitrogenPentoxide == null) {
            Fluid existing = FluidRegistry.getFluid(DINITROGEN_PENTOXIDE_FLUID_NAME);
            if (existing != null) {
                dinitrogenPentoxide = existing;
            }
        }
        if (dinitrogenPentoxide == null) {
            return null;
        }
        return new FluidStack(dinitrogenPentoxide, (int) amount);
    }

    public static Fluid getDinitrogenPentoxideFluid() {
        if (dinitrogenPentoxide == null) {
            dinitrogenPentoxide = FluidRegistry.getFluid(DINITROGEN_PENTOXIDE_FLUID_NAME);
        }
        return dinitrogenPentoxide;
    }
}
