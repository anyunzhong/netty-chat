package net.datafans.netty.chat.standalone.session;

import java.util.concurrent.ConcurrentHashMap;

import net.datafans.netty.common.session.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionMap {

	private Logger logger = LoggerFactory.getLogger(SessionMap.class);
	private static SessionMap instance = new SessionMap();

	private ConcurrentHashMap<Integer, Session> map = new ConcurrentHashMap<Integer, Session>();

	public static SessionMap sharedInstance() {
		return instance;
	}

	public void addSession(Integer uniqueId, Session session) {
		map.put(uniqueId, session);
		logger.info("SESSION_ADD " + uniqueId  +"   total: " +map.size());
	}

	public Session getSession(Integer uniqueId) {
		return map.get(uniqueId);
	}

	public void removeSession(Integer uniqueId) {
		if (!map.containsKey(uniqueId)) {
			return;
		}
		map.remove(uniqueId);
		logger.info("SESSION_REMOVED " + uniqueId+"   total: " +map.size());
	}

}
