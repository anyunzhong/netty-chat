package net.datafans.netty.common.boot;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UncaughtExceptionUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(UncaughtExceptionUtil.class);

	private UncaughtExceptionUtil() {

	}

	public static void declare() {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				logger.error("THREAD_TERMINATE " + t + "   " + e);
				e.printStackTrace();
			}
		});
	}
}
