package tictacgoal.client;

import java.util.*;
import java.util.Map.Entry;

public class TilePattern {

    public static final int WINNING_TILES = 3;
    public static final Set<TilePattern> PATTERNS = new HashSet<>(9);

    public static final class TileCoord {
        private final int row, col;

        TileCoord(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return col;
        }
    }

    static {
        // Across
        PATTERNS.add(new TilePattern(new TileCoord(0, 0), new TileCoord(0, 1), new TileCoord(0, 2)));
        PATTERNS.add(new TilePattern(new TileCoord(1, 0), new TileCoord(1, 1), new TileCoord(1, 2)));
        PATTERNS.add(new TilePattern(new TileCoord(2, 0), new TileCoord(2, 1), new TileCoord(2, 2)));

        // Down
        PATTERNS.add(new TilePattern(new TileCoord(0, 0), new TileCoord(1, 0), new TileCoord(2, 0)));
        PATTERNS.add(new TilePattern(new TileCoord(0, 1), new TileCoord(1, 1), new TileCoord(2, 1)));
        PATTERNS.add(new TilePattern(new TileCoord(0, 2), new TileCoord(1, 2), new TileCoord(2, 2)));

        // Diagonal
        PATTERNS.add(new TilePattern(new TileCoord(0, 0), new TileCoord(1, 1), new TileCoord(2, 2)));
        PATTERNS.add(new TilePattern(new TileCoord(0, 2), new TileCoord(1, 1), new TileCoord(2, 0)));
    }

    private final TileCoord[] coords;

    private TilePattern(TileCoord... coords) {
        this.coords = coords;
    }

    public TileCoord[] getTileCoords() {
        return coords;
    }

    /**
     * Find the next best play for the given player.
     * @param tiles board tiles
     * @param ai computer player
     * @return the tile to be used for next play
     */
    public static Tile nextPlay(Tile[] tiles, Player ai) {
        TileState turn = ai.getPosition();

        /**
         * Try to block the opponent.
         */
        TileState opp = turn == TileState.X ? TileState.O : TileState.X;
        Tile blockMove = bestMove(tiles, opp);
        if (blockMove != null) {
            return blockMove;
        }

        /**
         * Find best move for player.
         */
        Tile move = bestMove(tiles, turn);
        if (move != null) {
            return move;
        }

        /**
         * If all rules fail to determine the next play, select an empty tile.
         */
        for (Tile tile : tiles) {
            if (tile.getState() == TileState.EMPTY) {
                System.out.println("Chose an empty tile for computer.");
                return tile;
            }
        }
        return null;
    }

    /**
     * Find the best move for the given position.
     * @param tiles board tiles
     * @param playing position of the player
     * @return best available tile to make the next move
     */
    private static Tile bestMove(Tile[] tiles, TileState playing) {
        /*
         Iterate through all patterns and map used patterns that the given
         position has plays in to the number of plays they have in the pattern.
         */
        Map<TilePattern, Integer> ranking = new HashMap<>();
        for (TilePattern pattern : PATTERNS) {
            int plays = 0;
            for (TileCoord coord : pattern.getTileCoords()) {
                Tile tile = getTile(tiles, coord);
                if (tile != null) {
                    if (tile.getState() == playing) {
                        plays++;
                    }
                }
            }
            if (plays != 0) {
                ranking.put(pattern, plays);
            }
        }
        /*
         Then find the pattern entry with the highest number of plays and
         return the first empty tile from that pattern.
        */
        int max = -1;
        TilePattern pat = null;
        for (Entry<TilePattern, Integer> entry : ranking.entrySet()) {
            int plays = entry.getValue();
            if (plays > max) {
                max = plays;
                pat = entry.getKey();
            }
        }
        if (max != -1) {
            Tile avail = firstAvailable(tiles, pat);
            if (avail != null) {
                return avail;
            }
        }
        return null;
    }

    /**
     * Find the first available empty tile in the given pattern.
     * @param tiles board tiles
     * @param pattern pattern to check
     * @return first available empty tile
     */
    private static Tile firstAvailable(Tile[] tiles, TilePattern pattern) {
        for (Tile tile : tiles) {
            for (TileCoord coord : pattern.getTileCoords()) {
                if (tile.equals(coord) && tile.getState() == TileState.EMPTY) {
                    return tile;
                }
            }
        }
        return null;
    }

    /**
     * Find a tile using the given coordinates.
     * @param tiles board tiles
     * @param coord coordinates to check
     * @return tile matching the given coordinates
     */
    public static Tile getTile(Tile[] tiles, TileCoord coord) {
        for (Tile tile : tiles) {
            if (tile.equals(coord)) {
                return tile;
            }
        }
        return null;
    }

    /**
     * Find a valid win pattern and return the winning state and pattern.
     * @param tiles board tiles
     * @return the winning pattern and state
     */
    public static WinPattern findWinPattern(Tile[] tiles) {
        for (TilePattern pattern : PATTERNS) {
            int steps = 0;
            TileState prev = null;
            for (TileCoord coord : pattern.getTileCoords()) {
                for (Tile tile : tiles) {
                    TileState state = tile.getState();
                    if (state != TileState.EMPTY && tile.equals(coord)) {
                        if (prev == null) {
                            prev = state;
                            steps++;
                            continue;
                        }
                        if (state == prev) {
                            steps++;
                            if (steps == WINNING_TILES) {
                                return new WinPattern(prev, pattern);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static final class WinPattern {
        private final TileState state;
        private final TilePattern pattern;

        WinPattern(TileState state, TilePattern pattern) {
            this.state = state;
            this.pattern = pattern;
        }

        public TileState getState() {
            return state;
        }

        public TilePattern getPattern() {
            return pattern;
        }
    }
}
