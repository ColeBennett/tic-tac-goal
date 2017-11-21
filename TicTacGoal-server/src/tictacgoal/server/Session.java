package tictacgoal.server;

import tictacgoal.protocol.TileState;
import tictacgoal.protocol.protocol.Packet;
import tictacgoal.protocol.server.ClientListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Session {

    private final TicTacGoalServer server;
    private long startTime, endTime;
    private final Map<Player, TileState> players = new HashMap<>(2);

    public Session(TicTacGoalServer server) {
        this.server = server;
        startTime = System.currentTimeMillis();
    }

    public long getDuration() {
        if (startTime == 0 || endTime == 0)
            return 0;
        return endTime - startTime;
    }

    public Set<Player> getPlayers() {
        return players.keySet();
    }

    public TileState getState(Player player) {
        return players.get(player);
    }

    public void setState(Player player, TileState state) {
        players.put(player, state);
    }

    public void end() {
        endTime = System.currentTimeMillis();
    }

    public void broadcast(Packet packet) {
        for (Entry<ClientListener, Player> e : server.getPlayers().entrySet()) {
            if (players.containsKey(e.getValue())) {
                e.getKey().send(packet);
            }
        }
    }
}
