package net.datafans.netty.chat.standalone.boot;

import java.util.HashMap;
import java.util.Map;

import net.datafans.netty.chat.common.constant.BizType;
import net.datafans.netty.chat.common.entity.DataPackage;
import net.datafans.netty.chat.common.util.ByteUtil;
import net.datafans.netty.chat.standalone.biz.decoder.LoginBizDecoder;
import net.datafans.netty.common.biz.decoder.BizDecoder;

public class BizDecoderMapFactory {

	private static Map<Integer, BizDecoder<DataPackage>> decoderMap;

	private BizDecoderMapFactory() {

	}

	public static synchronized Map<Integer, BizDecoder<DataPackage>> sharedInstance() {
		if (decoderMap == null) {
			decoderMap = new HashMap<Integer, BizDecoder<DataPackage>>();
			BizDecoder<DataPackage> login = LoginBizDecoder.sharedInstance();

			
			decoderMap.put(ByteUtil.toInt(BizType.LOGIN), login);
		}

		return decoderMap;
	}

}
