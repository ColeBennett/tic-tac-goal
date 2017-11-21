package tictacgoal.client.views;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import tictacgoal.client.TicTacGoal;
import tictacgoal.client.TileState;
import tictacgoal.client.View;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class EndView extends View {

    private final TicTacGoal app;

    @FXML private AnchorPane base;
    @FXML private Label goal;
    @FXML private Label winner;

    private ScheduledFuture<?> task;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final Color[] colors = {
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.BLUE};

    public EndView(TicTacGoal app) {
        this.app = app;
    }

    @Override
    protected void initialize() {
        winner.setText("");
    }

    public void winner(TileState state) {
        winner.setText("Winner: " + state.getSymbol());
        if (state == TileState.X) {
            winner.setTextFill(Color.RED);
        } else if (state == TileState.O) {
            winner.setTextFill(Color.BLUE);
        }

        if (task != null) {
            task.cancel(true);
        }
        task = scheduler.scheduleAtFixedRate(() -> Platform.runLater(() -> {
            Color col = randomColor();
            goal.setTextFill(col);
            base.setStyle("-fx-background-color: " + Integer.toHexString(randomColor().hashCode()) + ";");
        }), 0, 250, TimeUnit.MILLISECONDS);
    }

    private final Random rand = new Random();

    private Color randomColor() {
        return colors[rand.nextInt(colors.length)];
    }
}
