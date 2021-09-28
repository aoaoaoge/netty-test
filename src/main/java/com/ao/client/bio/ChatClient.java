package com.ao.client.bio;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: aoyh
 * @Despriction:
 * @Package: com.ao.client
 * @Date:Created in 2021/5/29 16:20
 * @Modify By:
 */
public class ChatClient {

    private BufferedWriter writer;
    private BufferedReader reader;
    private Socket socket;

    private final String address = "0.0.0.0";
    private final int port = 8888;

    /**
     * 发送消息到服务器
     * @param message 消息
     * @throws IOException
     */
    public void sendMessageToServer(String message) throws IOException {
        //判断输出流是否关闭
        if (!socket.isOutputShutdown()){
            //写入消息
            writer.write(message+"\n");
            writer.flush();
        }
    }

    /**
     * 从服务器接收消息
     * @throws IOException
     */
    public String getMessageFromServer() throws IOException {
        String message = null;
        //判断输出流是否关闭
        if (!socket.isInputShutdown()){
            //读取消息
            if ((message = this.reader.readLine()) != null){
                System.out.println(message);
            }
        }
        return message;
    }

    public void start(){
        try{
            //连接服务器
            socket = new Socket(address, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            //创建线程处理用户输入
            executorService.execute(new UserInputHandler(this));
            getMessageFromServer();
        } catch (IOException e) {
            System.out.println("客户端启动失败：");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.start();
    }

}
