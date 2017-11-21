package tictacgoal.protocol;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum Protocol {

    REQUEST_MATCH,
    
    PLAYER_CONNECT,
    PLAYER_DISCONNECT,
    PLAYER_LIST,

    MATCH_START,
    MATCH_END,

    MOVE;

    private static final Map<Short, Protocol> VALUES;
    
    static {
        Protocol[] headers = values();
        VALUES = new HashMap<>(headers.length);
        for (Protocol header : headers) {
            VALUES.put(header.id, header);
        }
    }

    private final short id = (short) ordinal();

    public short getId() {
        return id;
    }

    public BufferedPacket writeHeader(BufferedPacket buf) {
        return buf.writeShort(id);
    }

    public BufferedPacket buffer() {
        return writeHeader(new BufferedPacket());
    }

    public BufferedPacket buffer(int initialCapacity) {
        return writeHeader(new BufferedPacket(initialCapacity + 1));
    }

    public <T> BufferedPacket construct(Collection<T> values) {
        return buffer(values.size()).writeAll(values);
    }
    
    public BufferedPacket construct(Object... values) {
        return buffer(values.length).writeAll(values);
    }
    
    public static Protocol valueOf(short id) {
        return VALUES.get(id);
    }

    public static Protocol valueOf(BufferedPacket buf) {
        return valueOf(buf.readShort());
    }

    public static final int PORT = 8085;
}
