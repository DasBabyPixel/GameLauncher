/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import gamelauncher.engine.util.logging.Logger;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

public class ExceptionHandler extends ChannelDuplexHandler {
    private final Logger logger;

    public ExceptionHandler(Logger logger) {
        this.logger = logger;
    }

    @Override public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.errorf("%s: %s", ctx.channel().remoteAddress(), cause.getLocalizedMessage());

    }
}
