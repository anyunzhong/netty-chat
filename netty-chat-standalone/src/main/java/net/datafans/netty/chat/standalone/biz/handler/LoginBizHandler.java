package net.datafans.netty.chat.standalone.biz.handler;

import net.datafans.netty.chat.common.entity.DataPackage;
import net.datafans.netty.chat.standalone.session.SessionMap;
import net.datafans.netty.common.biz.handler.AbstractBizHandler;
import net.datafans.netty.common.session.Session;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class LoginBizHandler extends AbstractBizHandler<DataPackage> {

	// private Logger logger = LoggerFactory.getLogger(LoginBizHandler.class);

	private static LoginBizHandler handler;

	public static LoginBizHandler sharedInstance() {
		if (handler == null) {
			handler = new LoginBizHandler();
		}
		return handler;
	}

	private LoginBizHandler() {

	}

	@Override
	public void handle(Session session, DataPackage pkg) {
		// 取出token验证
		JSONObject o = (JSONObject) JSON.parse(pkg.getContent());
		Integer userId = o.getInteger("user_id");
		if (userId != 0) {
			session.setSessionUniqueId(userId);
			SessionMap.sharedInstance().addSession(userId, session);
			session.write(DataPackage.LOGIN_SUCCESS_PACKAGE);
		} else {
			session.writeAndClose(DataPackage.LOGIN_FAIL_PACKAGE);
		}
	}

}
