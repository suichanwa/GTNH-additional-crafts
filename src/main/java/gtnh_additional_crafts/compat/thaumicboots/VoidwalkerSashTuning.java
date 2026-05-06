package gtnh_additional_crafts.compat.thaumicboots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;

public final class VoidwalkerSashTuning {

    private static final String TAG_MODE = "mode";
    private static final String SASH_CLASS_NAME = "taintedmagic.common.items.equipment.ItemVoidwalkerSash";
    private static final String SASH_KEYWORD = "itemvoidwalkersash";

    private VoidwalkerSashTuning() {}

    public static ItemStack getEquippedVoidwalkerSash(EntityPlayer player) {
        if (player == null) {
            return null;
        }

        InventoryBaubles baublesInventory = PlayerHandler.getPlayerBaubles(player);
        if (baublesInventory == null) {
            return null;
        }

        for (int slot = 0; slot < baublesInventory.getSizeInventory(); slot++) {
            ItemStack stack = baublesInventory.getStackInSlot(slot);
            if (isVoidwalkerSash(stack)) {
                return stack;
            }
        }
        return null;
    }

    public static boolean hasSpeedBoostEnabled(ItemStack sashStack) {
        if (!isVoidwalkerSash(sashStack)) {
            return false;
        }
        NBTTagCompound tag = sashStack.getTagCompound();
        if (tag == null) {
            return true;
        }
        return tag.getBoolean(TAG_MODE);
    }

    public static void setSpeedBoostEnabled(ItemStack sashStack, boolean enabled) {
        if (!isVoidwalkerSash(sashStack)) {
            return;
        }
        NBTTagCompound tag = sashStack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            sashStack.setTagCompound(tag);
        }
        tag.setBoolean(TAG_MODE, enabled);
    }

    public static boolean isVoidwalkerSash(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }

        Item item = stack.getItem();
        String className = item.getClass()
            .getName();
        if (SASH_CLASS_NAME.equals(className)) {
            return true;
        }

        String unlocalized = item.getUnlocalizedName(stack);
        if (unlocalized == null) {
            return false;
        }
        return unlocalized.toLowerCase()
            .contains(SASH_KEYWORD);
    }
}
