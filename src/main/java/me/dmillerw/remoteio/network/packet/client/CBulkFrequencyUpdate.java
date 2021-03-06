package me.dmillerw.remoteio.network.packet.client;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import me.dmillerw.remoteio.client.gui.GuiFrequency;
import me.dmillerw.remoteio.network.PacketHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Map;

/**
 * Created by dmillerw
 */
public class CBulkFrequencyUpdate implements IMessage {

    protected Map<Integer, String> map;

    public CBulkFrequencyUpdate() {

    }

    public CBulkFrequencyUpdate(Map<Integer, String> map) {
        this.map = map;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(map.size());
        for(Map.Entry<Integer, String> entry : map.entrySet()) {
            buf.writeInt(entry.getKey());
            ByteBufUtils.writeUTF8String(buf, entry.getValue());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int length = buf.readInt();

        map = Maps.newHashMap();
        for (int i=0; i<length; i++) {
            map.put(buf.readInt(), ByteBufUtils.readUTF8String(buf));
        }
    }

    public static class Handler implements IMessageHandler<CBulkFrequencyUpdate, IMessage> {

        @Override
        public IMessage onMessage(CBulkFrequencyUpdate message, MessageContext ctx) {
            PacketHandler.addScheduledTask(ctx.netHandler, () -> handleMessage(message, ctx));
            return null;
        }

        public void handleMessage(CBulkFrequencyUpdate message, MessageContext ctx) {
            GuiFrequency.frequencies.clear();
            GuiFrequency.frequencies.putAll(message.map);
        }
    }
}
