package tictacgoal.client.views;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;
import tictacgoal.client.*;
import tictacgoal.client.TilePattern.TileCoord;
import tictacgoal.client.TilePattern.WinPattern;
import tictacgoal.client.animation.FadeOutDownBigTransition;
import tictacgoal.client.animation.FadeOutRightBigTransition;
import tictacgoal.client.animation.ShakeTransition;
import tictacgoal.protocol.Protocol;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BoardView extends View {

    private final TicTacGoal app;

    /**
     * UI Components
     */
    @FXML private AnchorPane base;
    @FXML private ImageView background;
    @FXML private GridPane grid;

    @FXML private Label roundLabel;
    @FXML private Label gameTime;

    @FXML private Text playerxName;
    @FXML private Text playeroName;
    @FXML private Text playerxScore;
    @FXML private Text playeroScore;
    @FXML private HBox playerxInfo;
    @FXML private HBox playeroInfo;

    /**
     * Grid Parameters
     */
    public static final int ROWS = 3;
    public static final int COLUMNS = 3;
    public static final int TILES = ROWS * COLUMNS;

    /**
     * Game Board
     */
    private final Tile[] tiles = new Tile[TILES];

    private Player player;
    private Player playing;
    private List<Player> players;
    private TileState turn = TileState.X;

    private GameMode mode;
    private int round = 1, maxRounds = 3;
    private int xscore, oscore;

    private long startTime;
    private boolean ending;

    public BoardView(TicTacGoal app) {
        this.app = app;

        int i = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                tiles[i++] = new Tile(r, c);
            }
        }
    }

    @Override
    protected void initialize() {
        background.fitWidthProperty().bind(base.widthProperty());
        background.fitHeightProperty().bind(base.heightProperty());

        for (Tile tile : tiles) {
            grid.add(createFrom(tile), tile.getColumn(), tile.getRow());
        }

        start(Arrays.asList(
                        new Player(app.getUsername(), TileState.X),
                        new Player("Computer", TileState.O)),
                GameMode.SINGLEPLAYER);

        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() ->
                Platform.runLater(() -> {
                    long ellapsed = System.currentTimeMillis() - startTime;
                    gameTime.setText("Game Time: " + format.format(ellapsed));
                }), 0, 1, TimeUnit.SECONDS);
    }

    public void openSinglePlayer() {
        start(Arrays.asList(
                        new Player(app.getUsername(), TileState.X),
                        new Player("Computer", TileState.O)),
                GameMode.SINGLEPLAYER);
    }

    private HBox createFrom(Tile tile) {
        HBox box = new HBox();
        box.getStyleClass().add("tile");
        box.setAlignment(Pos.CENTER);
        tile.setBox(box);

        Button click = new Button("");
        click.getStyleClass().add("click");
        click.prefWidthProperty().bind(box.widthProperty());
        click.prefHeightProperty().bind(box.heightProperty());
        tile.setButton(click);

        click.setOnAction(event -> {
            if (click.getText().isEmpty() && turn == player.getPosition()) {
                switch (mode) {
                    case SINGLEPLAYER:
                        move(player.getName(), tile, player.getPosition());

                        /**
                         * AI Player
                         * Delay the AI's play to make it realistic.
                         */
                        Timeline delay = new Timeline(new KeyFrame(Duration.millis(500), ev -> {
                            if (ending) {
                                System.out.println("ENDING: STOPPED");
                                return;
                            }

                            Player ai = players.get(1);
                            Tile next = TilePattern.nextPlay(tiles, ai);
                            if (next != null) {
                                move(ai.getName(), next, ai.getPosition());
                                System.out.println("Computer's next play: " + next + " (" + next.getState() + ")");
                            } else {
                                turn = TileState.X;
                                System.out.println("No next play made by Computer.");
                            }
                        }));
                        delay.setCycleCount(1);
                        delay.play();
                        break;
                    case MULTIPLAYER:
                        MatchmakingView view = app.getMatchmakingView();
                        view.getConnection().send(Protocol.MOVE.construct(turn, tile.getRow(), tile.getColumn()));
                        break;
                }
            }
        });

        box.getChildren().addAll(click);
        return box;
    }

    public void start(List<Player> players, GameMode mode) {
        this.players = players;
        this.mode = mode;

        for (Player p : players) {
            TileState pos = p.getPosition();
            Text name = pos == TileState.X ? playerxName : playeroName;
            name.setText(p.getName() + " [" + pos.getSymbol() + "]");

            if (pos == TileState.X) {
                playing = p;
            }
            if (p.getName().equals(app.getUsername())) {
                player = p;
            }
        }

        turn = TileState.X;

        updateRound();
        startTime = System.currentTimeMillis();
    }

    public void end() {
        player = null;
        playing = null;
        players = null;
//        mode = null;

        xscore = 0;
        oscore = 0;
        playerxScore.setText("0");
        playeroScore.setText("0");

        startTime = 0;

        round = 1;
        turn = TileState.X;
    }

    public void move(String player, TileState newState, int row, int col) {
        Tile tile = getTile(row, col);
        if (tile != null) {
            move(player, tile, newState);
        }
    }

    public void move(String player, Tile tile, TileState newState) {
        tile.setState(newState);

        Button click = tile.getButton();
        click.setText(newState.getSymbol());
        if (newState == TileState.X) {
            click.getStyleClass().remove("click-o");
            click.getStyleClass().add("click-x");
        } else if (newState == TileState.O) {
            click.getStyleClass().remove("click-x");
            click.getStyleClass().add("click-o");
        }

        TileState old = turn;

        if (turn == TileState.X) {
            playeroInfo.getStyleClass().add("playerx-info");

            turn = TileState.O;
            playing = players.get(1);
        } else {
            playeroInfo.getStyleClass().add("playero-info");

            turn = TileState.X;
            playing = players.get(0);
        }

        System.out.println("Turn: " + old + " -> " + turn);

        checkWin();
    }

    private void checkWin() {
        WinPattern win = TilePattern.findWinPattern(tiles);
        if (win != null) {
            ending = true;

            for (TileCoord coord : win.getPattern().getTileCoords()) {
                Tile tile = getTile(coord.getRow(), coord.getColumn());
                if (tile != null) {
                    tile.getBox().getStyleClass().addAll("tile-win");
                }
            }

            if (win.getState() == TileState.X) {
                playerxScore.setText(Integer.toString(++xscore));
            } else if (win.getState() == TileState.O) {
                playeroScore.setText(Integer.toString(++oscore));
            }

            if (round == maxRounds) {
                Transition t = new FadeOutDownBigTransition(grid);
                t.setRate(.85);
                t.playFromStart();
                t.setOnFinished(ev -> {
                    resetRound(true);
                    end();

                    Media media = null;
                    try {
                        media = new Media(TicTacGoal.class.getResource("/goal.mp3").toURI().toString());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    if (media != null) {
                        MediaPlayer mp = new MediaPlayer(media);
//                        mp.setRate(.5);
                        mp.setStartTime(Duration.millis(9500));
                        mp.setStopTime(Duration.millis(14800));
                        mp.play();

                        second = false;
                        mp.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                            if (newValue.greaterThan(Duration.millis(11000)) && !second) {
                                second = true;

                                try {
                                    MediaPlayer omg = new MediaPlayer(new Media(TicTacGoal.class.getResource("/omg.mp3").toURI().toString()));
                                    omg.setStartTime(Duration.millis(7600));
                                    omg.setStopTime(Duration.millis(12500));
                                    omg.play();
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        mp.setOnEndOfMedia(() -> app.setView(ViewType.MENU));
                    }

                    app.setView(ViewType.END);
                    app.getEndView().winner(win.getState());
                });
            } else {
                Transition t = new FadeOutRightBigTransition(grid);
                t.setRate(.5);
                t.playFromStart();
                t.setOnFinished(ev -> resetRound(false));
            }
        } else if (isFull()) {
            for (Tile tile : tiles) {
                tile.getBox().getStyleClass().addAll("tile-tie");
            }
            Transition t = new ShakeTransition(grid);
            t.setRate(.6);
            t.playFromStart();
            t.setOnFinished(ev -> {
                resetRound(true);
//                app.getAudioManager().play("rko");
            });
        }
    }

    boolean second = false;

    private void resetRound(boolean tie) {
        for (Tile tile : tiles) {
            tile.getBox().getStyleClass().remove("tile-win");
            tile.getBox().getStyleClass().remove("tile-tie");
        }

        clear();
        grid.setOpacity(1);

        if (!tie) {
            if (round++ > maxRounds) {
                end();
            }
            updateRound();
        }

        ending = false;
        turn = TileState.X;
    }

    private void updateRound() {
        roundLabel.setText("Round " + round + " of " + maxRounds);
    }

    private Tile getTile(int row, int col) {
        for (Tile tile : tiles) {
            if (tile.getRow() == row && tile.getColumn() == col) {
                return tile;
            }
        }
        return null;
    }

    private boolean isFull() {
        for (Tile tile : tiles) {
            if (tile.getState() == TileState.EMPTY) {
                return false;
            }
        }
        return true;
    }

    private void clear() {
        for (Tile tile : tiles) {
            tile.setState(TileState.EMPTY);
            tile.getButton().getStyleClass().remove("click-x");
            tile.getButton().getStyleClass().remove("click-o");
            tile.getButton().setText("");
        }
    }
}
