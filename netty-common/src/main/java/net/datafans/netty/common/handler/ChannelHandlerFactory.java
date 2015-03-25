package net.datafans.netty.common.handler;

import io.netty.channel.ChannelHandler;

public interface ChannelHandlerFactory {
	ChannelHandler build();
}
