package com.ao.server.bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author: aoyh
 * @Despriction:
 * @Package: com.ao.server
 * @Date:Created in 2021/5/29 15:02
 * @Modify By:
 */
public class ChatServer {

    private int port = 8888;
    /**
     * 存储在线用户信息
     * 多线程操作
     *  key一般是sessionId 这里是端口 ；value 用于储存用户发送的信息
     */
    private Map<Integer, Writer> sessionMap = new ConcurrentHashMap<>();

    private ThreadPoolExecutor executorService = new ThreadPoolExecutor(2,
            5,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>());

    /**
     * 新增在线用户
     * @param socket 连接
     */
    public void addClient(Socket socket) throws IOException {
        if (socket != null){
            OutputStream outputStream = socket.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            sessionMap.put(socket.getPort(), writer);
            System.out.println(socket.getRemoteSocketAddress() + " 已连接");
        }
    }

    /**
     * 移除在线用户
     * @param socket 连接
     */
    public void removeClient(Socket socket) throws IOException {
        if (socket != null){
            sessionMap.get(socket.getPort()).close();
            sessionMap.remove(socket.getPort());
            System.out.println(socket.getRemoteSocketAddress() + " 已断开连接");
        }
    }

    /**
     * 消息发送
     * @param socket 连接
     * @param message 消息
     */
    public void sendMessage(Socket socket,String message) throws IOException {
        if (socket != null){
            for (Integer port:sessionMap.keySet()){
                if (port != socket.getPort()){
                    Writer writer = sessionMap.get(port);
                    writer.write(message);
                    writer.flush();
                }
            }
        }
    }

    /**
     * 启动服务接收请求 分配handler去处理
     */
    public void start(){
        try (ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("服务端启动："+serverSocket.getLocalSocketAddress());
            //等待连接
            while (true){
                Socket accept = serverSocket.accept();
                //分配线程创建handler处理消息
                executorService.execute(new ClientHandler(this,accept));
            }
        }catch (IOException e) {
            System.out.println("服务端启动异常：");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.start();
    }

}
