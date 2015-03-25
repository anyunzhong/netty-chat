package net.datafans.netty.chat.standalone.biz.decoder;

import net.datafans.netty.chat.common.entity.DataPackage;
import net.datafans.netty.common.biz.decoder.AbstractBizDecoder;
import net.datafans.netty.common.session.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginBizDecoder extends AbstractBizDecoder<DataPackage> {

	private Logger logger = LoggerFactory.getLogger(LoginBizDecoder.class);

	private static LoginBizDecoder decoder;

	public static LoginBizDecoder sharedInstance() {
		if (decoder == null) {
			decoder = new LoginBizDecoder();
		}
		return decoder;
	}

	private LoginBizDecoder() {

	}

	@Override
	public void decode(Session session, DataPackage pkg) {
		logger.info(pkg.toString());
	}

}
