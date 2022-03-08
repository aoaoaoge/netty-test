package com.ao.server.netty.tcp;

import com.ao.server.netty.udp.NettyServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 */
@Component
public class TcpServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);

    @Autowired
    private TcpHandler tcpHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast("decoder", new StringDecoder())
                .addLast("encoder", new StringEncoder())
                .addLast(tcpHandler);
        LOG.info("{}连接上",ch.remoteAddress().getHostName());
    }

}
