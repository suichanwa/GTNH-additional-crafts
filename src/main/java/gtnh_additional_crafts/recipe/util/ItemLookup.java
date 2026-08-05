package gtnh_additional_crafts.recipe.util;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.util.GTUtility;

public final class ItemLookup {

    private ItemLookup() {}

    public static ItemStack resolveFirstOreDictStack(int amount, String... oreNames) {
        for (String oreName : oreNames) {
            List<ItemStack> stacks = OreDictionary.getOres(oreName);
            if (stacks == null || stacks.isEmpty()) {
                continue;
            }
            ItemStack first = stacks.get(0);
            if (first != null && first.getItem() != null) {
                return GTUtility.copyAmount(amount, first);
            }
        }
        return null;
    }

    public static ItemStack resolveFirstOreDictMatchByTokens(int amount, String... requiredTokens) {
        for (String oreName : OreDictionary.getOreNames()) {
            String normalized = oreName == null ? "" : oreName.toLowerCase();
            boolean matches = true;
            for (String token : requiredTokens) {
                if (!normalized.contains(token.toLowerCase())) {
                    matches = false;
                    break;
                }
            }
            if (!matches) {
                continue;
            }

            List<ItemStack> stacks = OreDictionary.getOres(oreName);
            if (stacks == null || stacks.isEmpty()) {
                continue;
            }
            ItemStack first = stacks.get(0);
            if (first != null && first.getItem() != null) {
                return GTUtility.copyAmount(amount, first);
            }
        }
        return null;
    }

    public static Item findItem(String modId, String itemName) {
        return GameRegistry.findItem(modId, itemName);
    }

    public static ItemStack resolveVoidMetalNuggets(int amount) {
        ItemStack stack = resolveFirstOreDictStack(
            amount,
            "nuggetVoid",
            "nuggetVoidmetal",
            "nuggetVoidMetal",
            "nuggetVoidMetalThaumcraft");
        if (stack != null) {
            return stack;
        }
        return resolveFirstOreDictMatchByTokens(amount, "nugget", "void");
    }

}
