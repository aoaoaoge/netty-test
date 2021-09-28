package com.ao.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author: aoyh
 * @Despriction:
 * @Package: com.ao.server
 * @Date:Created in 2021/5/29 9:31
 * @Modify By:
 */
public class SocketServer {

    private static final int PORT = 8086;

    public static void startServer(){
        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("服务启动,端口为：--------"+PORT);
            while (true){
                //阻塞式-接收请求连接
                Socket accept = serverSocket.accept();
                System.out.println("客户端连接,地址为：-------------"+accept.getRemoteSocketAddress());
                //接收
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
                //发送
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(accept.getOutputStream()));
                String message;
                while ((message = bufferedReader.readLine()) != null){
                    System.out.println(accept.getRemoteSocketAddress()+" 消息:"+message);
                    bufferedWriter.write(message);
                    bufferedWriter.flush();
                    if ("exit".equals(message)){
                        System.out.println(accept.getRemoteSocketAddress()+"断开连接");
                        bufferedWriter.close();
                        bufferedReader.close();
                        accept.close();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("服务启动异常------"+e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SocketServer.startServer();
    }
}
