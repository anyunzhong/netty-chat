package net.datafans.netty.chat.standalone.boot;

import java.util.HashMap;
import java.util.Map;

import net.datafans.netty.chat.common.constant.BizType;
import net.datafans.netty.chat.common.entity.DataPackage;
import net.datafans.netty.chat.common.util.ByteUtil;
import net.datafans.netty.chat.standalone.biz.filter.LoginCheckBizFilter;
import net.datafans.netty.common.biz.filter.BizFilter;

public class BizFilterMapFactory {

	private static Map<Integer, BizFilter<DataPackage>> filterMap;

	private BizFilterMapFactory() {

	}

	public synchronized static Map<Integer, BizFilter<DataPackage>> sharedInstance() {
		if (filterMap == null) {
			filterMap = new HashMap<Integer, BizFilter<DataPackage>>();

			BizFilter<DataPackage> loginCheck = LoginCheckBizFilter.sharedInstance();

			filterMap.put(ByteUtil.toInt(BizType.GROUP_MSG), loginCheck);
			filterMap.put(ByteUtil.toInt(BizType.SIMPLE_MSG), loginCheck);
			
		}

		return filterMap;
	}

}
