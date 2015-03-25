package net.datafans.netty.common.shutdown;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Shutdown {

	private static final Logger logger = LoggerFactory.getLogger(Shutdown.class);

	private static Shutdown shutdown;
	private List<ShutdownListener> listeners = new ArrayList<ShutdownListener>();

	public synchronized static Shutdown sharedInstance() {
		if (shutdown == null) {
			shutdown = new Shutdown();
			shutdown.addShutdownHook();
		}
		return shutdown;
	}

	private void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				for (ShutdownListener listener : listeners) {
					listener.shutdown();
				}

				logger.info("APP_SHUTDOWN_SUCCESSFULLY!");
			}
		});
	}

	public void addListener(ShutdownListener listener) {
		listeners.add(listener);
	}
}
