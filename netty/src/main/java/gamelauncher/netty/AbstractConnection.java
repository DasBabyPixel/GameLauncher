/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.network.Connection;
import gamelauncher.engine.network.NetworkAddress;
import gamelauncher.engine.network.packet.Packet;
import gamelauncher.engine.network.packet.PacketHandler;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.property.PropertyUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;
import java8.util.concurrent.CompletableFuture;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractConnection extends AbstractGameResource implements Connection {
    protected final Property<State> state = Property.withValue(State.CLOSED);
    private final Property<State> stateUnmodifiable = PropertyUtil.unmodifiable(state);
    private final Executor cached;
    private final NettyNetworkClient networkClient;
    private final Lock handlerLock = new ReentrantLock(true);
    private final Map<Class<?>, Collection<HandlerEntry<?>>> handlers = new ConcurrentHashMap<>();
    private final Collection<HandlerEntry<Packet>> genericHandlers = new CopyOnWriteArrayList<>();
    private final GenericFutureListener<ChannelFuture> closeListener = f -> {
        if (!cleanedUp()) cleanup();
    };
    private Channel channel;
    private NetworkAddress remoteAddress;
    private NetworkAddress localAddress;

    public AbstractConnection(Executor cached, NettyNetworkClient networkClient) {
        this.cached = cached;
        this.networkClient = networkClient;
    }

    public void remoteAddress(NetworkAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public void localAddress(NetworkAddress localAddress) {
        this.localAddress = localAddress;
    }

    @Override public NetworkAddress remoteAddress() {
        return remoteAddress;
    }

    @Override public NetworkAddress localAddress() {
        return localAddress;
    }

    @Override public Property<State> state() {
        return stateUnmodifiable;
    }

    public Channel channel() {
        return channel;
    }

    @Override public NettyNetworkClient networkClient() {
        return networkClient;
    }

    public boolean handle(Packet packet) {
        Collection<HandlerEntry<?>> col;
        try {
            handlerLock.lock();
            col = handlers.get(packet.getClass());
        } finally {
            handlerLock.unlock();
        }
        if (!handleGeneric(packet)) if (col == null) return false;
        for (HandlerEntry<?> h : col) {
            h.receivePacket(this, packet);
        }
        return true;
    }

    @Override public <T extends Packet> void addHandler(Class<T> packetTpye, PacketHandler<T> handler) {
        handlerLock.lock();
        if (!handlers.containsKey(packetTpye)) {
            handlers.put(packetTpye, ConcurrentHashMap.newKeySet());
        }
        handlers.get(packetTpye).add(new HandlerEntry<>(packetTpye, handler));
        handlerLock.unlock();
    }

    @Override public <T extends Packet> void removeHandler(Class<T> packetType, PacketHandler<T> handler) {
        handlerLock.lock();
        if (handlers.containsKey(packetType)) {
            Collection<HandlerEntry<?>> col = handlers.get(packetType);
            col.removeIf(he -> he.clazz() == packetType && he.handler == handler);
            if (col.isEmpty()) handlers.remove(packetType);

        }
        handlerLock.unlock();
    }

    @Override public StateEnsurance ensureState(State state) {
        return new PropertyStateEnsurance(this.state, state);
    }

    @Override public void sendPacket(Packet packet) {
        if (state.value() != State.CONNECTED) return;
        channel.writeAndFlush(packet, channel.voidPromise());
    }

    @Override public CompletableFuture<Void> sendPacketAsync(Packet packet) {
        if (state.value() != State.CONNECTED) throw new IllegalStateException(state.value().toString());
        CompletableFuture<Void> fut = new CompletableFuture<>();
        channel.writeAndFlush(packet).addListener(f -> {
            if (f.isSuccess()) fut.complete(null);
            else fut.completeExceptionally(f.cause());
        });
        return fut;
    }

    protected void init(Channel channel) {
        this.channel = channel;
        this.channel.closeFuture().addListener(closeListener);
    }

    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        CompletableFuture<Void> f = new CompletableFuture<>();
        channel.closeFuture().removeListener(closeListener);
        channel.close().addListener(fut -> {
            if (fut.isSuccess()) f.complete(null);
            else f.completeExceptionally(fut.cause());
        });
        return f;
    }

    private boolean handleGeneric(Packet packet) {
        if (genericHandlers.isEmpty()) return false;
        for (HandlerEntry<Packet> handler : genericHandlers) {
            handler.receivePacket(this, packet);
        }
        return true;
    }

    static class HandlerEntry<T extends Packet> {
        private final Class<T> clazz;
        private final PacketHandler<T> handler;

        public HandlerEntry(Class<T> clazz, PacketHandler<T> handler) {
            this.clazz = clazz;
            this.handler = handler;
        }

        public Class<T> clazz() {
            return clazz;
        }

        public void receivePacket(Connection connection, Object packet) {
            handler.receivePacket(connection, clazz.cast(packet));
        }
    }
}
