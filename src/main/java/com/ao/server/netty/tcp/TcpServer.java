package com.ao.server.netty.tcp;

import com.ao.server.netty.udp.NettyServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
@Component
public class TcpServer {

    @Value("${nettyServer.ip}")
    private String serverIp;

    @Value("${nettyServer.tcpPort}")
    private int serverPort;

    private ChannelFuture channelFuture;

    @Autowired
    private ServerBootstrap serverBootstrap;

    private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 10, 60,
            TimeUnit.SECONDS, new SynchronousQueue<>()
            , r -> new Thread(r,"NettyServer-Thread" + threadNumber.getAndIncrement()));

    @PostConstruct
    public void start() {
        executor.execute(()->{
            LOG.info("--------------服务端tcp服务启动--------------");
            channelFuture = serverBootstrap.bind(serverIp, serverPort);
            System.out.println("tcp服务器正在监听数据......");
        });
    }

    @PreDestroy
    public void stop() throws InterruptedException {
        channelFuture.channel().closeFuture().sync();
        LOG.info("--------------服务端tcp服务关闭--------------");
    }

}
