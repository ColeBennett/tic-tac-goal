package tictacgoal.protocol;

public enum TileState {

    EMPTY(""),
    X("X"),
    O("O");

    private final String symb;

    TileState(String symb) {
        this.symb = symb;
    }

    public String getSymbol() {
        return symb;
    }

    public static TileState from(String symb) {
        if (symb.equals(X.symb)) {
            return X;
        } else if (symb.equals(O.symb)) {
            return O;
        }
        return EMPTY;
    }
}
