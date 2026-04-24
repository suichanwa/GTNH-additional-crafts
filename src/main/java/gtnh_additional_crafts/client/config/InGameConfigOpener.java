package gtnh_additional_crafts.client.config;

import net.minecraft.client.Minecraft;

public final class InGameConfigOpener {

    private InGameConfigOpener() {}

    public static void open() {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null) {
            return;
        }
        minecraft.displayGuiScreen(new GuiThaumicBootsControls());
    }
}
