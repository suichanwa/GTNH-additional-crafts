package gtnh_additional_crafts.client.nei;

import codechicken.nei.api.IConfigureNEI;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.Tags;

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
            MyMod.logInfo("Registered NEI Liquid Boiler handler successfully.");
        } catch (Throwable t) {
            MyMod.logInfo("Failed to register NEI Liquid Boiler handler: " + t.getMessage());
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
