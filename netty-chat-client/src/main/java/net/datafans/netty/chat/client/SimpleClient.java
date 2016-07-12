package net.datafans.netty.chat.client;

import com.alibaba.fastjson.JSON;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import io.netty.channel.ChannelHandler;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.datafans.netty.chat.client.handler.DataPackageHandler;
import net.datafans.netty.chat.common.constant.BizType;
import net.datafans.netty.chat.common.constant.Protocal;
import net.datafans.netty.chat.common.constant.Version;
import net.datafans.netty.chat.common.entity.DataPackage;
import net.datafans.netty.chat.common.entity.response.SuccessResponse;
import net.datafans.netty.chat.common.handler.DataPackageDecoder;
import net.datafans.netty.chat.common.handler.DataPackageEncoder;
import net.datafans.netty.common.client.NettyClient;
import net.datafans.netty.common.config.GlobalConfig;
import net.datafans.netty.common.handler.ChannelHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleClient extends NettyClient {


    private static  Logger logger = LoggerFactory.getLogger(SimpleClient.class);

    @Override
    protected boolean enableFrameDecoder() {
        return true;
    }

    @Override
    protected void setFrameDecoderConfig(GlobalConfig.FrameDecoder config) {
        config.setOffset(0);
        config.setLength(Protocal.FIELD_PACKAGE_SIZE_LENGTH);
        config.setAdjustment(-Protocal.FIELD_PACKAGE_SIZE_LENGTH);
    }

    @Override
    public void setHandlerList(List<ChannelHandlerFactory> handlerList) {

        handlerList.add(new ChannelHandlerFactory() {
            @Override
            public ChannelHandler build() {
                return new DataPackageDecoder();
            }
        });
        handlerList.add(new ChannelHandlerFactory() {

            @Override
            public ChannelHandler build() {
                return new DataPackageEncoder();
            }
        });
        handlerList.add(new ChannelHandlerFactory() {

            @Override
            public ChannelHandler build() {
                return new DataPackageHandler();
            }
        });

    }

    @Override
    public int getPort() {
        return 50000;
    }

    @Override
    protected String getHost() {
        return "127.0.0.1";
    }

    @Override
    protected Object getHeartbeatDataPackage() {
        return DataPackage.HEARTBEAT_PACKAGE;
    }

    @Override
    protected int autoReconnectTimesThreshold() {
        return Integer.MAX_VALUE;
    }

    public static void main(String[] args) {

        final SimpleClient client = new SimpleClient();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                logger.info("开始登录");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("user_id", 100);
                DataPackage login = new DataPackage(Version.V1, BizType.LOGIN, JSON
                        .toJSONString(map).getBytes());
                client.write(login);
                logger.info("结束登录");

                logger.info("发送普通消息");
                Map<String, Object> simpleMsgMap = new HashMap<String, Object>();
                map.put("text", "hello world!");
                DataPackage simpleMsgPkt = new DataPackage(Version.V1, BizType.SIMPLE_MSG, JSON
                        .toJSONString(simpleMsgMap).getBytes());
                client.write(simpleMsgPkt);
                logger.info("发送普通消息完成");


            }
        }).start();
        client.start();
    }

}
