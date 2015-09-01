package net.datafans.netty.common.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;

import net.datafans.netty.common.NettyConfig;
import net.datafans.netty.common.NettyLifecycle;
import net.datafans.netty.common.boot.UncaughtExceptionUtil;
import net.datafans.netty.common.config.GlobalConfig;
import net.datafans.netty.common.handler.ChannelHandlerFactory;
import net.datafans.netty.common.server.config.Config;
import net.datafans.netty.common.shutdown.Shutdown;
import net.datafans.netty.common.shutdown.ShutdownListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NettyServer implements NettyLifecycle, NettyConfig {

	private Logger logger = LoggerFactory.getLogger(NettyServer.class);

	private EventLoopGroup bossGroup;
	private EventLoopGroup workGroup;
	private ServerBootstrap bootstrap;
	private Channel channel;

	public NettyServer() {
		try {
			init();
		} catch (Exception e) {
			logger.error("SERVER_INIT_ERROR",e);
		}
	}

	private void init() throws Exception {

		UncaughtExceptionUtil.declare();

		Shutdown.sharedInstance().addListener(shutdownListener);

		setHandlerList(handlerList);

		bossGroup = new NioEventLoopGroup();
		workGroup = new NioEventLoopGroup();

		bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workGroup);
		bootstrap.channel(NioServerSocketChannel.class);
		if (handlerList.isEmpty()) {
			throw new Exception("HANDLER_LIST_EMPTY");
		}
		setBootstrapChildHandler(bootstrap, handlerList);
		setBootstrapOption(bootstrap);

	}

	@Override
	public void start() {
		try {
			if (getPort() <= 0) {
				throw new Exception("LISTEN_PORT_ILLEGAL");
			}
			ChannelFuture future = bootstrap.bind(getPort()).sync();
			future.addListener(startListener);
			channel = future.channel();
			future.channel().closeFuture().sync();

		} catch (Exception e) {
			logger.error("SERVER START ERROR", e);
		}
	}

	@Override
	public void stop() {
		if (channel != null) {
			ChannelFuture future = channel.close();
			future.addListener(stopListener);
		}
	}

	@Override
	public void terminate() {

		stop();

		bossGroup.shutdownGracefully();
		logger.info("BOSSGROUP_SHUTDOWN_GRACEFULLY");

		workGroup.shutdownGracefully();
		logger.info("WORKGROUP_SHUTDOWN_GRACEFULLY");

	}

	private void setBootstrapChildHandler(final ServerBootstrap bootstrap, final List<ChannelHandlerFactory> handlers) {

		bootstrap.childHandler(new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel ch) throws Exception {

				// 心跳检测
				ch.pipeline().addLast(new ReadTimeoutHandler(defaultHeartbeatTimeout(), TimeUnit.MILLISECONDS));

				if (enableFrameDecoder()) {
					GlobalConfig.FrameDecoder config = new GlobalConfig.FrameDecoder();
					setFrameDecoderConfig(config);
					ch.pipeline().addLast(
							new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, config.getOffset(), config.getLength(),
									config.getAdjustment(), 0));
				}

				for (ChannelHandlerFactory factory : handlers) {
					ch.pipeline().addLast(factory.build());
				}
			}
		});
	}

	private void setBootstrapOption(final ServerBootstrap bootstrap) {

		bootstrap.option(ChannelOption.SO_BACKLOG, optionSocketBacklog());
		bootstrap.option(ChannelOption.ALLOCATOR, optionByteBufAllocator());
		bootstrap.childOption(ChannelOption.ALLOCATOR, childOptionByteBufAllocator());
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, childOptionSocketKeepAlive());
		//bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);
	}

	protected int optionSocketBacklog() {
		return 128;
	}

	protected ByteBufAllocator optionByteBufAllocator() {
		return PooledByteBufAllocator.DEFAULT;
	}

	protected ByteBufAllocator childOptionByteBufAllocator() {
		return PooledByteBufAllocator.DEFAULT;
	}

	protected boolean childOptionSocketKeepAlive() {
		return true;
	}

	protected boolean enableFrameDecoder() {
		return false;
	}

	protected long defaultHeartbeatTimeout() {
		return Config.Server.DEFAULT_HEARTBEAT_TIMEOUT_MINISECONDS;
	}

	protected void setFrameDecoderConfig(final GlobalConfig.FrameDecoder config) {

	}

	private ChannelFutureListener startListener = new ChannelFutureListener() {

		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			if (future.isSuccess()) {
				logger.info("SERVER_START_SUCCESSFULLY");
			} else {
				logger.error("SERVER_START_FAILED");
			}
		}
	};
	private ChannelFutureListener stopListener = new ChannelFutureListener() {

		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			if (future.isSuccess()) {
				logger.info("SERVER_STOP_SUCCESSFULLY");
			} else {
				logger.error("SERVER_STOP_FAILED");
			}
		}
	};

	private ShutdownListener shutdownListener = new ShutdownListener() {

		@Override
		public void shutdown() {
			terminate();
		}
	};
}
