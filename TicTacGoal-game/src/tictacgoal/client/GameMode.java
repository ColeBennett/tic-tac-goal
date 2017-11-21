package tictacgoal.client;

public enum GameMode {

    SINGLEPLAYER("Single Player"),
    MULTIPLAYER("Multiplayer");

    private final String name;

    GameMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
