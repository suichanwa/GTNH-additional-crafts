package gtnh_additional_crafts.compat.thaumictinkerer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;
import gtnh_additional_crafts.MyMod;

public class BottomlessPouchGuiHandler implements IGuiHandler {

    public static final int GUI_ID_BOTTOMLESS_POUCH = 0;

    private static final int POUCH_SLOT_COUNT = 13 * 9;
    private static final String CONTAINER_CLASS_NAME = "thaumic.tinkerer.common.block.tile.container.kami.ContainerIchorPouch";
    private static final String GUI_CLASS_NAME = "thaumic.tinkerer.client.gui.kami.GuiIchorPouch";

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int sourceType, int slot, int z) {
        if (id != GUI_ID_BOTTOMLESS_POUCH) {
            return null;
        }
        ItemStack pouchStack = BottomlessPouchAccess.getStack(player, sourceType, slot);
        if (!BottomlessPouchAccess.isBottomlessPouch(pouchStack)) {
            return null;
        }
        return createContainer(player, pouchStack, sourceType, slot);
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int sourceType, int slot, int z) {
        if (id != GUI_ID_BOTTOMLESS_POUCH) {
            return null;
        }
        ItemStack pouchStack = BottomlessPouchAccess.getStack(player, sourceType, slot);
        if (!BottomlessPouchAccess.isBottomlessPouch(pouchStack)) {
            return null;
        }

        Container container = createContainer(player, pouchStack, sourceType, slot);
        if (container == null) {
            return null;
        }

        try {
            Class<?> guiClass = Class.forName(GUI_CLASS_NAME);
            Constructor<?> constructor = guiClass.getConstructor(Container.class);
            return constructor.newInstance(container);
        } catch (Exception exception) {
            MyMod.logInfo("Unable to open ThaumicTinkerer Bottomless Pouch GUI: " + exception.getMessage());
            return null;
        }
    }

    private Container createContainer(EntityPlayer player, ItemStack pouchStack, int sourceType, int slot) {
        try {
            Class<?> containerClass = Class.forName(CONTAINER_CLASS_NAME);
            Constructor<?> constructor = containerClass.getConstructor(EntityPlayer.class, ItemStack.class);
            Container container = (Container) constructor.newInstance(player, pouchStack);
            setBlockedContainerSlot(container, sourceType, slot);
            return container;
        } catch (Exception exception) {
            MyMod.logInfo("Unable to create ThaumicTinkerer Bottomless Pouch container: " + exception.getMessage());
            return null;
        }
    }

    private void setBlockedContainerSlot(Container container, int sourceType, int slot)
        throws NoSuchFieldException, IllegalAccessException {
        Field blockSlotField = container.getClass()
            .getDeclaredField("blockSlot");
        blockSlotField.setAccessible(true);
        blockSlotField.setInt(container, getContainerSlotIndex(sourceType, slot));
    }

    private int getContainerSlotIndex(int sourceType, int slot) {
        if (sourceType != BottomlessPouchAccess.SOURCE_INVENTORY) {
            return -1;
        }
        if (slot < 9) {
            return POUCH_SLOT_COUNT + 27 + slot;
        }
        return POUCH_SLOT_COUNT + slot - 9;
    }
}
