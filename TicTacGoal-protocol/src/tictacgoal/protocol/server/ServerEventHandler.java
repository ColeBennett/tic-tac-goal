package tictacgoal.protocol.server;

import tictacgoal.protocol.EventHandler;
import tictacgoal.protocol.protocol.Packet;

public interface ServerEventHandler extends EventHandler {
    
    void packetReceived(ClientListener client, Packet packet);
    
    void clientConnected(ClientListener client);

    void clientDisconnected(ClientListener client);
}
