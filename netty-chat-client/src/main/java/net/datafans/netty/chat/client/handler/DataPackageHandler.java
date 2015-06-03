package net.datafans.netty.chat.client.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import net.datafans.netty.common.session.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataPackageHandler extends ChannelHandlerAdapter {

	private Logger logger = LoggerFactory.getLogger(DataPackageHandler.class);
	private Session session;

	private AttributeKey<Session> sk = AttributeKey.valueOf("session");

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {

		logger.info(msg.toString());
		if (session != null) {
			session.setLastActiveTimeToNow();
		}

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error("CHANNEL_EXCEPTION " + cause);
		ChannelFuture future = ctx.close();
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				logger.info("CHANNEL_CLOSED");
			}
		});

	}

	@Override
	public void channelActive(final ChannelHandlerContext ctx) {
		logger.info("CHANNEL_ACTIVE " + ctx.channel().remoteAddress());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("CHANNEL_INACTIVE " + ctx.channel().remoteAddress());
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		logger.info("CHANNEL_READ_COMPLETED " + ctx.channel().remoteAddress());
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		logger.info("CHANNEL_REGISTERED");
		session = new Session(ctx);
		session.setLastActiveTimeToNow();
		ctx.channel().attr(sk).set(session);
	}

	public Session getSession() {
		return session;
	}
}
