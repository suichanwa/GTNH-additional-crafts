package gtnh_additional_crafts;

import net.minecraft.item.Item;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import gtnh_additional_crafts.fluid.ModFluids;
import gtnh_additional_crafts.network.BootsControlMessage;

@Mod(
    modid = MyMod.MODID,
    version = Tags.VERSION,
    name = "gtnh_additional_crafts",
    acceptedMinecraftVersions = "[1.7.10]",
    guiFactory = "gtnh_additional_crafts.client.config.ModGuiFactory")
public class MyMod {

    public static final String MODID = "gtnh_additional_crafts";
    private static final String LEGACY_MODID = "mymodid";
    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @SidedProxy(clientSide = "gtnh_additional_crafts.ClientProxy", serverSide = "gtnh_additional_crafts.CommonProxy")
    public static CommonProxy proxy;

    public static void logInfo(String message) {
        FMLLog.info("[%s] %s", MODID, message);
    }

    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        NETWORK.registerMessage(BootsControlMessage.Handler.class, BootsControlMessage.class, 0, Side.SERVER);
        proxy.preInit(event);
    }

    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    @Mod.EventHandler
    public void missingMappings(FMLMissingMappingsEvent event) {
        if (ModFluids.bioTarBlock == null) {
            return;
        }
        Item bioTarItem = Item.getItemFromBlock(ModFluids.bioTarBlock);
        String legacyBioTarId = LEGACY_MODID + ":bio_tar";

        for (FMLMissingMappingsEvent.MissingMapping missing : event.getAll()) {
            if (!legacyBioTarId.equals(missing.name)) {
                continue;
            }
            if (missing.type == GameRegistry.Type.BLOCK) {
                missing.remap(ModFluids.bioTarBlock);
            } else if (missing.type == GameRegistry.Type.ITEM && bioTarItem != null) {
                missing.remap(bioTarItem);
            }
        }
    }
}
