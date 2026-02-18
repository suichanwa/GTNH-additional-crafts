package com.myname.mymodid.fluid;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FluidTextureHandler {

    @SubscribeEvent
    public void onTextureStitchPre(TextureStitchEvent.Pre event) {
        if (event.map == null || event.map.getTextureType() != 0) {
            return;
        }

        Fluid fluid = ModFluids.getDinitrogenPentoxideFluid();
        if (fluid == null) {
            return;
        }

        fluid.setIcons(
            event.map.registerIcon(ModFluids.DINITROGEN_PENTOXIDE_STILL_ICON),
            event.map.registerIcon(ModFluids.DINITROGEN_PENTOXIDE_FLOW_ICON));
    }
}
