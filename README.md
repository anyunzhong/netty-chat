# netty-chat


# 工程说明

netty-common: 服务端和java客户端公用基类

netty-common-server: 服务端基础库 不带协议

netty-common-client-java: 客户端基础库  不带协议



# 一个用来聊天的简单demo  包括简单协议编码 解码

netty-chat-common: 编码 解码相关 server和client都需要

netty-chat-standalone: server启动类和业务逻辑处理相关 能独立提供服务

netty-chat-client: 简单的基于java的客户端测试类



＃ 几点说明

1. 聊天服务端demo只是用来测试 生产环境需要做分布式 把连接和业务逻辑处理分到不同的实例

2. 几个基础工程只负责 连接 会话  心跳相关工作  具体的协议编码 解码需要你去实现 比如可以做消息队列 RPC相关的一些中间件

3. Android客户端SDK https://github.com/anyunzhong/AndroidSocket

4. iOS客户端SDK https://github.com/anyunzhong/DFSocket
