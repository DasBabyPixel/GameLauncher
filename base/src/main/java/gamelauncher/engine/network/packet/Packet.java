package gamelauncher.engine.network.packet;

/**
 * @author DasBabyPixel
 */
public abstract class Packet implements BufferObject {

    private final String key;

    public Packet(String key) {
        this.key = key;
    }

    @Override public final void write(PacketBuffer buffer) {
        write0(buffer);
    }

    @Override public final void read(PacketBuffer buffer) {
        read0(buffer);
    }

    /**
     * @return the unique key to identify this packet type with
     */
    public String getKey() {
        return key;
    }

    protected abstract void write0(PacketBuffer buffer);

    protected abstract void read0(PacketBuffer buffer);

}
