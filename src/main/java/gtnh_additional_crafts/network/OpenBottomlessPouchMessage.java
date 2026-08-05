package gtnh_additional_crafts.network;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import gtnh_additional_crafts.MyMod;
import gtnh_additional_crafts.patches.thaumictinkerer.BottomlessPouchAccessPatch;
import gtnh_additional_crafts.patches.thaumictinkerer.BottomlessPouchAccessPatch.PouchSource;
import gtnh_additional_crafts.patches.thaumictinkerer.BottomlessPouchGuiHandlerPatch;
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
            PouchSource source = BottomlessPouchAccessPatch.findPouch(player);
            if (source == null) {
                return null;
            }

            BottomlessPouchAccessPatch.markSourceDirty(player, source);
            player.openGui(
                MyMod.instance,
                BottomlessPouchGuiHandlerPatch.GUI_ID_BOTTOMLESS_POUCH,
                player.worldObj,
                source.sourceType,
                source.slot,
                0);
            return null;
        }
    }
}
