package tictacgoal.protocol.client;

public interface ClientFutureListener {
    
    /**
     * Called when the client successfully 
     * connects to specified remote host.
     */
    void clientConnected();
    
    /**
     * Called when the client fails to connect 
     * to specified remote host or fails to bind
     * to optionally specified local address.
     */
    void connectFailed();
}
