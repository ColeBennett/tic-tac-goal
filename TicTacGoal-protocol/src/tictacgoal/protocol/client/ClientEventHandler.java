package tictacgoal.protocol.client;

import tictacgoal.protocol.EventHandler;
import tictacgoal.protocol.protocol.Packet;

public interface ClientEventHandler extends EventHandler {
    
    void packetReceived(Packet packet);
    
    void connectionLost();
}
