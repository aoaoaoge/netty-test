package com.ao.client.nio;

import io.netty.util.internal.StringUtil;

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
 * @Package: com.ao.client
 * @Date:Created in 2021/6/7 15:22
 * @Modify By:
 */
public class ChatNioClient {

    private static final int BUFFER = 1024;
    private ByteBuffer reader = ByteBuffer.allocate(BUFFER);
    private ByteBuffer writer = ByteBuffer.allocate(BUFFER);
    private Selector selector;
    private SocketChannel clientChannel;
    private Charset charset = StandardCharsets.UTF_8;

    public void start(){
        try {
            clientChannel = SocketChannel.open();
            selector = Selector.open();
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_CONNECT);
            clientChannel.connect(new InetSocketAddress("127.0.0.1",8086));
            while (true){
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    handle(selectionKey);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch (ClosedSelectorException ignored){
            //防止while中使用到已经断开了的 Selector
        }
    }

    private void handle(SelectionKey selectionKey) throws IOException {
        //判断是否连接
        if (selectionKey.isConnectable()) {
            SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
            //finishConnect()返回true说明连接完成
            if (clientChannel.finishConnect()) {
                System.out.println("连接服务器成功！");
                //创建线程处理用户输入
                new Thread(new UserInputNioHandler(this)).start();
            }
            //监听服务器转发的消息
            clientChannel.register(selector,SelectionKey.OP_READ);
        }
        //触发read消息转发
        if (selectionKey.isReadable()) {
            SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
            String message = receive(clientChannel);
            System.out.println(message);
            if ("exit".equals(message)) {
                selectionKey.cancel();
                selector.wakeup();
            }
        }
    }

    private String receive(SocketChannel clientChannel) throws IOException {
        reader.clear();
        while (clientChannel.read(reader)>0) {
            ;
        }
        reader.flip();
        return String.valueOf(charset.decode(reader));
    }

    public void sendMessageToServer(String message) throws IOException {
        if (!StringUtil.isNullOrEmpty(message)){
            writer.clear();
            writer.put(charset.encode(message));
            writer.flip();
            while (writer.hasRemaining()){
                clientChannel.write(writer);
            }
            if ("exit".equals(message)){
                selector.close();
            }
        }
    }

    public static void main(String[] args) {
        new ChatNioClient().start();
    }

}
