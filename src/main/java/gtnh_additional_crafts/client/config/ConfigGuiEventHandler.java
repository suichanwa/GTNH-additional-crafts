package gtnh_additional_crafts.client.config;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import gtnh_additional_crafts.Config;
import gtnh_additional_crafts.MyMod;

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
