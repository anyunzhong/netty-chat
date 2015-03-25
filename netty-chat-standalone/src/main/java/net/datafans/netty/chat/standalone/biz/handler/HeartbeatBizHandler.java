package net.datafans.netty.chat.standalone.biz.handler;

import net.datafans.netty.chat.common.entity.DataPackage;
import net.datafans.netty.common.biz.handler.AbstractBizHandler;
import net.datafans.netty.common.session.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartbeatBizHandler extends AbstractBizHandler<DataPackage> {

	private Logger logger = LoggerFactory.getLogger(HeartbeatBizHandler.class);

	private static HeartbeatBizHandler handler;

	public static HeartbeatBizHandler sharedInstance() {
		if (handler == null) {
			handler = new HeartbeatBizHandler();
		}
		return handler;
	}

	private HeartbeatBizHandler() {

	}

	@Override
	public void handle(Session session, DataPackage pkg) {
		logger.info(pkg.toString());
		session.setLastActiveTimeToNow();
		session.write(pkg);
	}

}
