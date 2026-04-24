package com.myname.mymodid.client.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ConfigHotkeyHandler {

    private static final KeyBinding OPEN_CONFIG_KEY = new KeyBinding(
        "key.mymodid.open_tb_config",
        Keyboard.KEY_B,
        "key.categories.mymodid");

    public static void registerKeyBinding() {
        ClientRegistry.registerKeyBinding(OPEN_CONFIG_KEY);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null || minecraft.thePlayer == null || minecraft.currentScreen != null) {
            return;
        }
        while (OPEN_CONFIG_KEY.isPressed()) {
            InGameConfigOpener.open();
        }
    }
}
