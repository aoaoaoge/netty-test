package com.ao.server.netty.udp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

@Configuration
public class NettyConfig {

    @Value("${nettyServer.ip}")
    private String serverIp;

    @Value("${nettyServer.port}")
    private int serverPort;

    @Autowired
    private NettyServer nettyServer;

    @Bean
    public NettyBean getNettyBean() {
        return new NettyBean();
    }

//    @PostConstruct
    public void getNettyServer() {
        nettyServer.start(new InetSocketAddress(serverIp, serverPort));
    }

//    @PreDestroy
    public void stopNettyServer() throws InterruptedException {
        nettyServer.stop();
    }
}
