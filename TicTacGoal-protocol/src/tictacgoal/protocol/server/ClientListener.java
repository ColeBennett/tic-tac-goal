package tictacgoal.protocol.server;

import io.netty.channel.Channel;
import tictacgoal.protocol.AbstractConnection;
import tictacgoal.protocol.protocol.PacketRegistry;

public final class ClientListener extends AbstractConnection {
    
    private final DataServer server;
    
    ClientListener(DataServer server, Channel channel) {
        this.server = server;
        this.channel = channel;
    }

    @Override
    public PacketRegistry getPacketRegistry() {
        return server.getPacketRegistry();
    }
    
    public DataServer getParent() {
        return server;
    }
}
