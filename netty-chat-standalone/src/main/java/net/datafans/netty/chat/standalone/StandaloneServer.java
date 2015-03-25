package net.datafans.netty.chat.standalone;

import io.netty.channel.ChannelHandler;

import java.util.List;

import net.datafans.netty.chat.common.constant.Protocal;
import net.datafans.netty.chat.common.handler.DataPackageDecoder;
import net.datafans.netty.chat.common.handler.DataPackageEncoder;
import net.datafans.netty.chat.standalone.boot.BizDecoderMapFactory;
import net.datafans.netty.chat.standalone.boot.BizFilterMapFactory;
import net.datafans.netty.chat.standalone.boot.BizHandlerMapFactory;
import net.datafans.netty.chat.standalone.handler.DataPackageHandler;
import net.datafans.netty.common.config.GlobalConfig;
import net.datafans.netty.common.handler.ChannelHandlerFactory;
import net.datafans.netty.common.server.NettyServer;

public class StandaloneServer extends NettyServer {

	private final static NettyServer server = new StandaloneServer();

	public static NettyServer sharedInstance() {
		return server;
	}

	@Override
	protected boolean enableFrameDecoder() {
		return true;
	}

	@Override
	protected void setFrameDecoderConfig(GlobalConfig.FrameDecoder config) {
		config.setOffset(0);
		config.setLength(Protocal.FIELD_PACKAGE_SIZE_LENGTH);
		config.setAdjustment(-Protocal.FIELD_PACKAGE_SIZE_LENGTH);
	}

	@Override
	public void setHandlerList(List<ChannelHandlerFactory> handlerList) {

		handlerList.add(new ChannelHandlerFactory() {
			@Override
			public ChannelHandler build() {
				return new DataPackageDecoder();
			}
		});
		handlerList.add(new ChannelHandlerFactory() {

			@Override
			public ChannelHandler build() {
				return new DataPackageEncoder();
			}
		});
		handlerList.add(new ChannelHandlerFactory() {

			@Override
			public ChannelHandler build() {
				DataPackageHandler.Builder builder = new DataPackageHandler.Builder();
				builder.setFilter(BizFilterMapFactory.sharedInstance());
				builder.setDecoder(BizDecoderMapFactory.sharedInstance());
				builder.setHandler(BizHandlerMapFactory.sharedInstance());
				return builder.build();
			}
		});

	}

	@Override
	public int getPort() {
		return 50000;
	}

	public static void main(String[] args) {
		NettyServer server = StandaloneServer.sharedInstance();
		server.start();
	}
}
