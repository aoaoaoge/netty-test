package com.ao.client.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Author: aoyh
 * @Despriction:
 * @Package: com.ao.client.aio
 * @Date:Created in 2021/6/11 11:46
 * @Modify By:
 */
public class ChatAioClient {

    private static final int BUFFER_SIZE = 1024;
    private AsynchronousSocketChannel clientChannel;
    private Charset charset = StandardCharsets.UTF_8;

    private String address;
    private int port;

    public ChatAioClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void start(){
        try {
            //打开通道
            clientChannel = AsynchronousSocketChannel.open();
            //获取连接
            clientChannel.connect(new InetSocketAddress(address,port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ChatAioClient("127.0.0.1",8086);
    }
}
