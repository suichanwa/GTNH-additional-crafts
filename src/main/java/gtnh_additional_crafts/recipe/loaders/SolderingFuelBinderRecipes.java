package gtnh_additional_crafts.recipe.loaders;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.recipe.util.FluidLookup;
import gtnh_additional_crafts.recipe.util.ItemLookup;

public final class SolderingFuelBinderRecipes {

    private SolderingFuelBinderRecipes() {}

    public static void register() {
        registerSolderingAlloyIronAntimonyRecipe();
        registerSuperFuelBinderBeeswaxRecipes();
        registerSuperFuelBinderCreosoteBeeswaxRecipe();
        registerBeeswaxFurnaceFuel();
        registerMagicSuperFuelBinderVoidMetalRecipes();
    }

    private static final int BEESWAX_FURNACE_BURN_TICKS = 500;

    public static void registerSolderingAlloyIronAntimonyRecipe() {
        ItemStack ironIngots = GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Iron, 7L);
        ItemStack antimonyIngots = GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Antimony, 3L);
        ItemStack solderingAlloyIngots = GTOreDictUnificator.get(OrePrefixes.ingot, Materials.SolderingAlloy, 9L);

        if (ironIngots == null || antimonyIngots == null || solderingAlloyIngots == null) {
            MyMod.logInfo(
                "Skipped Alloy Smelter soldering alloy recipe: iron, antimony, or soldering alloy ingot stack is unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(ironIngots, antimonyIngots)
            .itemOutputs(solderingAlloyIngots)
            .duration(16 * GTRecipeBuilder.SECONDS)
            .eut(120)
            .addTo(RecipeMaps.alloySmelterRecipes);

        MyMod
            .logInfo("Registered Alloy Smelter recipe: 7x Iron Ingot + 3x Antimony Ingot -> 9x Soldering Alloy Ingot.");
    }

    public static void registerSuperFuelBinderBeeswaxRecipes() {
        ItemStack beeswax = resolveForestryBeeswax(8);
        FluidStack advancedGlue = Materials.AdvancedGlue.getFluid(100L);

        if (beeswax == null || advancedGlue == null) {
            MyMod.logInfo(
                "Skipped beeswax Super Fuel Binder recipes: Forestry beeswax or Advanced Glue is unavailable.");
            return;
        }

        registerSuperFuelBinderBeeswaxRecipe(beeswax, Materials.Sodium, 4);
        registerSuperFuelBinderBeeswaxRecipe(beeswax, Materials.Lithium, 8);
        registerSuperFuelBinderBeeswaxRecipe(beeswax, Materials.Caesium, 12);

        MyMod.logInfo(
            "Registered Mixer recipes: 8x Forestry Beeswax + 100L Advanced Glue alternate Super Fuel Binder route.");
    }

    public static void registerSuperFuelBinderCreosoteBeeswaxRecipe() {
        ItemStack sulfurDust = GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1L);
        ItemStack woodDust = GTOreDictUnificator.get(OrePrefixes.dust, Materials.Wood, 4L);
        ItemStack beeswax = resolveForestryBeeswax(16);
        ItemStack output = ItemList.SFMixture.get(6);
        FluidStack creosote = FluidLookup
            .getFirstAvailableFluid(2000, "creosote", "creosoteoil", "Creosote", "Creosote Oil");

        if (sulfurDust == null || woodDust == null || beeswax == null || output == null || creosote == null) {
            MyMod.logInfo(
                "Skipped creosote beeswax Super Fuel Binder recipe: sulfur, wood dust, Forestry beeswax, creosote, or output is unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(sulfurDust, woodDust, beeswax, GTUtility.getIntegratedCircuit(3))
            .itemOutputs(output)
            .fluidInputs(creosote)
            .duration(40 * GTRecipeBuilder.SECONDS)
            .eut(16)
            .addTo(RecipeMaps.mixerRecipes);

        MyMod.logInfo("Registered Mixer recipe: 16x Forestry Beeswax + 2000L Creosote -> 6x Super Fuel Binder.");
    }

    public static void registerBeeswaxFurnaceFuel() {
        if (OreDictionary.getOres("itemBeeswax")
            .isEmpty()) {
            MyMod.logInfo("Skipped Beeswax furnace fuel: no itemBeeswax ore dictionary entries found.");
            return;
        }

        GameRegistry.registerFuelHandler(new IFuelHandler() {

            @Override
            public int getBurnTime(ItemStack fuel) {
                if (fuel == null) {
                    return 0;
                }
                for (ItemStack ore : OreDictionary.getOres("itemBeeswax")) {
                    if (OreDictionary.itemMatches(ore, fuel, false)) {
                        return BEESWAX_FURNACE_BURN_TICKS;
                    }
                }
                return 0;
            }
        });

        MyMod.logInfo(
            "Registered Beeswax as a furnace fuel: burns for " + BEESWAX_FURNACE_BURN_TICKS
                + " ticks ("
                + (BEESWAX_FURNACE_BURN_TICKS / 20)
                + "s), any itemBeeswax ore-dict entry.");
    }

    public static void registerMagicSuperFuelBinderVoidMetalRecipes() {
        FluidStack moltenVoidMetal = Materials.Void.getMolten(36L);
        if (moltenVoidMetal == null) {
            MyMod.logInfo("Skipped Void Metal Magic Super Fuel Binder recipes: molten Void Metal is unavailable.");
            return;
        }

        registerMagicSuperFuelBinderVoidMetalRecipe(Materials.InfusedAir);
        registerMagicSuperFuelBinderVoidMetalRecipe(Materials.InfusedEarth);
        registerMagicSuperFuelBinderVoidMetalRecipe(Materials.InfusedEntropy);
        registerMagicSuperFuelBinderVoidMetalRecipe(Materials.InfusedFire);
        registerMagicSuperFuelBinderVoidMetalRecipe(Materials.InfusedOrder);
        registerMagicSuperFuelBinderVoidMetalRecipe(Materials.InfusedWater);

        MyMod.logInfo(
            "Registered Mixer recipes: 24x Super Fuel Binder + infused Thaumcraft material + 36 mB Void Metal -> 24x Magic Super Fuel Binder.");
    }

    private static void registerSuperFuelBinderBeeswaxRecipe(ItemStack beeswax, Materials metal, int outputAmount) {
        ItemStack sulfurDust = GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1L);
        ItemStack metalDust = GTOreDictUnificator.get(OrePrefixes.dust, metal, 1L);
        ItemStack woodDust = GTOreDictUnificator.get(OrePrefixes.dust, Materials.Wood, 4L);
        ItemStack output = ItemList.SFMixture.get(outputAmount);
        FluidStack advancedGlue = Materials.AdvancedGlue.getFluid(100L);

        if (sulfurDust == null || metalDust == null || woodDust == null || output == null || advancedGlue == null) {
            MyMod.logInfo(
                "Skipped beeswax Super Fuel Binder recipe for " + metal + ": required input or output is unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(sulfurDust, metalDust, woodDust, beeswax.copy(), GTUtility.getIntegratedCircuit(2))
            .itemOutputs(output)
            .fluidInputs(advancedGlue)
            .duration(40 * GTRecipeBuilder.SECONDS)
            .eut(16)
            .addTo(RecipeMaps.mixerRecipes);
    }

    private static void registerMagicSuperFuelBinderVoidMetalRecipe(Materials infusedMaterial) {
        ItemStack superFuelBinder = ItemList.SFMixture.get(24);
        ItemStack infusedDust = GTOreDictUnificator.get(OrePrefixes.dust, infusedMaterial, 1L);
        ItemStack magicSuperFuelBinder = ItemList.MSFMixture.get(24);
        FluidStack moltenVoidMetal = Materials.Void.getMolten(36L);

        if (superFuelBinder == null || infusedDust == null || magicSuperFuelBinder == null || moltenVoidMetal == null) {
            MyMod.logInfo(
                "Skipped Void Metal Magic Super Fuel Binder recipe for " + infusedMaterial
                    + ": required input or output is unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(superFuelBinder, infusedDust, GTUtility.getIntegratedCircuit(1))
            .itemOutputs(magicSuperFuelBinder)
            .fluidInputs(moltenVoidMetal)
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(64)
            .addTo(RecipeMaps.mixerRecipes);
    }

    private static ItemStack resolveForestryBeeswax(int amount) {
        Item beeswax = ItemLookup.findItem("Forestry", "beeswax");
        if (beeswax == null) {
            beeswax = ItemLookup.findItem("forestry", "beeswax");
        }
        if (beeswax != null) {
            return new ItemStack(beeswax, amount, 0);
        }
        return ItemLookup.resolveFirstOreDictStack(amount, "itemBeeswax");
    }

}
