package com.ao.server.bio;

import com.ao.server.bio.ChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @Author: aoyh
 * @Despriction:
 * @Package: com.ao.client
 * @Date:Created in 2021/5/29 15:46
 * @Modify By:
 */
public class ClientHandler implements Runnable{

    private ChatServer chatServer;
    private Socket socket;

    public ClientHandler(ChatServer chatServer, Socket socket) {
        this.chatServer = chatServer;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            chatServer.addClient(socket);
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String message;
            //读取消息
            while((message = bufferedReader.readLine()) != null){
                String msg = socket.getRemoteSocketAddress() + "：" +message;
                System.out.println(msg);
                //向其他客户端发送消息
                chatServer.sendMessage(socket,msg);
                if ("exit".equals(message)){
                    chatServer.removeClient(socket);
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println(socket.getRemoteSocketAddress() + " 已断开连接");
            System.out.println("客户端信息接收失败:");
            e.printStackTrace();
        }
    }
}
