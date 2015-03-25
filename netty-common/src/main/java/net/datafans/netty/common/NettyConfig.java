package net.datafans.netty.common;

import java.util.ArrayList;
import java.util.List;

import net.datafans.netty.common.handler.ChannelHandlerFactory;

public interface NettyConfig {

	final List<ChannelHandlerFactory> handlerList = new ArrayList<ChannelHandlerFactory>();

	void setHandlerList(final List<ChannelHandlerFactory> handlerList);

	int getPort();

}
