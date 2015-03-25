package net.datafans.netty.common.biz.filter;

import net.datafans.netty.common.session.Session;

public interface BizFilter<T> {
	void doFilter(Session session, T pkg);
}
