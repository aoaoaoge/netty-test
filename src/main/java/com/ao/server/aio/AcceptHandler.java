package com.ao.server.aio;

import com.ao.client.aio.ClientHandler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @Author: aoyh
 * @Despriction: AsynchronousSocketChannel为accept返回的类型，T 为辅助参数类型，没有就填 Object
 * @Package: com.ao.server.aio
 * @Date:Created in 2021/6/9 16:55
 * @Modify By:
 */
public class AcceptHandler<T> implements CompletionHandler<AsynchronousSocketChannel, T> {

    private ChatAioServer chatAioServer;

    public AcceptHandler(ChatAioServer chatAioServer) {
        this.chatAioServer = chatAioServer;
    }

    /**
     * 连接完成的回调处理
     * @param clientChannel
     * @param attachment
     */
    @Override
    public void completed(AsynchronousSocketChannel clientChannel, T attachment) {
        //如果服务端未关闭
        if (chatAioServer.getServerSocketChannel().isOpen()){
            //继续处理其他客户端
            chatAioServer.getServerSocketChannel().accept(null,this);
        }
        if (clientChannel != null && clientChannel.isOpen()) {
            //客户端处理程序处理读写
            ClientHandler clientHandler = new ClientHandler(clientChannel,chatAioServer);
            chatAioServer.getChannelList().add(clientHandler);
            ByteBuffer byteBuffer = ByteBuffer.allocate(ChatAioServer.BUFFER_SIZE);
            clientChannel.read(byteBuffer,byteBuffer,clientHandler);
        }
    }

    @Override
    public void failed(Throwable exc, T attachment) {
        System.out.println("连接失败:"+exc);
    }
}
