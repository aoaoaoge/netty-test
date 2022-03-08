package com.ao.server.netty.udp;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ConcurrentHashMap;

public class NettyBean {
    /**
     * 保存连接上下文
     */
    private ConcurrentHashMap<String, ChannelHandlerContext> channel = new ConcurrentHashMap<>(16);

    public ConcurrentHashMap<String, ChannelHandlerContext> getChannel() {
        return channel;
    }

    public void setChannel(String key, ChannelHandlerContext val) {
        this.channel.put(key, val);
    }
}
