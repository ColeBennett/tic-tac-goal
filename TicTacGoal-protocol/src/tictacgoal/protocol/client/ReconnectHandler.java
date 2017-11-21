package tictacgoal.protocol.client;

public interface ReconnectHandler {
	
    /**
     * Called when the client successfully reconnects
     * to specified remote address. You must have this
     * handler registered on the client, along with a 
     * set reconnect delay (in milliseconds).
     */
	void clientReconnected();
}
