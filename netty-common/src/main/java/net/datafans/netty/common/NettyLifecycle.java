package net.datafans.netty.common;

public interface NettyLifecycle {

	void start();

	void stop();

	//销毁资源
	void terminate();
}
