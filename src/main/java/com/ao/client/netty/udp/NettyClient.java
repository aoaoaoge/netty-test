package com.ao.client.netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyClient {
    private static final Logger LOG = LoggerFactory.getLogger(NettyClient.class);

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 10, 60,
            TimeUnit.SECONDS, new SynchronousQueue<>()
            , r -> new Thread(r,"NettyClient-Thread" + threadNumber.getAndIncrement()));

    @Autowired
    NettyClientHandler nettyClientHandler;

    public void start(InetSocketAddress address) {
        try {
            executor.execute(() -> {
                EventLoopGroup group = new NioEventLoopGroup();
                try {
                    Bootstrap b = new Bootstrap();
                    b.group(group)
                            .channel(NioDatagramChannel.class)
                            .option(ChannelOption.SO_BROADCAST, true)
                            .handler(nettyClientHandler);
                    LOG.info("--------------客户端upd服务启动----------------");
                    Channel channel = b.bind(address.getAddress(),address.getPort()+2).sync().channel();
                    ByteBuf byteBuf = Unpooled.copiedBuffer("客户端信息发送测试".getBytes(StandardCharsets.UTF_8));
                    channel.writeAndFlush(new DatagramPacket(byteBuf,new InetSocketAddress(
                            address.getAddress(),address.getPort()))).sync();
                    channel.closeFuture().await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    group.shutdownGracefully();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
