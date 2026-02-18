package com.myname.mymodid;

import com.myname.mymodid.compat.gregtech.DieselEngineOxidizerPatch;
import com.myname.mymodid.compat.kekztech.SOFCHeliumPatch;
import com.myname.mymodid.fluid.ModFluids;
import com.myname.mymodid.recipe.GregTechRecipeLoader;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());

        MyMod.logInfo(Config.greeting);
        MyMod.logInfo("I am MyMod at version " + Tags.VERSION);

        ModFluids.registerFluids();
        DieselEngineOxidizerPatch.schedule();
        SOFCHeliumPatch.schedule();
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        GregTechRecipeLoader.registerRecipes();
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {}

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {}
}
