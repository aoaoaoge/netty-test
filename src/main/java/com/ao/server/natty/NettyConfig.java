package com.ao.server.natty;

import com.ao.client.natty.NettyClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
public class NettyConfig {

    @Value("${nettyServer.ip}")
    private String serverIp;

    @Value("${nettyServer.port}")
    private int serverPort;

    @Bean
    public NettyBean getNettyBean() {
        return new NettyBean();
    }

    @Bean
    public NettyServer getNettyServer() {
        NettyServer nettyServer = new NettyServer();
        nettyServer.start(new InetSocketAddress(serverIp, serverPort));
        return nettyServer;
    }
}
