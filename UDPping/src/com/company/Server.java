package com.company;

import java.io.*;
import java.net.*;
import java.util.*;
/**
 * 服务器端
 * @author xiaShang
 *
 */

public class Server {

    private static final double LOSS_RATE = 0.3;
    private static final int AVERAGE_DELAY = 100;

    public static void main(String[] args) throws IOException, InterruptedException {

        DatagramSocket socket = new DatagramSocket(8808);
        Random random = new Random();

        while(true){
            /*
             * 接收客户端发送来的消息
             */
            //创建一个数据报数据包以保留传入的UDP数据包。
            DatagramPacket request = new DatagramPacket(new byte[1024],1024);
            //阻塞主机直到主机接收UDP数据包。
            socket.receive(request);
            printData(request);
            /*
             * 将接收到的消息回显到客户端
             */
            //确定是否答复或模拟分组丢失。
            if(random.nextDouble() < LOSS_RATE) {
                System.out.println("Reply not sent.");
                continue;
            }
            Thread.sleep((int)(random.nextDouble()*2*AVERAGE_DELAY));
            //发送答复。
            InetAddress clientHost = request.getAddress(); //客户端的地址
            int clientPort = request.getPort(); //客户端的端口
            byte[] buf = request.getData();
            DatagramPacket reply = new DatagramPacket(buf, buf.length,clientHost,clientPort);
            socket.send(reply);
            System.out.println("Reply sent.");
        }
    }

    private static void printData(DatagramPacket request) throws IOException {
        byte[] buf = request.getData();
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        InputStreamReader isr = new InputStreamReader(bais);
        BufferedReader br = new BufferedReader(isr);
        String line = br.readLine();
        System.out.println("Received from"+request.getAddress().getHostAddress()+":"+new String(line));
    }
}
