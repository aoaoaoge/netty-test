package com.ao.client.aio;

import com.ao.server.aio.ChatAioServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @Author: aoyh
 * @Despriction:
 * @Package: com.ao.client
 * @Date:Created in 2021/6/9 16:20
 * @Modify By:
 */
public class ClientHandler implements CompletionHandler<Integer, ByteBuffer> {

    private AsynchronousSocketChannel clientChannel;
    private ChatAioServer chatAioServer;

    public AsynchronousSocketChannel getClientChannel() {
        return clientChannel;
    }

    public ClientHandler(AsynchronousSocketChannel clientChannel, ChatAioServer chatAioServer) {
        this.clientChannel = clientChannel;
        this.chatAioServer = chatAioServer;
    }

    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        if (buffer != null) {
            if (result <= 0){
                chatAioServer.removeClient(this);
            }else {
                //读取消息
                buffer.flip();
                String message = String.valueOf(chatAioServer.getCharset().decode(buffer));
                System.out.println(getPort(clientChannel)+message);
                //转发消息
                sendMessage(clientChannel,message);
            }
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        System.out.println("客户端读写异常："+exc);
    }

    /**
     * 服务器转发客户端消息
     * @param clientChannel
     * @param message
     */
    public void sendMessage(AsynchronousSocketChannel clientChannel,String message){
        for (ClientHandler clientHandler : chatAioServer.getChannelList()) {
            if (!clientHandler.clientChannel.equals(clientChannel)) {
                ByteBuffer buffer = chatAioServer.getCharset().encode(message);
                //write不需要buffer当辅助参数，因为写到客户端的通道就完事了，而读还需要回调函数转发给其他客户端。
                clientHandler.clientChannel.write(buffer,null,clientHandler);
            }
        }
    }

    /**
     * 根据客户端channel获取对应端口号的方法
     * @param clientChannel
     * @return
     */
    private String getPort(AsynchronousSocketChannel clientChannel){
        try {
            InetSocketAddress address=(InetSocketAddress)clientChannel.getRemoteAddress();
            return "客户端["+address.getAddress()+"]:";
        } catch (IOException e) {
            e.printStackTrace();
            return "客户端[Undefined]:";
        }
    }
}
