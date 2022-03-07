package com.ao.client.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Author: aoyh
 * @Despriction:
 * @Package: com.ao.client
 * @Date:Created in 2021/5/29 16:30
 * @Modify By:
 */
public class UserInputNioHandler implements Runnable{

    private final ChatNioClient client;

    public UserInputNioHandler(ChatNioClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            // 用户输入
            while (true){
                String message = reader.readLine();
                //发送消息
                client.sendMessageToServer(message);
                if ("exit".equals(message)){
                    System.out.println("已断开连接");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
