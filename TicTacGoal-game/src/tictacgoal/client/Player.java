package tictacgoal.client;

public class Player {

    private final String name;
    private TileState position;
    private int score;

    public Player(String name, TileState position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public TileState getPosition() {
        return position;
    }

    public int getScore() {
        return score;
    }

    public void setPosition(TileState position) {
        this.position = position;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Player && ((Player) o).name.equals(name);
    }
}
