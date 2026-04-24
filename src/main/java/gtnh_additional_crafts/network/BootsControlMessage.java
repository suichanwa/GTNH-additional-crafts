package gtnh_additional_crafts.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import gtnh_additional_crafts.compat.thaumicboots.ThaumicBootsTuning;
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

    public BootsControlMessage() {}

    public BootsControlMessage(double speedMultiplier, double jumpMultiplier, boolean omniEnabled, boolean stepEnabled,
        boolean inertiaCancelEnabled, double forwardAxisMultiplier, double strafeAxisMultiplier) {
        this.speedMultiplier = speedMultiplier;
        this.jumpMultiplier = jumpMultiplier;
        this.omniEnabled = omniEnabled;
        this.stepEnabled = stepEnabled;
        this.inertiaCancelEnabled = inertiaCancelEnabled;
        this.forwardAxisMultiplier = forwardAxisMultiplier;
        this.strafeAxisMultiplier = strafeAxisMultiplier;
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
    }

    public static class Handler implements IMessageHandler<BootsControlMessage, IMessage> {

        @Override
        public IMessage onMessage(BootsControlMessage message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().playerEntity;
            ItemStack bootsStack = IBoots.getBoots(player);
            if (bootsStack == null) {
                return null;
            }
            Item bootsItemRaw = bootsStack.getItem();
            if (!(bootsItemRaw instanceof IBoots)) {
                return null;
            }
            IBoots bootsItem = (IBoots) bootsItemRaw;
            bootsItem.setModeSpeed(bootsStack, clamp01(message.speedMultiplier));
            bootsItem.setModeJump(bootsStack, clamp01(message.jumpMultiplier));
            bootsItem.setModeOmni(bootsStack, message.omniEnabled);
            bootsItem.setModeStep(bootsStack, message.stepEnabled);
            bootsItem.setIsInertiaCanceling(bootsStack, message.inertiaCancelEnabled);
            ThaumicBootsTuning.setForwardMultiplier(bootsStack, message.forwardAxisMultiplier);
            ThaumicBootsTuning.setStrafeMultiplier(bootsStack, message.strafeAxisMultiplier);
            player.inventory.markDirty();
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
