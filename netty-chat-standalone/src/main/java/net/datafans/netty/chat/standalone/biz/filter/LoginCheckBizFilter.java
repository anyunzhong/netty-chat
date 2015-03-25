package net.datafans.netty.chat.standalone.biz.filter;

import net.datafans.netty.chat.common.entity.DataPackage;
import net.datafans.netty.common.biz.filter.AbstractBizFilter;
import net.datafans.netty.common.session.Session;

public class LoginCheckBizFilter extends AbstractBizFilter<DataPackage> {

	private static LoginCheckBizFilter filter;

	public static LoginCheckBizFilter sharedInstance() {
		if (filter == null) {
			filter = new LoginCheckBizFilter();
		}
		return filter;
	}

	private LoginCheckBizFilter() {

	}

	@Override
	public void doFilter(Session session, DataPackage pkg) {
		// 检查是否登陆
		if (!session.isValid()) {
			session.writeAndClose(DataPackage.LOGIN_INVALID_PACKAGE);
		}
	}

}
