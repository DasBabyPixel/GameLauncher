package gamelauncher.engine.network.packet;

import de.dasbabypixel.annotations.Api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author DasBabyPixel
 */
public class PacketRegistry {

    private final Map<Class<? extends Packet>, Entry<? extends Packet>> entryMap = new ConcurrentHashMap<>();

    private final Map<Integer, Class<? extends Packet>> classById = new ConcurrentHashMap<>();

    /**
     * Registers a packet
     */
    public final <T extends Packet> void register(Class<T> clazz, Supplier<T> constructor) {
        Entry<T> entry = new Entry<>(constructor);
        entryMap.put(clazz, entry);
        classById.put(constructor.get().key().hashCode(), clazz);
    }

    /**
     * Unregisters a packet
     */
    @Api public final void unregister(Class<? extends Packet> clazz) throws PacketNotRegisteredException {
        if (!entryMap.containsKey(clazz)) {
            throw new PacketNotRegisteredException(clazz.getName());
        }
        classById.remove(entryMap.remove(clazz).constructor.get().key().hashCode());
    }

    /**
     * @return an empty packet instance of the specified type
     */
    public final <T extends Packet> T createPacket(Class<T> clazz) throws PacketNotRegisteredException {
        if (entryMap.containsKey(clazz)) {
            return clazz.cast(entryMap.get(clazz).constructor.get());
        }
        throw new PacketNotRegisteredException(clazz.getName());
    }

    /**
     * @return the packet type for the specified id
     */
    public final Class<? extends Packet> getPacketType(int id) throws PacketNotRegisteredException {
        if (classById.containsKey(id)) {
            return classById.get(id);
        }
        throw new PacketNotRegisteredException(Integer.toString(id));
    }

    private static class Entry<T> {

        /**
         * The constructor of the packet
         */
        public final Supplier<T> constructor;

        public Entry(Supplier<T> constructor) {
            this.constructor = constructor;
        }
    }
}
