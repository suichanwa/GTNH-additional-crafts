package com.myname.mymodid;

import com.myname.mymodid.compat.gregtech.DieselEngineOxidizerPatch;
import com.myname.mymodid.compat.gregtech.RocketFuelGeneratorPatch;
import com.myname.mymodid.compat.gregtech.VacuumFreezerNitrogenPatch;
import com.myname.mymodid.compat.kekztech.SOFCHeliumPatch;
import com.myname.mymodid.recipe.GregTechRecipeLoader;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());

        MyMod.logInfo(Config.greeting);
        MyMod.logInfo("I am MyMod at version " + Tags.VERSION);

        DieselEngineOxidizerPatch.schedule();
        RocketFuelGeneratorPatch.schedule();
        VacuumFreezerNitrogenPatch.schedule();
        SOFCHeliumPatch.schedule();
    }

    public void init(FMLInitializationEvent event) {
        GregTechRecipeLoader.registerRecipes();
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        RocketFuelGeneratorPatch.applyIfNeeded();
    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {}
}
