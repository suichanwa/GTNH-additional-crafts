package gtnh_additional_crafts.patches.thaumicboots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import thaumicboots.api.IBoots;

public class ThaumicBootsRuntimeEventHandlerPatch {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        EntityPlayer player = event.player;
        if (player == null) {
            return;
        }
        ItemStack bootsStack = IBoots.getBoots(player);
        if (bootsStack == null) {
            return;
        }
        ThaumicBootsTuningPatch.applyVoidwalkerEmtTravelerMovement(player, bootsStack);
        ThaumicBootsTuningPatch.applyAxisMultipliers(player, bootsStack);
    }
}
