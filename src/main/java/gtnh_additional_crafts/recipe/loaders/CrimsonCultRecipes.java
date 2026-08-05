package gtnh_additional_crafts.recipe.loaders;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.recipe.util.ItemLookup;

public final class CrimsonCultRecipes {

    private CrimsonCultRecipes() {}

    public static void register() {
        registerCrimsonCultArmorSalvageRecipes();
    }

    public static void registerCrimsonCultArmorSalvageRecipes() {
        registerCrimsonCultMaceratorRecipe(
            "Crimson Cult Hood",
            resolveCrimsonCultArmor("ItemHelmetCultistRobe"),
            2,
            3000,
            1,
            150,
            800);

        registerCrimsonCultMaceratorRecipe(
            "Crimson Cult Robe",
            resolveCrimsonCultArmor("ItemChestplateCultistRobe"),
            5,
            4000,
            2,
            300,
            1200);

        registerCrimsonCultMaceratorRecipe(
            "Crimson Cult Leggings",
            resolveCrimsonCultArmor("ItemLeggingsCultistRobe"),
            4,
            3500,
            2,
            200,
            1000);

        registerCrimsonCultMaceratorRecipe(
            "Crimson Cult Helm",
            resolveCrimsonCultArmor("ItemHelmetCultistPlate"),
            3,
            3000,
            1,
            150,
            800);

        registerCrimsonCultMaceratorRecipe(
            "Crimson Cult Chestplate",
            resolveCrimsonCultArmor("ItemChestplateCultistPlate"),
            6,
            4000,
            2,
            300,
            1200);

        registerCrimsonCultMaceratorRecipe(
            "Crimson Cult Greaves",
            resolveCrimsonCultArmor("ItemLeggingsCultistPlate"),
            5,
            3500,
            2,
            200,
            1000);

        registerCrimsonCultMaceratorRecipe(
            "Crimson Cult Boots",
            resolveCrimsonCultArmor("ItemBootsCultist"),
            2,
            3000,
            1,
            150,
            800);
    }

    private static void registerCrimsonCultMaceratorRecipe(String armorName, ItemStack armorStack, int shadowNuggets,
        int thaumiumIngotChance, int thaumiumIngots, int voidMetalNuggetChance, int enchantedFabricChance) {
        if (armorStack == null) {
            MyMod.logInfo("Skipped " + armorName + " salvage recipe: source armor item is unavailable.");
            return;
        }

        ItemStack shadowNuggetStack = resolveShadowMetalNuggets(shadowNuggets);
        ItemStack thaumiumIngotStack = GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Thaumium, thaumiumIngots);
        ItemStack voidMetalNuggetStack = ItemLookup.resolveVoidMetalNuggets(1);
        ItemStack enchantedFabricStack = resolveEnchantedFabric();

        if (shadowNuggetStack == null || thaumiumIngotStack == null
            || voidMetalNuggetStack == null
            || enchantedFabricStack == null) {
            MyMod.logInfo("Skipped " + armorName + " salvage recipe: required GT/Thaumcraft outputs are unavailable.");
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputs(armorStack.copy())
            .itemOutputs(shadowNuggetStack, thaumiumIngotStack, voidMetalNuggetStack, enchantedFabricStack)
            .outputChances(
                10000,
                clampChance(thaumiumIngotChance),
                clampChance(voidMetalNuggetChance),
                clampChance(enchantedFabricChance))
            .duration(12 * GTRecipeBuilder.SECONDS)
            .eut(24)
            .addTo(RecipeMaps.maceratorRecipes);

        MyMod.logInfo(
            "Registered Macerator salvage recipe for " + armorName
                + ": "
                + shadowNuggets
                + "x Shadow Nugget, "
                + thaumiumIngots
                + "x Thaumium Ingot @ "
                + (thaumiumIngotChance / 100)
                + "%, "
                + (voidMetalNuggetChance / 100)
                + "% Void Metal Nugget, "
                + (enchantedFabricChance / 100)
                + "% Enchanted Fabric.");
    }

    private static int clampChance(int chance) {
        return Math.max(1, Math.min(10000, chance));
    }

    private static ItemStack resolveCrimsonCultArmor(String registryName) {
        Item item = ItemLookup.findItem("Thaumcraft", registryName);
        if (item == null) {
            item = ItemLookup.findItem("thaumcraft", registryName);
        }
        return item == null ? null : new ItemStack(item, 1, 0);
    }

    private static ItemStack getThaumcraftItem(String registryName, int meta, int amount) {
        Item item = ItemLookup.findItem("Thaumcraft", registryName);
        if (item == null) {
            item = ItemLookup.findItem("thaumcraft", registryName);
        }
        return item == null ? null : new ItemStack(item, amount, meta);
    }

    private static ItemStack resolveShadowMetalNuggets(int amount) {
        ItemStack stack = ItemLookup.resolveFirstOreDictStack(
            amount,
            "nuggetShadow",
            "nuggetShadowmetal",
            "nuggetShadowMetal",
            "nuggetShadowium");
        if (stack != null) {
            return stack;
        }
        return ItemLookup.resolveFirstOreDictMatchByTokens(amount, "nugget", "shadow");
    }

    private static ItemStack resolveEnchantedFabric() {
        ItemStack stack = ItemLookup
            .resolveFirstOreDictStack(1, "clothEnchanted", "fabricEnchanted", "itemEnchantedFabric");
        if (stack != null) {
            return stack;
        }

        Item resource = ItemLookup.findItem("Thaumcraft", "ItemResource");
        if (resource == null) {
            resource = ItemLookup.findItem("thaumcraft", "ItemResource");
        }
        if (resource == null) {
            return null;
        }

        for (int meta = 0; meta <= 31; meta++) {
            ItemStack probe = new ItemStack(resource, 1, meta);
            String unlocalized = resource.getUnlocalizedName(probe);
            if (unlocalized != null && unlocalized.toLowerCase()
                .contains("fabric")) {
                return probe;
            }
        }

        return new ItemStack(resource, 1, 7);
    }

}
