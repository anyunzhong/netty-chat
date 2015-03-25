package net.datafans.netty.chat.standalone.boot;

import java.util.HashMap;
import java.util.Map;

import net.datafans.netty.chat.common.constant.BizType;
import net.datafans.netty.chat.common.entity.DataPackage;
import net.datafans.netty.chat.common.util.ByteUtil;
import net.datafans.netty.chat.standalone.biz.handler.HeartbeatBizHandler;
import net.datafans.netty.chat.standalone.biz.handler.LoginBizHandler;
import net.datafans.netty.chat.standalone.biz.handler.SimpleMsgBizHandler;
import net.datafans.netty.common.biz.handler.BizHandler;

public class BizHandlerMapFactory {

	private static Map<Integer, BizHandler<DataPackage>> handlerMap;

	private BizHandlerMapFactory() {

	}

	public static synchronized Map<Integer, BizHandler<DataPackage>> sharedInstance() {
		if (handlerMap == null) {
			handlerMap = new HashMap<Integer, BizHandler<DataPackage>>();

			BizHandler<DataPackage> heartbeatHandler = HeartbeatBizHandler.sharedInstance();
			handlerMap.put(ByteUtil.toInt(BizType.HEARTBEAT), heartbeatHandler);

			BizHandler<DataPackage> loginHandler = LoginBizHandler.sharedInstance();
			handlerMap.put(ByteUtil.toInt(BizType.LOGIN), loginHandler);

			BizHandler<DataPackage> simpleMsgHandler = SimpleMsgBizHandler.sharedInstance();
			handlerMap.put(ByteUtil.toInt(BizType.SIMPLE_MSG), simpleMsgHandler);
		}

		return handlerMap;
	}
}
