package net.datafans.netty.chat.standalone.biz.handler;

import net.datafans.netty.chat.common.constant.BizType;
import net.datafans.netty.chat.common.constant.Version;
import net.datafans.netty.chat.common.entity.DataPackage;
import net.datafans.netty.common.biz.handler.AbstractBizHandler;
import net.datafans.netty.common.session.Session;

public class SimpleMsgBizHandler extends AbstractBizHandler<DataPackage> {

	// private Logger logger = LoggerFactory.getLogger(LoginBizHandler.class);

	private static SimpleMsgBizHandler handler;

	public static SimpleMsgBizHandler sharedInstance() {
		if (handler == null) {
			handler = new SimpleMsgBizHandler();
		}
		return handler;
	}

	private SimpleMsgBizHandler() {

	}

	@Override
	public void handle(Session session, DataPackage pkg) {
		DataPackage sendPkg = new DataPackage(Version.V1, BizType.SIMPLE_MSG);
		sendPkg.setId(pkg.getId());
		session.write(sendPkg);
	}

}
