module gamelauncher.netty {
    exports gamelauncher.netty;
    exports gamelauncher.netty.standalone;
    exports gamelauncher.netty.standalone.handler;
    exports gamelauncher.netty.standalone.packet.c2s;
    exports gamelauncher.netty.standalone.packet.s2c;
    requires transitive gamelauncher.api;
    requires io.netty.common;
    requires io.netty.codec;
    requires io.netty.transport;
    requires io.netty.buffer;
    requires io.netty.codec.http;
    requires io.netty.handler;
    requires io.netty.handler.proxy;
    requires transitive java.net.http;
    requires org.fusesource.jansi;
    requires org.bouncycastle.tls;
    requires org.bouncycastle.pkix;
    requires org.bouncycastle.provider;
}
