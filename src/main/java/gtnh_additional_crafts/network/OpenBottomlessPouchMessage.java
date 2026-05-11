package gtnh_additional_crafts.network;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.compat.thaumictinkerer.BottomlessPouchAccess;
import gtnh_additional_crafts.compat.thaumictinkerer.BottomlessPouchAccess.PouchSource;
import gtnh_additional_crafts.compat.thaumictinkerer.BottomlessPouchGuiHandler;
import io.netty.buffer.ByteBuf;

public class OpenBottomlessPouchMessage implements IMessage {

    @Override
    public void fromBytes(ByteBuf buffer) {}

    @Override
    public void toBytes(ByteBuf buffer) {}

    public static class Handler implements IMessageHandler<OpenBottomlessPouchMessage, IMessage> {

        @Override
        public IMessage onMessage(OpenBottomlessPouchMessage message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().playerEntity;
            PouchSource source = BottomlessPouchAccess.findPouch(player);
            if (source == null) {
                return null;
            }

            BottomlessPouchAccess.markSourceDirty(player, source);
            player.openGui(
                MyMod.instance,
                BottomlessPouchGuiHandler.GUI_ID_BOTTOMLESS_POUCH,
                player.worldObj,
                source.sourceType,
                source.slot,
                0);
            return null;
        }
    }
}
