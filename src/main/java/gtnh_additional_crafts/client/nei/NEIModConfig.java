package gtnh_additional_crafts.client.nei;

import net.minecraftforge.common.MinecraftForge;

import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.event.NEIRegisterHandlerInfosEvent;
import codechicken.nei.recipe.GuiRecipeTab;
import codechicken.nei.recipe.HandlerInfo;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.Tags;
import gtnh_additional_crafts.machine.ModMachines;

@SideOnly(Side.CLIENT)
public class NEIModConfig implements IConfigureNEI {

    private static boolean isInitialized = false;

    public static synchronized void init() {
        if (isInitialized) {
            return;
        }
        isInitialized = true;
        try {
            new NEILiquidBoilerHandler().register();
            registerHandlerInfoDirect();
            MinecraftForge.EVENT_BUS.register(new NEIModConfig());
            MyMod.logInfo("Registered NEI Liquid Boiler handler successfully.");
        } catch (Throwable t) {
            MyMod.logInfo("Failed to register NEI Liquid Boiler handler: " + t.getMessage());
        }
    }

    private static void registerHandlerInfoDirect() {
        try {
            HandlerInfo infoOverlay = new HandlerInfo.Builder("liquid_boiler_fuels", "GTNH Additional Crafts", "gtnh_additional_crafts")
                .setDisplayStack(ModMachines.getSmallLiquidBoiler(1L))
                .setWidth(166)
                .setHeight(130)
                .setMaxRecipesPerPage(1)
                .setShowOverlayButton(false)
                .setShowFavoritesButton(true)
                .build();

            HandlerInfo infoClass = new HandlerInfo.Builder(NEILiquidBoilerHandler.class, "GTNH Additional Crafts", "gtnh_additional_crafts")
                .setDisplayStack(ModMachines.getSmallLiquidBoiler(1L))
                .setWidth(166)
                .setHeight(130)
                .setMaxRecipesPerPage(1)
                .setShowOverlayButton(false)
                .setShowFavoritesButton(true)
                .build();

            GuiRecipeTab.handlerMap.put("liquid_boiler_fuels", infoOverlay);
            GuiRecipeTab.handlerMap.put("Liquid Boiler Fuels", infoOverlay);
            GuiRecipeTab.handlerMap.put(NEILiquidBoilerHandler.class.getName(), infoClass);
        } catch (Throwable t) {
            MyMod.logInfo("Failed to directly register HandlerInfo: " + t.getMessage());
        }
    }

    @SubscribeEvent
    public void onRegisterHandlerInfos(NEIRegisterHandlerInfosEvent event) {
        try {
            HandlerInfo infoOverlay = new HandlerInfo.Builder("liquid_boiler_fuels", "GTNH Additional Crafts", "gtnh_additional_crafts")
                .setDisplayStack(ModMachines.getSmallLiquidBoiler(1L))
                .setWidth(166)
                .setHeight(130)
                .setMaxRecipesPerPage(1)
                .setShowOverlayButton(false)
                .setShowFavoritesButton(true)
                .build();

            HandlerInfo infoClass = new HandlerInfo.Builder(NEILiquidBoilerHandler.class, "GTNH Additional Crafts", "gtnh_additional_crafts")
                .setDisplayStack(ModMachines.getSmallLiquidBoiler(1L))
                .setWidth(166)
                .setHeight(130)
                .setMaxRecipesPerPage(1)
                .setShowOverlayButton(false)
                .setShowFavoritesButton(true)
                .build();

            event.registerHandlerInfo(infoOverlay);
            event.registerHandlerInfo(infoClass);
            GuiRecipeTab.handlerMap.put("Liquid Boiler Fuels", infoOverlay);
        } catch (Throwable t) {
            MyMod.logInfo("Failed to register NEIRegisterHandlerInfosEvent: " + t.getMessage());
        }
    }

    @Override
    public void loadConfig() {
        init();
    }

    @Override
    public String getName() {
        return "GTNH Additional Crafts NEI";
    }

    @Override
    public String getVersion() {
        return Tags.VERSION;
    }
}
