package net.datafans.netty.chat.standalone.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.util.Map;

import net.datafans.netty.chat.common.entity.DataPackage;
import net.datafans.netty.chat.common.util.ByteUtil;
import net.datafans.netty.chat.standalone.session.SessionMap;
import net.datafans.netty.common.biz.decoder.BizDecoder;
import net.datafans.netty.common.biz.filter.BizFilter;
import net.datafans.netty.common.biz.handler.BizHandler;
import net.datafans.netty.common.session.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataPackageHandler extends ChannelHandlerAdapter {

	private Logger logger = LoggerFactory.getLogger(DataPackageHandler.class);
	private Session session;

	private AttributeKey<Session> sk = AttributeKey.valueOf("session");

	private Map<Integer, BizHandler<DataPackage>> handlerMap;
	private Map<Integer, BizFilter<DataPackage>> filterMap;
	private Map<Integer, BizDecoder<DataPackage>> decoderMap;

	public DataPackageHandler(Map<Integer, BizHandler<DataPackage>> handlerMap,
			Map<Integer, BizFilter<DataPackage>> filterMap, Map<Integer, BizDecoder<DataPackage>> decoderMap) {
		super();
		this.handlerMap = handlerMap;
		this.filterMap = filterMap;
		this.decoderMap = decoderMap;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {

		logger.info(msg.toString());

		if (msg != null && msg instanceof DataPackage) {
			DataPackage pkg = (DataPackage) msg;
			Integer bizType = ByteUtil.toInt(pkg.getType());

			if (session != null) {

				BizFilter<DataPackage> filter = filterMap.get(bizType);
				if (filter != null) {
					filter.doFilter(session, pkg);
				}

				BizDecoder<DataPackage> decoder = decoderMap.get(bizType);
				if (decoder != null) {
					decoder.decode(session, pkg);
				}

				BizHandler<DataPackage> handler = handlerMap.get(bizType);
				if (handler != null) {
					handler.handle(session, pkg);
				}
			}

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
		session = new Session(ctx);
		session.setLastActiveTimeToNow();
		ctx.channel().attr(sk).set(session);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("CHANNEL_INACTIVE " + ctx.channel().remoteAddress());
		if (session.isValid()) {
			SessionMap.sharedInstance().removeSession(session.getSessionUniqueId());
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		logger.info("CHANNEL_READ_COMPLETED " + ctx.channel().remoteAddress());
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		logger.info("CHANNEL_REGISTERED");

	}

	public Session getSession() {
		return session;
	}

	public static class Builder {

		private Map<Integer, BizHandler<DataPackage>> handlerMap;
		private Map<Integer, BizFilter<DataPackage>> filterMap;
		private Map<Integer, BizDecoder<DataPackage>> decoderMap;

		public Builder setDecoder(Map<Integer, BizDecoder<DataPackage>> decoderMap) {
			this.decoderMap = decoderMap;
			return this;
		}

		public Builder setFilter(Map<Integer, BizFilter<DataPackage>> filterMap) {
			this.filterMap = filterMap;
			return this;
		}

		public Builder setHandler(Map<Integer, BizHandler<DataPackage>> handlerMap) {
			this.handlerMap = handlerMap;
			return this;
		}

		public DataPackageHandler build() {
			return new DataPackageHandler(handlerMap, filterMap, decoderMap);
		}
	}
}
