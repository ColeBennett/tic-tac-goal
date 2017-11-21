package tictacgoal.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tictacgoal.client.View.ViewType;
import tictacgoal.client.util.AudioManager;
import tictacgoal.client.views.BoardView;
import tictacgoal.client.views.EndView;
import tictacgoal.client.views.MatchmakingView;
import tictacgoal.client.views.MenuView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class TicTacGoal extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static final Logger log = Logger.getLogger("TicTacGoal");

    private final RootStack root = new RootStack();
    private final AudioManager audioManager = new AudioManager();
    private final Map<ViewType, View> views = new HashMap<>();

    private String username;

    @Override
    public void start(Stage stage) throws Exception {
        views.put(ViewType.MENU, new MenuView(this));
        views.put(ViewType.MULTIPLAYER, new MatchmakingView(this));
        views.put(ViewType.BOARD, new BoardView(this));
        views.put(ViewType.END, new EndView(this));

        Scene scene = new Scene(root);
        scene.getStylesheets().add("style.css");
        stage.setScene(scene);

        setView(ViewType.MENU);

        stage.setWidth(750);
        stage.setHeight(700);
        stage.setTitle("Tic-Tac-Goal");
        stage.centerOnScreen();
        stage.setOnCloseRequest(event -> System.exit(0));
        stage.show();

//        audioManager.play("theme");
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public String getUsername() {
        return username;
    }

    public View getView(ViewType type) {
        return views.get(type);
    }

    public MenuView getMenuView() {
        return (MenuView) getView(ViewType.MENU);
    }

    public BoardView getBoardView() {
        return (BoardView) getView(ViewType.BOARD);
    }

    public MatchmakingView getMatchmakingView() {
        return (MatchmakingView) getView(ViewType.MULTIPLAYER);
    }

    public EndView getEndView() {
        return (EndView) getView(ViewType.END);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setView(ViewType type) {
        View view = views.get(type);
        if (!view.hasLoaded()) {
            ClassLoader cldr = getClass().getClassLoader();
            FXMLLoader ldr = new FXMLLoader(cldr.getResource(type.getFxml()));
            view.setLoader(ldr);
            ldr.setController(view);
            Parent node = null;
            try {
                node = ldr.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (node != null) {
                root.add(type, (Pane) node);
            }
        }

        if (type == ViewType.MULTIPLAYER) {
            ((MatchmakingView) view).open();
        }

        root.show(type);
    }
}
