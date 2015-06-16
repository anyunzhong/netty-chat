package net.datafans.netty.common.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.AttributeKey;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.datafans.netty.common.NettyConfig;
import net.datafans.netty.common.NettyLifecycle;
import net.datafans.netty.common.boot.UncaughtExceptionUtil;
import net.datafans.netty.common.client.config.Config;
import net.datafans.netty.common.config.GlobalConfig;
import net.datafans.netty.common.constant.ChannelState;
import net.datafans.netty.common.handler.ChannelHandlerFactory;
import net.datafans.netty.common.session.Session;
import net.datafans.netty.common.shutdown.Shutdown;
import net.datafans.netty.common.shutdown.ShutdownListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NettyClient implements NettyLifecycle, NettyConfig {

	private Logger logger = LoggerFactory.getLogger(NettyClient.class);

	private Channel channel;

	private ExecutorService pool = Executors.newCachedThreadPool();

	private Bootstrap bootstrap;
	private EventLoopGroup workGroup;

	private ChannelState state;

	private boolean isTerminate = false;

	private int restartTryTimes = 0;
	private int heartbeatFailTimes = 0;

	protected NettyClient() {

		try {
			init();
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	private void init() throws Exception {

		state = ChannelState.CLOSED;

		UncaughtExceptionUtil.declare();

		Shutdown.sharedInstance().addListener(shutdownListener);

		setHandlerList(handlerList);

		workGroup = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap.group(workGroup);
		bootstrap.channel(NioSocketChannel.class);

		if (handlerList.isEmpty()) {
			throw new Exception("HANDLER_LIST_EMPTY");
		}

		setBootstrapHandler(bootstrap, handlerList);
		setBootstrapOption(bootstrap);

		autoRestartWhenClosed();
		autoSendHeartbeat();
		autoDetectHeartbeart();
	}

	@Override
	public void start() {

		logger.info(state.toString());
		if (state != ChannelState.CLOSED) {
			return;
		}
		try {

			if (getPort() <= 0) {
				throw new Exception("LISTEN_PORT_ILLEGAL");
			}
			ChannelFuture future = bootstrap.connect(getHost(), getPort());
			state = ChannelState.CONNECTING;
			future.addListener(channelStartListener);

			future.channel().closeFuture().sync();

		} catch (Exception e) {
			state = ChannelState.CLOSED;
			logger.error(e.toString());
		}

	}

	@Override
	public void stop() {

		logger.info(state.toString());
		if (state == ChannelState.CLOSED) {
			return;
		}
		if (channel != null) {
			ChannelFuture future = channel.close();
			state = ChannelState.CLOSING;
			future.addListener(channelStopListener);
		}

	}

	@Override
	public void terminate() {
		stop();

		setTerminate(true);
		workGroup.shutdownGracefully();
		logger.info("WORKGROUP_SHUTDOWN_GRACEFULLY");
		pool.shutdownNow();
		logger.info("POOL_SHUTDOWN_GRACEFULLY");
	}

	private void autoRestartWhenClosed() {
		pool.execute(autoRestartTask);
	}

	private void autoSendHeartbeat() {
		pool.execute(autoSendHeartbeatTask);
	}

	private void autoDetectHeartbeart() {
		pool.execute(autoDetectHeartbeatTask);
	}

	private void setBootstrapHandler(final Bootstrap bootstrap, final List<ChannelHandlerFactory> handlers) {

		bootstrap.handler(new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel ch) throws Exception {

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

	private void setBootstrapOption(final Bootstrap bootstrap) {

		bootstrap.option(ChannelOption.ALLOCATOR, optionByteBufAllocator());
		bootstrap.option(ChannelOption.SO_KEEPALIVE, optionSocketKeepAlive());
	}

	protected ByteBufAllocator optionByteBufAllocator() {
		return PooledByteBufAllocator.DEFAULT;
	}

	protected boolean optionSocketKeepAlive() {
		return true;
	}

	protected boolean enableFrameDecoder() {
		return false;
	}

	protected void setFrameDecoderConfig(final GlobalConfig.FrameDecoder config) {

	}

	protected int autoReconnectTimesThreshold() {
		return Config.Client.AUTO_RECONNECT_TIMES_THRESSHOLD;
	}

	protected int defaultHeartbeatInterval() {
		return Config.Client.DEFAULT_HEARTBEAT_INTERVAL_MINISECONDS;
	}

	protected long defaultHeartbeatTimeout() {
		return Config.Client.DEFAULT_HEARTBEAT_TIMEOUT_MINISECONDS;
	}

	protected int defaultHeartbeatSendFailThreshold() {
		return Config.Client.DEFAULT_HEARTBEAT_SEND_FAIL_THRESSHOLD;
	}

	protected abstract Object getHeartbeatDataPackage();

	protected abstract String getHost();

	public boolean isTerminate() {
		return isTerminate;
	}

	private void setTerminate(boolean isTerminate) {
		this.isTerminate = isTerminate;
	}

	private ChannelFutureListener channelStartListener = new ChannelFutureListener() {

		@Override
		public void operationComplete(ChannelFuture future) throws Exception {

			if (future.isSuccess()) {
				logger.info("CHANNEL_OPENED " + channel);
				state = ChannelState.RUNNING;
				restartTryTimes = 0;
				channel = future.channel();
			} else {
				logger.error("CHANNEL_CONNECTION_ERROR " + future.cause());
				state = ChannelState.CLOSED;
				channel = null;
			}

		}
	};

	private ChannelFutureListener channelStopListener = new ChannelFutureListener() {
		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			channel = null;
			state = ChannelState.CLOSED;
			logger.info("CHANNEL_CLOSED");
		}
	};

	private ChannelFutureListener heartbeatSendListener = new ChannelFutureListener() {
		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			if (future.isSuccess()) {
				logger.info("AUTO_SEND_HEARTBEAT_DONE");
				heartbeatFailTimes = 0;
			} else {
				logger.error("AUTO_SEND_HEARTBEAT_ERROR");
				heartbeatFailTimes++;
				// 心跳失败
				if (heartbeatFailTimes >= defaultHeartbeatSendFailThreshold()) {
					stop();
				}
			}

		}
	};

	private Runnable autoRestartTask = new Runnable() {
		public void run() {

			while (true) {
				if (restartTryTimes > autoReconnectTimesThreshold()) {
					logger.info("AUTO_RESTART_TERMINATE");
					NettyClient.this.terminate();
					break;
				}
				try {
					Thread.sleep(1000);
					if (state == ChannelState.CLOSED) {
						restartTryTimes++;
						logger.info("AUTO_RESTART_TIME " + restartTryTimes);
						NettyClient.this.start();
					}
				} catch (Exception e) {
					logger.error("AUTO_RESTART_THREAD_EXCEPTION " + e);
				}
			}

		}
	};

	private Runnable autoSendHeartbeatTask = new Runnable() {
		public void run() {
			while (true) {
				if (isTerminate()) {
					logger.info("AUTO_SEND_HEARTBEAT_TERMINATE");
					break;
				}
				try {

					if (channel != null) {
						ChannelFuture future = channel.writeAndFlush(getHeartbeatDataPackage());
						future.addListener(heartbeatSendListener);
					}

					Thread.sleep(defaultHeartbeatInterval());

				} catch (Exception e) {
					logger.error("AUTO_SEND_HEARTBEAT_THREAD_EXCEPTION " + e);
				}
			}

		}
	};

	private Runnable autoDetectHeartbeatTask = new Runnable() {
		public void run() {
			while (true) {
				try {
					if (isTerminate()) {
						logger.info("AUTO_DETECT_HEARTBEAT_TERMINATE");
						break;
					}
					Thread.sleep(1000);

					Session session = getSession();
					if (session != null) {
						long interval = System.currentTimeMillis() - session.getLastActiveTime();

						// 心跳超时
						if (interval > defaultHeartbeatTimeout()) {
							logger.info("AUTO_DETECT_HEARTBEAT_TIMEOUT");
							stop();
						}
					}

				} catch (Exception e) {
					logger.error("AUTO_DETECT_HEARTBEAT_THREAD_EXCEPTION " + e);
				}
			}

		}
	};

	private ShutdownListener shutdownListener = new ShutdownListener() {

		@Override
		public void shutdown() {
			terminate();
		}
	};

	private Session getSession() {

		if (channel != null) {
			Object o = channel.attr(AttributeKey.valueOf("session")).get();
			if (o != null && o instanceof Session) {
				Session session = (Session) o;
				return session;
			}
		}

		return null;
	}

	public void write(Object pkg) {
		Session session = getSession();
		if (session == null) {
			return;
		}

		session.writeAndClose(pkg);
	}

}
