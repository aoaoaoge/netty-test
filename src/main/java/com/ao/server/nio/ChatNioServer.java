package com.ao.server.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * @Author: aoyh
 * @Despriction:
 * @Package: com.ao.server
 * @Date:Created in 2021/5/31 9:22
 * @Modify By:
 */
public class ChatNioServer {

    /**
     * 初始缓冲区大小
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * 创建读缓冲区 写缓冲区
     */
    private ByteBuffer readBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private ByteBuffer writeBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    private int port;

    public ChatNioServer(int port) {
        this.port = port;
    }

    public void startServer(){
        //创建ServerSocketChannel和selector打开连接
        try(ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();Selector selector = Selector.open()) {
            //将ServerSocketChannel模式设置为为非阻塞式,默认为阻塞式
            serverSocketChannel.configureBlocking(false);
            //为 ServerSocket 绑定端口
            serverSocketChannel.socket().bind(new InetSocketAddress("localhost",port));
            //将 ServerSocketChannel 注册到 Selector,并监听accept事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("服务启动成功,服务地址:"+serverSocketChannel.getLocalAddress());
            while (true){
                //触发监听事件
                if (selector.select()>0){
                    //获取事件 selectedKeys()是事件的所有信息
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    for (SelectionKey selectionKey : selectionKeys) {
                        handler(selectionKey,selector);
                    }
                    //处理完后清空selectedKeys，避免重复处理
                    selectionKeys.clear();
                }
            }
        } catch (IOException e) {
            System.out.println("服务器异常:");
            e.printStackTrace();
        }catch (ClosedSelectorException ignored){
            //防止while中使用到已经断开了的 Selector
        }
    }

    private void handler(SelectionKey selectionKey, Selector selector) throws IOException {
        //监听客户端是否注册
        if (selectionKey.isAcceptable()){
            //获取服务器连接
            ServerSocketChannel serverChannel = (ServerSocketChannel) selectionKey.channel();
            //通过服务端连接accept()获取客户端连接
            SocketChannel clientChannel = serverChannel.accept();
            //设置为非阻塞
            clientChannel.configureBlocking(false);
            //注册客户端到selector 监听客户端写入
            clientChannel.register(selector,SelectionKey.OP_READ);
            System.out.println(clientChannel.getRemoteAddress()+" 上线");
        }
        //监听客户端是否写入消息
        if (selectionKey.isReadable()){
            //获取客户端
            SocketChannel client = (SocketChannel) selectionKey.channel();
            //接收消息
            String message = receive(client);
            System.out.println("收到客户端消息:"+message);
            //发送给其他客户端
            sendMassage(selector,client,message);
            if ("exit".equals(message)){
                selectionKey.channel();
                selector.wakeup();
                System.out.println(client.getRemoteAddress()+" 下线");
            }
        }
    }


    private Charset charset = StandardCharsets.UTF_8;

    /**
     * 接收消息方法
     * @param client
     * @return
     */
    private String receive(SocketChannel client) throws IOException {
        readBuffer.clear();
        while (client.read(readBuffer) > 0) ;
        readBuffer.flip();
        return charset.decode(readBuffer).toString();
    }

    private void sendMassage(Selector selector, SocketChannel client, String message) throws IOException {

        for (SelectionKey selectedKey : selector.selectedKeys()) {
            //排除服务器和本客户端并且保证key是有效的，isValid()会判断Selector监听是否正常、对应的通道是保持连接的状态等
            if (!(selectedKey.channel() instanceof ServerSocketChannel) && !client.equals(selectedKey.channel()) && selectedKey.isValid()){
                SocketChannel otherClient = (SocketChannel) selectedKey.channel();
                writeBuffer.clear();
                writeBuffer.put(charset.encode(message));
                writeBuffer.flip();
                //把消息写入到缓冲区后，再把缓冲区的内容写到客户端对应的通道中
                while (writeBuffer.hasRemaining()){
                    otherClient.write(writeBuffer);
                }
            }
        }
    }

    public static void main(String[] args) {
        new ChatNioServer(8086).startServer();
    }

}
