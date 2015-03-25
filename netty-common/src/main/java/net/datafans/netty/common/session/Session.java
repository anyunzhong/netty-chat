package net.datafans.netty.common.session;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Session {

	private Logger logger = LoggerFactory.getLogger(Session.class);

	private ChannelHandlerContext ctx;

	private Map<String, Object> map = new HashMap<String, Object>();

	public Session(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	public synchronized void set(String key, Object value) {
		map.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> T get(String key) {
		return (T) map.get(key);
	}

	public void setLastActiveTimeToNow() {
		set(Key.LAST_ACTIVE_TIME, System.currentTimeMillis());
	}

	public long getLastActiveTime() {
		return get(Key.LAST_ACTIVE_TIME);
	}

	public void setSessionUniqueId(Integer uniqueId) {
		set(Key.SESSION_UNIQUE_ID, uniqueId);
	}

	public Integer getSessionUniqueId() {
		return get(Key.SESSION_UNIQUE_ID);
	}

	public boolean isValid() {
		return getSessionUniqueId() != null ? true : false;
	}

	public synchronized void write(Object pkg) {
		ctx.writeAndFlush(pkg);
	}

	public synchronized void writeAndClose(Object pkg) {
		ChannelFuture future = ctx.writeAndFlush(pkg);
		future.addListener(ChannelFutureListener.CLOSE);
	}

	public synchronized void close() {
		ChannelFuture future = ctx.close();
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					logger.info("CHANNEL_CLOSED ");
				}
			}
		});
	}

	private static class Key {
		private final static String LAST_ACTIVE_TIME = "last_active_time";
		private final static String SESSION_UNIQUE_ID = "session_unique_id";
	}
}
