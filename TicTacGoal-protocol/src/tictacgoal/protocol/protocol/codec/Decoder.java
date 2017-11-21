package tictacgoal.protocol.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import tictacgoal.protocol.BufferedPacket;
import tictacgoal.protocol.protocol.Packet;
import tictacgoal.protocol.protocol.PacketRegistry;
import tictacgoal.protocol.protocol.impl.KeepAlivePacket;

import java.util.List;

public class Decoder extends ByteToMessageDecoder {
    
    private static final int HEADER_LENGTH = 5;

    private final PacketRegistry registry;
    
    public Decoder(PacketRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        if (buf.readableBytes() < HEADER_LENGTH) {
            return;
        }
        buf.markReaderIndex();
        
        short id = buf.readShort();
        int bodyLen = buf.readUnsignedMedium();   
        
        if (buf.readableBytes() < bodyLen) {
            buf.resetReaderIndex();
            return;
        }

        int startLen = buf.readableBytes();
        
        Packet packet;
        switch (id) {
        case KeepAlivePacket.PACKET_ID:
            packet = KeepAlivePacket.INSTANCE;
            out.add(packet);
            return;
        case BufferedPacket.PACKET_ID:            
            packet = new BufferedPacket(buf);
            break;
        default:
            packet = registry.newInstance(id);
            break;
        }
        
        if (packet == null) {
            throw new DecoderException("Bad packet id: " + id);
        }
        if (id != BufferedPacket.PACKET_ID) {
            try {
                packet.read(buf);
            } catch (Throwable t) {
                buf.skipBytes(bodyLen);
                throw new DecoderException(t);
            }   
            if (buf.readableBytes() != (startLen - bodyLen)) {
                buf.skipBytes(bodyLen);
                throw new DecoderException("Did not read all bytes from packet:"
                        + " (id: " + id + ", expected length: " + bodyLen + ")");
            }
        }
        out.add(packet);
    }
}
