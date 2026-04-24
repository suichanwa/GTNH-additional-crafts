package gtnh_additional_crafts;

import net.minecraftforge.client.ClientCommandHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gtnh_additional_crafts.client.config.ConfigGuiEventHandler;
import gtnh_additional_crafts.client.config.ConfigHotkeyHandler;
import gtnh_additional_crafts.client.config.OpenConfigClientCommand;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    private ConfigHotkeyHandler configHotkeyHandler;

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ConfigHotkeyHandler.registerKeyBinding();
        configHotkeyHandler = new ConfigHotkeyHandler();
        FMLCommonHandler.instance()
            .bus()
            .register(new ConfigGuiEventHandler());
        FMLCommonHandler.instance()
            .bus()
            .register(configHotkeyHandler);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        ClientCommandHandler.instance.registerCommand(new OpenConfigClientCommand());
    }

}
