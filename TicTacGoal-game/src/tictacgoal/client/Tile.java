package tictacgoal.client;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import tictacgoal.client.TilePattern.TileCoord;

public class Tile {

    private int row, col;
    private TileState state = TileState.EMPTY;
    private HBox box;
    private Button button;

    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return col;
    }

    public TileState getState() {
        return state;
    }

    public HBox getBox() {
        return box;
    }

    public Button getButton() {
        return button;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int col) {
        this.col = col;
    }

    public void setState(TileState state) {
        if (state == null) {
            state = TileState.EMPTY;
        }
        this.state = state;
    }

    public void setBox(HBox box) {
        this.box = box;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public boolean equals(TileCoord coord) {
        return coord.getRow() == row && coord.getColumn() == col;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tile)) return false;
        Tile t = (Tile) o;
        return t.getRow() == row && t.getColumn() == col;
    }

    @Override
    public String toString() {
        return "(row: " + row + ", col: " + col + ", state: " +  state + ')';
    }
}
