package net.datafans.netty.chat.client;

import io.netty.channel.ChannelHandler;

import java.util.List;

import net.datafans.netty.chat.client.handler.DataPackageHandler;
import net.datafans.netty.chat.common.constant.Protocal;
import net.datafans.netty.chat.common.entity.DataPackage;
import net.datafans.netty.chat.common.handler.DataPackageDecoder;
import net.datafans.netty.chat.common.handler.DataPackageEncoder;
import net.datafans.netty.common.client.NettyClient;
import net.datafans.netty.common.config.GlobalConfig;
import net.datafans.netty.common.handler.ChannelHandlerFactory;

public class SimpleClient extends NettyClient {

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
        client.start();
    }

}
