package net.datafans.netty.common.client.config;

public class Config {

	public static class Client {
		public final static int AUTO_RECONNECT_TIMES_THRESSHOLD = 5;
		public final static int DEFAULT_HEARTBEAT_INTERVAL_MINISECONDS = 15000;
		public final static int DEFAULT_HEARTBEAT_TIMEOUT_MINISECONDS = 15000 * 5;
		public final static int DEFAULT_HEARTBEAT_SEND_FAIL_THRESSHOLD = 3;
	}
}
