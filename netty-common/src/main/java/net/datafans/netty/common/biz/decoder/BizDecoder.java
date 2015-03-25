package net.datafans.netty.common.biz.decoder;

import net.datafans.netty.common.session.Session;

public interface BizDecoder<T> {
	void decode(Session session, T pkg);
}
