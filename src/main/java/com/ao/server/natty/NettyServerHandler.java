package com.ao.server.natty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
@Component
public class NettyServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Value("${nettyClient.ip}")
    private String clientIp;

    @Value("${nettyClient.port}")
    private int clientPort;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        ByteBuf byteBuf = msg.content();
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        System.out.println("receive client msg:" + new String(bytes));
        // 向其他客户端发送消息
        ByteBuf byteBuf1 = Unpooled.copiedBuffer(("服务器转发消息:" + new String(bytes)).getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(new DatagramPacket(byteBuf1, new InetSocketAddress(clientIp, clientPort)));
    }

}
