package tictacgoal.client.views;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import tictacgoal.client.Player;
import tictacgoal.client.TicTacGoal;
import tictacgoal.client.View;
import tictacgoal.client.Connection;
import tictacgoal.protocol.Protocol;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MatchmakingView extends View {

    private final TicTacGoal app;

    @FXML private AnchorPane base;
    @FXML private Label playersOnline;
    @FXML private ListView<Text> playerList;
    @FXML private Button back;

    private final Connection conn;
    private final Map<String, Player> players = new LinkedHashMap<>();

    public MatchmakingView(TicTacGoal app) {
        this.app = app;

        conn = new Connection(app);
    }

    @Override
    protected void initialize() {
        updatePlayersOnline();

        playerList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        playerList.getSelectionModel().clearSelection();
        playerList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.getText().isEmpty()) {
                conn.send(Protocol.REQUEST_MATCH.construct(newValue.getText()));
            }
        });

        back.setOnAction(event -> app.setView(ViewType.MENU));
    }

    public Connection getConnection() {
        return conn;
    }

    public void open() {
        if (!conn.getClient().isActive()) {
            conn.connect().addListener(channelFuture ->
                    conn.send(Protocol.PLAYER_CONNECT.construct(app.getUsername())));
        }
    }

    public void addPlayer(Player player) {
        players.put(player.getName(), player);
        Text text = new Text(player.getName());
        text.setFill(Color.AQUA);
        if (!player.getName().equals(app.getUsername())) {
            playerList.getItems().add(text);
        }
        updatePlayersOnline();
    }

    public void removePlayer(String name) {
        Iterator<Text> itr = playerList.getItems().iterator();
        while (itr.hasNext()) {
            if (itr.next().getText().contains(name)) {
                itr.remove();
            }
        }

        players.remove(name);
        updatePlayersOnline();
    }

    private void updatePlayersOnline() {
        playersOnline.setText("Players Online: " + players.size());
    }
}
