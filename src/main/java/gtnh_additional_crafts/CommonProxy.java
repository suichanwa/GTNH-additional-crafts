package gtnh_additional_crafts;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import gtnh_additional_crafts.compat.gregtech.DieselEngineOxidizerPatch;
import gtnh_additional_crafts.compat.gregtech.LargeSemifluidGeneratorPatch;
import gtnh_additional_crafts.compat.gregtech.RocketFuelGeneratorPatch;
import gtnh_additional_crafts.compat.gregtech.VacuumFreezerNitrogenPatch;
import gtnh_additional_crafts.compat.kekztech.SOFCHeliumPatch;
import gtnh_additional_crafts.compat.thaumicboots.ThaumicBootsRuntimeEventHandler;
import gtnh_additional_crafts.fluid.ModFluids;
import gtnh_additional_crafts.item.ModItems;
import gtnh_additional_crafts.recipe.GregTechRecipeLoader;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        ModItems.registerItems();
        ModFluids.registerFluids();

        MyMod.logInfo(Config.greeting);
        MyMod.logInfo("I am MyMod at version " + Tags.VERSION);

        DieselEngineOxidizerPatch.schedule();
        LargeSemifluidGeneratorPatch.schedule();
        RocketFuelGeneratorPatch.schedule();
        VacuumFreezerNitrogenPatch.schedule();
        SOFCHeliumPatch.schedule();
        FMLCommonHandler.instance()
            .bus()
            .register(new ThaumicBootsRuntimeEventHandler());
    }

    public void init(FMLInitializationEvent event) {
        GregTechRecipeLoader.registerRecipes();
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        LargeSemifluidGeneratorPatch.applyIfNeeded();
        RocketFuelGeneratorPatch.applyIfNeeded();
    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {}
}
