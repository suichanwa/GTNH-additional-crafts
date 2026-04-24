package com.myname.mymodid.client.config;

import com.myname.mymodid.Config;
import com.myname.mymodid.MyMod;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ConfigGuiEventHandler {

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (!MyMod.MODID.equals(event.modID)) {
            return;
        }
        Config.onConfigGuiChanged();
        ThaumicBootsConfigManager.saveIfChanged();
    }
}
