package tictacgoal.client;

import io.netty.channel.ChannelFuture;
import javafx.application.Platform;
import tictacgoal.client.View.ViewType;
import tictacgoal.client.views.BoardView;
import tictacgoal.client.views.MatchmakingView;
import tictacgoal.protocol.BufferedPacket;
import tictacgoal.protocol.Protocol;
import tictacgoal.protocol.client.*;
import tictacgoal.protocol.protocol.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Connection implements ClientEventHandler {

    private static final Logger log = Logger.getLogger("TicTacGoal");

    private final TicTacGoal app;
    private final DataClient client;

    public Connection(TicTacGoal app) {
        this.app = app;

        client = new NioDataClient(new ClientFutureListener() {
            @Override
            public void clientConnected() {
                log.info("Connected to server: " + client.getRemoteAddress());
            }

            @Override
            public void connectFailed() {
                log.info("Failed to connect to server.");
            }
        });
        client.addHandler(this);
        client.setReconnectDelayMillis(1000);
        client.setReconnectHandler(new ReconnectHandler() {
            @Override
            public void clientReconnected() {
                log.info("Reconnected to server: " + client.getRemoteAddress());
            }
        });
    }

    public DataClient getClient() {
        return client;
    }

    public void send(BufferedPacket buf) {
        Protocol header = Protocol.valueOf(buf.getShort(0));
        client.send(buf);
        log.info("Sent packet [" + header + "]: " + buf);
    }

    public ChannelFuture connect() {
        return client.connect("localhost", Protocol.PORT);
    }

    public void disconnect() {
        client.disconnect();
    }

    @Override
    public void packetReceived(Packet packet) {
        if (packet instanceof BufferedPacket) {
            Platform.runLater(() -> {
                BufferedPacket buf = (BufferedPacket) packet;
                Protocol header = Protocol.valueOf(buf);
                log.info("Received packet [" + header + "]: " + buf);

                MatchmakingView view = app.getMatchmakingView();
                if (header == Protocol.PLAYER_LIST) {
                    for (int i = 0; i < buf.size(); i++) {
                        view.addPlayer(new Player(buf.getString(i), null));
                    }
                } else if (header == Protocol.PLAYER_CONNECT) {
                    view.addPlayer(new Player(buf.getString(0), null));
                } else if (header == Protocol.PLAYER_DISCONNECT) {
                    view.removePlayer(buf.getString(0));
                } else if (header == Protocol.MATCH_START) {
                    String opponent = buf.getString(0);
                    log.info("Match start; opponent: " + opponent);
                    app.setView(ViewType.BOARD);
                    BoardView board = (BoardView) app.getView(ViewType.BOARD);

                    List<Player> players = new ArrayList<>(2);
                    for (int i = 0; i < buf.size(); i += 2) {
                        players.add(new Player(buf.getString(i), buf.getEnum(i + 1, TileState.class)));
                    }
                    board.start(players, GameMode.MULTIPLAYER);
                } else if (header == Protocol.MOVE) {
                    String player = buf.getString(0);
                    TileState state = buf.getEnum(1, TileState.class);
                    int row = buf.getInt(2), col = buf.getInt(3);
                    log.info("Game move by " + player + "; state: " + state + " (row: " + row + ", col: " + col + ")");
                    BoardView board = (BoardView) app.getView(ViewType.BOARD);
                    board.move(player, state, row, col);
                }
            });
        }
    }

    @Override
    public void connectionLost() {
        log.info("Connection lost");
    }
}
