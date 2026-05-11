package gtnh_additional_crafts.compat.thaumictinkerer;

import java.util.Locale;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public final class BottomlessPouchAccess {

    public static final int SOURCE_INVENTORY = 0;
    public static final int SOURCE_BAUBLES = 1;

    private static final String THAUMIC_TINKERER_MOD_ID = "ThaumicTinkerer";
    private static final String ICHOR_POUCH_ITEM_NAME = "ichorPouch";
    private static final String ICHOR_POUCH_CLASS_NAME = "thaumic.tinkerer.common.item.kami.ItemIchorPouch";

    private BottomlessPouchAccess() {}

    public static PouchSource findPouch(EntityPlayer player) {
        if (player == null) {
            return null;
        }

        ItemStack[] inventory = player.inventory.mainInventory;
        for (int slot = 0; slot < inventory.length; slot++) {
            if (isBottomlessPouch(inventory[slot])) {
                return new PouchSource(SOURCE_INVENTORY, slot);
            }
        }

        InventoryBaubles baublesInventory = PlayerHandler.getPlayerBaubles(player);
        if (baublesInventory == null) {
            return null;
        }
        for (int slot = 0; slot < baublesInventory.getSizeInventory(); slot++) {
            if (isBottomlessPouch(baublesInventory.getStackInSlot(slot))) {
                return new PouchSource(SOURCE_BAUBLES, slot);
            }
        }
        return null;
    }

    public static ItemStack getStack(EntityPlayer player, int sourceType, int slot) {
        if (player == null || slot < 0) {
            return null;
        }

        if (sourceType == SOURCE_INVENTORY) {
            ItemStack[] inventory = player.inventory.mainInventory;
            if (slot >= inventory.length) {
                return null;
            }
            return inventory[slot];
        }

        if (sourceType == SOURCE_BAUBLES) {
            InventoryBaubles baublesInventory = PlayerHandler.getPlayerBaubles(player);
            if (baublesInventory == null || slot >= baublesInventory.getSizeInventory()) {
                return null;
            }
            return baublesInventory.getStackInSlot(slot);
        }

        return null;
    }

    public static boolean isBottomlessPouch(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }

        Item item = stack.getItem();
        Item registeredPouch = GameRegistry.findItem(THAUMIC_TINKERER_MOD_ID, ICHOR_POUCH_ITEM_NAME);
        if (registeredPouch != null && item == registeredPouch) {
            return true;
        }

        if (ICHOR_POUCH_CLASS_NAME.equals(
            item.getClass()
                .getName())) {
            return true;
        }

        String unlocalizedName = item.getUnlocalizedName(stack);
        return unlocalizedName != null && unlocalizedName.toLowerCase(Locale.ROOT)
            .contains(ICHOR_POUCH_ITEM_NAME.toLowerCase(Locale.ROOT));
    }

    public static void markSourceDirty(EntityPlayer player, PouchSource source) {
        if (player == null || source == null) {
            return;
        }
        if (source.sourceType == SOURCE_BAUBLES) {
            InventoryBaubles baublesInventory = PlayerHandler.getPlayerBaubles(player);
            if (baublesInventory != null) {
                baublesInventory.markDirty();
            }
        }
        player.inventory.markDirty();
    }

    public static final class PouchSource {

        public final int sourceType;
        public final int slot;

        public PouchSource(int sourceType, int slot) {
            this.sourceType = sourceType;
            this.slot = slot;
        }
    }
}
