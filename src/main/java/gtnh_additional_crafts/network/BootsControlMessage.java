package gtnh_additional_crafts.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import gtnh_additional_crafts.compat.thaumicboots.ThaumicBootsTuning;
import gtnh_additional_crafts.compat.thaumicboots.VoidwalkerSashTuning;
import io.netty.buffer.ByteBuf;
import thaumicboots.api.IBoots;

public class BootsControlMessage implements IMessage {

    private double speedMultiplier;
    private double jumpMultiplier;
    private boolean omniEnabled;
    private boolean stepEnabled;
    private boolean inertiaCancelEnabled;
    private double forwardAxisMultiplier;
    private double strafeAxisMultiplier;
    private boolean hasSashState;
    private boolean sashSpeedBoostEnabled;
    private boolean hasVoidwalkerTravelerMovementState;
    private boolean voidwalkerTravelerMovementEnabled;

    public BootsControlMessage() {}

    public BootsControlMessage(double speedMultiplier, double jumpMultiplier, boolean omniEnabled, boolean stepEnabled,
        boolean inertiaCancelEnabled, double forwardAxisMultiplier, double strafeAxisMultiplier, boolean hasSashState,
        boolean sashSpeedBoostEnabled, boolean hasVoidwalkerTravelerMovementState,
        boolean voidwalkerTravelerMovementEnabled) {
        this.speedMultiplier = speedMultiplier;
        this.jumpMultiplier = jumpMultiplier;
        this.omniEnabled = omniEnabled;
        this.stepEnabled = stepEnabled;
        this.inertiaCancelEnabled = inertiaCancelEnabled;
        this.forwardAxisMultiplier = forwardAxisMultiplier;
        this.strafeAxisMultiplier = strafeAxisMultiplier;
        this.hasSashState = hasSashState;
        this.sashSpeedBoostEnabled = sashSpeedBoostEnabled;
        this.hasVoidwalkerTravelerMovementState = hasVoidwalkerTravelerMovementState;
        this.voidwalkerTravelerMovementEnabled = voidwalkerTravelerMovementEnabled;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        speedMultiplier = buffer.readDouble();
        jumpMultiplier = buffer.readDouble();
        omniEnabled = buffer.readBoolean();
        stepEnabled = buffer.readBoolean();
        inertiaCancelEnabled = buffer.readBoolean();
        forwardAxisMultiplier = buffer.readDouble();
        strafeAxisMultiplier = buffer.readDouble();
        hasSashState = buffer.readBoolean();
        sashSpeedBoostEnabled = buffer.readBoolean();
        hasVoidwalkerTravelerMovementState = buffer.readBoolean();
        voidwalkerTravelerMovementEnabled = buffer.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeDouble(speedMultiplier);
        buffer.writeDouble(jumpMultiplier);
        buffer.writeBoolean(omniEnabled);
        buffer.writeBoolean(stepEnabled);
        buffer.writeBoolean(inertiaCancelEnabled);
        buffer.writeDouble(forwardAxisMultiplier);
        buffer.writeDouble(strafeAxisMultiplier);
        buffer.writeBoolean(hasSashState);
        buffer.writeBoolean(sashSpeedBoostEnabled);
        buffer.writeBoolean(hasVoidwalkerTravelerMovementState);
        buffer.writeBoolean(voidwalkerTravelerMovementEnabled);
    }

    public static class Handler implements IMessageHandler<BootsControlMessage, IMessage> {

        @Override
        public IMessage onMessage(BootsControlMessage message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().playerEntity;
            boolean changed = false;

            if (message.hasSashState) {
                ItemStack sashStack = VoidwalkerSashTuning.getEquippedVoidwalkerSash(player);
                if (sashStack != null) {
                    VoidwalkerSashTuning.setSpeedBoostEnabled(sashStack, message.sashSpeedBoostEnabled);
                    changed = true;
                }
            }

            ItemStack bootsStack = IBoots.getBoots(player);
            if (bootsStack != null) {
                Item bootsItemRaw = bootsStack.getItem();
                if (bootsItemRaw instanceof IBoots) {
                    IBoots bootsItem = (IBoots) bootsItemRaw;
                    bootsItem.setModeSpeed(bootsStack, clamp01(message.speedMultiplier));
                    bootsItem.setModeJump(bootsStack, clamp01(message.jumpMultiplier));
                    bootsItem.setModeOmni(bootsStack, message.omniEnabled);
                    bootsItem.setModeStep(bootsStack, message.stepEnabled);
                    bootsItem.setIsInertiaCanceling(bootsStack, message.inertiaCancelEnabled);
                    ThaumicBootsTuning.setForwardMultiplier(bootsStack, message.forwardAxisMultiplier);
                    ThaumicBootsTuning.setStrafeMultiplier(bootsStack, message.strafeAxisMultiplier);
                    if (message.hasVoidwalkerTravelerMovementState) {
                        ThaumicBootsTuning.setVoidwalkerTravelerMovementEnabled(
                            bootsStack,
                            message.voidwalkerTravelerMovementEnabled);
                    }
                    changed = true;
                }
            }

            if (changed) {
                player.inventory.markDirty();
            }
            return null;
        }

        private static double clamp01(double value) {
            if (value < 0.0D) {
                return 0.0D;
            }
            if (value > 1.0D) {
                return 1.0D;
            }
            return value;
        }
    }
}
