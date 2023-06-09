/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import de.dasbabypixel.api.property.InvalidationListener;
import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.network.Connection;
import gamelauncher.engine.network.packet.Packet;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.property.PropertyUtil;
import io.netty.channel.Channel;
import java8.util.concurrent.CompletableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractConnection extends AbstractGameResource implements Connection {
    protected final Property<State> state = Property.withValue(State.CLOSED);
    private final Property<State> stateUnmodifiable = PropertyUtil.unmodifiable(state);
    private final Executor cached;
    private Channel channel;

    public AbstractConnection(Executor cached) {
        this.cached = cached;
    }

    protected void init(Channel channel) {
        this.channel = channel;
    }

    @Override public Property<State> state() {
        return stateUnmodifiable;
    }

    @Override public StateEnsurance ensureState(State state) {
        CompletableFuture<Void> f = new CompletableFuture<>();
        InvalidationListener l = p -> {
            if (p.value() == state) {
                f.completeAsync(() -> null, cached);
            }
        };
        this.state.addListener(l);
        if (this.state.value() == state) {
            this.state.removeListener(l);
            return new Completed(state);
        }
        f.exceptionally(t -> null).thenRun(() -> this.state.removeListener(l));
        return new StateEnsurance() {
            private long time = 5;
            private TimeUnit unit = TimeUnit.SECONDS;
            private TimeoutHandler timeoutHandler;

            @Override public StateEnsurance timeoutAfter(long time, TimeUnit unit) {
                this.time = time;
                this.unit = unit;
                return this;
            }

            @Override public StateEnsurance timeoutHandler(TimeoutHandler timeoutHandler) {
                this.timeoutHandler = timeoutHandler;
                return this;
            }

            @Override public State await() {
                try {
                    f.get(time, unit);
                    return state;
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (TimeoutException e) {
                    State s = AbstractConnection.this.state.value();
                    if (s == state) return s;
                    if (timeoutHandler != null) timeoutHandler.timeout(s);
                    return s;
                }
            }
        };
    }

    @Override public void sendPacket(Packet packet) {
        channel.writeAndFlush(packet, channel.voidPromise());
    }

    @Override public CompletableFuture<Void> sendPacketAsync(Packet packet) {
        CompletableFuture<Void> fut = new CompletableFuture<>();
        channel.writeAndFlush(packet).addListener(f -> {
            if (f.isSuccess()) fut.complete(null);
            else fut.completeExceptionally(f.cause());
        });
        return fut;
    }

    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        CompletableFuture<Void> f = new CompletableFuture<>();
        channel.close().addListener(fut -> {
            if (fut.isSuccess()) f.complete(null);
            else f.completeExceptionally(fut.cause());
        });
        return f;
    }

    private static class Completed implements StateEnsurance {
        private final State state;

        public Completed(State state) {
            this.state = state;
        }

        @Override public StateEnsurance timeoutAfter(long time, TimeUnit unit) {
            return this;
        }

        @Override public StateEnsurance timeoutHandler(TimeoutHandler timeoutHandler) {
            return this;
        }

        @Override public State await() {
            return state;
        }
    }
}
