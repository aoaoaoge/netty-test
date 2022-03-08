package com.ao.server.netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * udp服务
 */
public class NettyServer {

    private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 10, 60,
            TimeUnit.SECONDS, new SynchronousQueue<Runnable>()
            , r -> new Thread(r,"NettyServer-Thread" + threadNumber.getAndIncrement()));

    @Autowired
    NettyServerHandler nettyServerHandler;

    public void start(InetSocketAddress address) {
        try {
            executor.execute(() -> {
                EventLoopGroup group = new NioEventLoopGroup();
                try {
                    Bootstrap b = new Bootstrap();
                    b.group(group)
                            .channel(NioDatagramChannel.class)
                            // 广播
                            .option(ChannelOption.SO_BROADCAST, true)
                            .handler(nettyServerHandler);
                    LOG.info("--------------服务端upd服务启动----------------");
                    ChannelFuture channelFuture = b.bind(address.getAddress(),address.getPort()).sync();
                    System.out.println("服务器正在监听数据......");
                    channelFuture.channel().closeFuture().await();
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
