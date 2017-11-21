package tictacgoal.protocol.server;

public interface ServerFutureListener {
    
    /**
     * Called when the server successfully 
     * binds to specified host.
     */
    void bindSucceeded();
    
    /**
     * Called when the server fails to bind
     * to specified host for any reason.
     */
    void bindFailed();
}
