package com.myname.mymodid.fluid;

import java.lang.reflect.Method;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FluidTextureHandler {

    @SubscribeEvent
    public void onTextureStitchPre(TextureStitchEvent.Pre event) {
        if (event.map == null || !isBlockAtlas(event.map)) {
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

    private boolean isBlockAtlas(Object textureMap) {
        Integer textureType = invokeTextureType(textureMap, "getTextureType");
        if (textureType == null) {
            textureType = invokeTextureType(textureMap, "func_130086_a");
        }
        return textureType == null || textureType == 0;
    }

    private Integer invokeTextureType(Object textureMap, String methodName) {
        try {
            Method m = textureMap.getClass()
                .getMethod(methodName);
            Object result = m.invoke(textureMap);
            return result instanceof Integer ? (Integer) result : null;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}
