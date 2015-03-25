package net.datafans.netty.common.biz.handler;

import net.datafans.netty.common.session.Session;

public interface BizHandler<T> {
	void handle(Session session, T pkg);
}
