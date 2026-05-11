package gtnh_additional_crafts.client.keybind;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.network.OpenBottomlessPouchMessage;

public class BottomlessPouchHotkeyHandler {

    private static final KeyBinding OPEN_POUCH_KEY = new KeyBinding(
        "key.gtnh_additional_crafts.open_bottomless_pouch",
        Keyboard.KEY_P,
        "key.categories.gtnh_additional_crafts");

    public static void registerKeyBinding() {
        ClientRegistry.registerKeyBinding(OPEN_POUCH_KEY);
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
        while (OPEN_POUCH_KEY.isPressed()) {
            MyMod.NETWORK.sendToServer(new OpenBottomlessPouchMessage());
        }
    }
}
