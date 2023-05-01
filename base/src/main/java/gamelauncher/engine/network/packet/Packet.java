package gamelauncher.engine.network.packet;

import gamelauncher.engine.data.DataBuffer;
import gamelauncher.engine.data.DataSerializable;

/**
 * @author DasBabyPixel
 */
public abstract class Packet implements DataSerializable {

    private final String key;

    public Packet(String key) {
        this.key = key;
    }

    @Override public final void write(DataBuffer buffer) {
        write0(buffer);
    }

    @Override public final void read(DataBuffer buffer) {
        read0(buffer);
    }

    /**
     * @return the unique key to identify this packet type with
     */
    public String key() {
        return key;
    }

    protected abstract void write0(DataBuffer buffer);

    protected abstract void read0(DataBuffer buffer);

}
