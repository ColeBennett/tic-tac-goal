package tictacgoal.client.views;

import tictacgoal.client.animation.ShakeTransition;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import tictacgoal.client.TicTacGoal;
import tictacgoal.client.View;

public class MenuView extends View {

    private final TicTacGoal app;

    @FXML private AnchorPane base;
    @FXML private ImageView background;

    @FXML private Button singleplayer;
    @FXML private Button multiplayer;
    @FXML private TextField username;

    public MenuView(TicTacGoal app) {
        this.app = app;
    }

    @Override
    protected void initialize() {
        background.fitWidthProperty().bind(base.widthProperty());
        background.fitHeightProperty().bind(base.heightProperty());

        singleplayer.setOnAction(event -> {
            changeView(ViewType.BOARD);
            app.getBoardView().openSinglePlayer();
        });
        multiplayer.setOnAction(event ->
            changeView(ViewType.MULTIPLAYER));

        username.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 15 || !newValue.matches("^[a-zA-Z0-9_-]*$")) {
                username.setText(oldValue);
            } else {
                app.setUsername(newValue);
            }
        });
        username.requestFocus();
    }

    private void changeView(ViewType to) {
        if (username.getText().isEmpty()) {
            username.setStyle("-fx-border-color: red;");
            Transition t = new ShakeTransition(username);
            t.setOnFinished(event -> {
                username.setStyle("-fx-border-color: white;");
            });
            t.play();
        } else {
            app.setView(to);
        }
    }
}
