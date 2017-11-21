package tictacgoal.server;

import tictacgoal.protocol.BufferedPacket;
import tictacgoal.protocol.Protocol;
import tictacgoal.protocol.TileState;
import tictacgoal.protocol.protocol.Packet;
import tictacgoal.protocol.server.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class TicTacGoalServer implements ServerEventHandler {

    public static void main(String[] args) {
        new TicTacGoalServer();
    }

    private final Logger log = Logger.getLogger("TicTacGoal");
    private final DataServer server;

    private final Set<Session> sessions = new HashSet<>();
    private final Map<ClientListener, Player> players = new HashMap<>();

    public TicTacGoalServer() {
        server = new NioDataServer(new ServerFutureListener() {
            @Override
            public void bindSucceeded() {
                log.info("Server now running: " + server.getAddress());
            }

            @Override
            public void bindFailed() {
                log.severe("Server failed to start.");
            }
        });
        server.addHandler(this);
        server.bind(Protocol.PORT);
    }

    @Override
    public void packetReceived(ClientListener client, Packet packet) {
        if (packet instanceof BufferedPacket) {
            BufferedPacket buf = (BufferedPacket) packet;
            Protocol header = Protocol.valueOf(buf);
            if (header == null) return;
            log.info("Received [" + header + "]: " + buf);

            if (header == Protocol.PLAYER_CONNECT) {
                String name = buf.getString(0);
                players.put(client, new Player(name));

                List<String> names = new ArrayList<>(players.size());
                for (Player p : players.values()) {
                    names.add(p.getName());
                }
                send(client, Protocol.PLAYER_LIST.construct(names));

                sendAllExcept(client, Protocol.PLAYER_CONNECT.construct(name));
                return;
            } else if (header == Protocol.PLAYER_DISCONNECT) {
                Player player = players.remove(client);
                sendAll(Protocol.PLAYER_DISCONNECT.construct(player.getName()));
                return;
            }

            Player player = players.get(client);
            if (header == Protocol.MOVE) {
                TileState state = buf.getEnum(0, TileState.class);
                int row = buf.getInt(1);
                int col = buf.getInt(2);
                for (Session s : sessions) {
                    if (s.getPlayers().contains(player)) {
                        s.broadcast(Protocol.MOVE.construct(player.getName(), state, row, col));
                        break;
                    }
                }
                log.info("Game move by " + player.getName() + "; state: " + state);
            } else if (header == Protocol.REQUEST_MATCH) {
                if (player != null) {
                    String targetName = buf.getString(0);
                    Player target = null;
                    for (Player p : players.values()) {
                        if (p.getName().equals(targetName)) {
                            target = p;
                            break;
                        }
                    }
                    if (target != null) {
                        Session session = new Session(this);
                        session.setState(player, TileState.X);
                        session.setState(target, TileState.O);
                        sessions.add(session);

                        session.broadcast(Protocol.MATCH_START.construct(
                                player.getName(), TileState.X,
                                target.getName(), TileState.O));
                        log.info("Created session; " + player.getName() + " [X] vs " + target.getName() + " [O]");
                    } else {
                        log.info("Player not found for request match: " + targetName);
                    }
                }
            }
        }
    }

    @Override
    public void clientConnected(ClientListener client) {
        log.info("Client connected: " + client + "; pending connect request");
    }

    @Override
    public void clientDisconnected(ClientListener client) {
        log.info("Client disconnected: " + client);
        Player player = players.remove(client);
        if (player != null) {
            sendAllExcept(client, Protocol.PLAYER_DISCONNECT.construct(player.getName()));
            log.info("Player disconnected: " + player.getName());
        } else {
            log.warning("Unknown player disconnected: " + client);
        }
    }

    public Map<ClientListener, Player> getPlayers() {
        return players;
    }

    public void send(ClientListener client, BufferedPacket buf) {
        Protocol header = Protocol.valueOf(buf.getShort(0));
        client.send(buf);
        log.info("Sent [" + header + "]: to " + client);
    }

    public void sendAll(BufferedPacket buf) {
        for (ClientListener client : players.keySet()) {
            send(client, buf);
        }
    }

    public void sendAllExcept(ClientListener except, BufferedPacket buf) {
        for (ClientListener client : players.keySet()) {
            if (!client.equals(except)) {
                send(client, buf);
            }
        }
    }

    private ClientListener getClient(Player player) {
        for (Entry<ClientListener, Player> entry : players.entrySet()) {
            if (entry.getValue().equals(player)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
