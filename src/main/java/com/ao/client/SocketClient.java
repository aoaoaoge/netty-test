package com.ao.client;

import java.io.*;
import java.net.Socket;

/**
 * @Author: aoyh
 * @Despriction:
 * @Package: com.ao.client
 * @Date:Created in 2021/5/29 10:41
 * @Modify By:
 */
public class SocketClient {

    private static final String address = "localhost";

    private static final int port = 8086;

    public static void startClient(){
        try {
            Socket socket = new Socket(address,port);
            //接收
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //发送
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //用户输入
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            String message;
            while (true){
                //从用户输入写入socket连接
                String userInput = userInputReader.readLine();
                //换行符方便服务器解析
                bufferedWriter.write(userInput+"\n");
                bufferedWriter.flush();
//                if((message = bufferedReader.readLine()) != null){
//                    System.out.println("服务端返回信息："+message);
//                }
                if ("exit".equals(userInput)){
                    bufferedWriter.close();
                    userInputReader.close();
                    socket.close();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("客户端异常-----"+e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SocketClient.startClient();
    }

}
