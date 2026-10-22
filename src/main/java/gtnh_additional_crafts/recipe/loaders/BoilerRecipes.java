package gtnh_additional_crafts.recipe.loaders;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OreDictNames;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.machine.ModMachines;

public final class BoilerRecipes {

    private BoilerRecipes() {}

    public static void register() {
        registerSmallLiquidBoilerRecipes();
        registerLargeLiquidBoilerRecipes();
    }

    private static void registerSmallLiquidBoilerRecipes() {
        ItemStack smallLiquidBoiler = ModMachines.getSmallLiquidBoiler(1L);
        if (smallLiquidBoiler == null) {
            MyMod.logInfo("Skipped Small Liquid Boiler recipes: machine stack is null.");
            return;
        }

        // Recipe 1: Upgrade from Small Coal Boiler
        ItemStack smallCoalBoiler = ItemList.Machine_Bronze_Boiler.get(1L);
        if (smallCoalBoiler != null) {
            GTModHandler.addCraftingRecipe(
                smallLiquidBoiler,
                GTModHandler.RecipeBits.BITS | GTModHandler.RecipeBits.DO_NOT_CHECK_FOR_COLLISIONS,
                new Object[] { "PGP", "IwI", "BSB", 'P', OrePrefixes.plate.get(Materials.Bronze), 'G',
                    new ItemStack(Blocks.glass, 1), 'I', OrePrefixes.pipeMedium.get(Materials.Bronze), 'B',
                    new ItemStack(Blocks.brick_block, 1), 'S', smallCoalBoiler });
        }

        // Recipe 2: Direct crafting with furnace
        GTModHandler.addCraftingRecipe(
            smallLiquidBoiler,
            GTModHandler.RecipeBits.BITS | GTModHandler.RecipeBits.DO_NOT_CHECK_FOR_COLLISIONS,
            new Object[] { "PGP", "IwI", "BFB", 'P', OrePrefixes.plate.get(Materials.Bronze), 'G',
                new ItemStack(Blocks.glass, 1), 'I', OrePrefixes.pipeMedium.get(Materials.Bronze), 'B',
                new ItemStack(Blocks.brick_block, 1), 'F', OreDictNames.craftingFurnace });

        MyMod.logInfo("Registered Small Liquid Boiler recipes.");
    }

    private static void registerLargeLiquidBoilerRecipes() {
        ItemStack largeLiquidBoiler = ModMachines.getLargeLiquidBoiler(1L);
        if (largeLiquidBoiler == null) {
            MyMod.logInfo("Skipped High Pressure Liquid Boiler recipes: machine stack is null.");
            return;
        }

        // Recipe 1: Upgrade from Small Liquid Boiler
        ItemStack smallLiquidBoiler = ModMachines.getSmallLiquidBoiler(1L);
        if (smallLiquidBoiler != null) {
            GTModHandler.addCraftingRecipe(
                largeLiquidBoiler,
                GTModHandler.RecipeBits.BITS | GTModHandler.RecipeBits.DO_NOT_CHECK_FOR_COLLISIONS,
                new Object[] { "PIP", "IBI", "KwK", 'P', OrePrefixes.plate.get(Materials.Steel), 'I',
                    OrePrefixes.pipeMedium.get(Materials.Steel), 'B', smallLiquidBoiler, 'K',
                    new ItemStack(Blocks.brick_block, 1) });
        }

        // Recipe 2: Upgrade from High Pressure Coal Boiler
        ItemStack hpCoalBoiler = ItemList.Machine_Steel_Boiler.get(1L);
        if (hpCoalBoiler != null) {
            GTModHandler.addCraftingRecipe(
                largeLiquidBoiler,
                GTModHandler.RecipeBits.BITS | GTModHandler.RecipeBits.DO_NOT_CHECK_FOR_COLLISIONS,
                new Object[] { "PIP", "ISI", "KwK", 'P', OrePrefixes.plate.get(Materials.Steel), 'I',
                    OrePrefixes.pipeMedium.get(Materials.Steel), 'S', hpCoalBoiler, 'K',
                    new ItemStack(Blocks.brick_block, 1) });
        }

        MyMod.logInfo("Registered High Pressure Liquid Boiler recipes.");
    }
}
