package net.datafans.netty.common;

import java.util.ArrayList;
import java.util.List;

import net.datafans.netty.common.handler.ChannelHandlerFactory;

public interface NettyConfig {

	List<ChannelHandlerFactory> handlerList = new ArrayList<>();

	void setHandlerList(final List<ChannelHandlerFactory> handlerList);

	int getPort();

}
