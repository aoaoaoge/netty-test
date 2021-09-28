package com.ao.server.aio;

import com.ao.client.aio.ClientHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

/**
 * @Author: aoyh
 * @Despriction:
 * @Package: com.ao.server
 * @Date:Created in 2021/6/9 15:22
 * @Modify By:
 */
public class ChatAioServer {

    public static final int BUFFER_SIZE = 1024;

    private AsynchronousChannelGroup asynchronousChannelGroup;
    private AsynchronousServerSocketChannel serverSocketChannel;

    //在线用户列表。为了并发下的线程安全，所以使用CopyOnWriteArrayList
    //CopyOnWriteArrayList在写时加锁，读时不加锁，而本项目正好在转发消息时需要频繁读取.
    //ClientHandler包含每个客户端的通道，类型选择为ClientHandler是为了在write的时候调用每个客户端的handler
    private CopyOnWriteArrayList<ClientHandler> channelList;
    private Charset charset = StandardCharsets.UTF_8;

    private int port;

    public ChatAioServer(int port) {
        this.port = port;
    }

    public void start(){
        try {
            //创建线程池与AsynchronousChannelGroup绑定
            ExecutorService executorService = new ThreadPoolExecutor(
                    1, 3,
                    20, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(), r -> new Thread("ChatAioServerThread"));
            asynchronousChannelGroup = AsynchronousChannelGroup.withThreadPool(executorService);
            //创建通道
            serverSocketChannel = AsynchronousServerSocketChannel.open(asynchronousChannelGroup);
            serverSocketChannel.bind(new InetSocketAddress("127.0.0.1",port));
            System.out.println("服务启动："+serverSocketChannel.getLocalAddress());
            //为了让服务一直运行,写在循环里,accept是异步的,所以加了阻塞的System.in.read()
            while (true){
                serverSocketChannel.accept(null,new AcceptHandler(this));
                System.in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (serverSocketChannel != null) {
                try {
                    serverSocketChannel.close();
                } catch (IOException e) {
                    System.out.println("服务关闭异常：");
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeClient(ClientHandler client){
        channelList.remove(client);
        if (client != null){
            try {
                System.out.println(client.getClientChannel().getRemoteAddress());
                client.getClientChannel().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public Charset getCharset() {
        return charset;
    }

    public AsynchronousServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }

    public CopyOnWriteArrayList<ClientHandler> getChannelList() {
        return channelList;
    }

    public void setChannelList(CopyOnWriteArrayList<ClientHandler> channelList) {
        this.channelList = channelList;
    }

    public static void main(String[] args) {
        new ChatAioServer(8086).start();
    }

}
