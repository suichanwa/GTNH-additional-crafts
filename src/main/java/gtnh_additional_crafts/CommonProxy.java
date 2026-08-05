package gtnh_additional_crafts;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import gtnh_additional_crafts.fluid.ModFluids;
import gtnh_additional_crafts.item.ModItems;
import gtnh_additional_crafts.patches.gregtech.DieselEngineOxidizerPatch;
import gtnh_additional_crafts.patches.gregtech.LargeSemifluidGeneratorPatch;
import gtnh_additional_crafts.patches.gregtech.MolecularTransformerEnergyHatchPatch;
import gtnh_additional_crafts.patches.gregtech.RocketFuelGeneratorPatch;
import gtnh_additional_crafts.patches.gregtech.VacuumFreezerNitrogenPatch;
import gtnh_additional_crafts.patches.kekztech.SOFCHeliumPatch;
import gtnh_additional_crafts.patches.thaumicboots.ThaumicBootsRuntimeEventHandlerPatch;
import gtnh_additional_crafts.patches.thaumictinkerer.BottomlessPouchGuiHandlerPatch;
import gtnh_additional_crafts.recipe.RecipeLoader;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        ModItems.registerItems();
        ModFluids.registerFluids();

        MyMod.logInfo(Config.greeting);
        MyMod.logInfo("I am MyMod at version " + Tags.VERSION);

        DieselEngineOxidizerPatch.schedule();
        LargeSemifluidGeneratorPatch.schedule();
        if (Config.mtAllowMultipleEnergyHatches) {
            MolecularTransformerEnergyHatchPatch.schedule();
        } else {
            MyMod.logInfo("Molecular Transformer multi-energy-hatch patch is disabled in config.");
        }
        RocketFuelGeneratorPatch.schedule();
        VacuumFreezerNitrogenPatch.schedule();
        SOFCHeliumPatch.schedule();
        FMLCommonHandler.instance()
            .bus()
            .register(new ThaumicBootsRuntimeEventHandlerPatch());
    }

    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(MyMod.instance, new BottomlessPouchGuiHandlerPatch());
        RecipeLoader.registerRecipes();
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        LargeSemifluidGeneratorPatch.applyIfNeeded();
        MolecularTransformerEnergyHatchPatch.applyIfNeeded();
        RocketFuelGeneratorPatch.applyIfNeeded();
    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {}
}
