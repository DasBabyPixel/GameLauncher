module gamelauncher.netty {
    exports gamelauncher.netty;
    exports gamelauncher.netty.standalone;
    exports gamelauncher.netty.standalone.handler;
    exports gamelauncher.netty.standalone.packet.c2s;
    exports gamelauncher.netty.standalone.packet.s2c;
    requires transitive gamelauncher.api;
    requires transitive io.netty.common;
    requires transitive io.netty.codec;
    requires transitive io.netty.transport;
    requires transitive io.netty.buffer;
    requires transitive io.netty.codec.http;
    requires transitive io.netty.handler;
    requires transitive io.netty.handler.proxy;
    requires transitive java.net.http;
    requires org.fusesource.jansi;
    requires org.bouncycastle.tls;
    requires org.bouncycastle.pkix;
    requires org.bouncycastle.provider;
}
