package tictacgoal.protocol.protocol;

public final class Medium {

    public static final int SIZE = 3;
    public static final int MAX_UNSIGNED_VALUE = 16777215;
    public static final int MIN_UNSIGNED_VALUE = 0;   
    public static final int MAX_SIGNED_VALUE = 8388607;
    public static final int MIN_SIGNED_VALUE = -8388608;
    
    private final int value;
    
    public Medium(int value) {
        this.value = value;
    }
    
    public int value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Medium && ((Medium) o).value == value;
    }
    
    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
