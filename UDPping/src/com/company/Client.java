package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 客户端
 * @author XiaShang
 *
 */
public class Client {

    public static void main(String[] args) throws IOException {
        //发起向服务器的连接
        DatagramSocket clientSocket = new DatagramSocket();

        InetAddress IPAddress = InetAddress.getByName("192.168.1.107");//182.92.202.60

        for(int i=0;i<10;i++) {
            try {
                byte[] sendData = new byte[1024];
                byte[] receiveData = new byte[1024];
                Date currentTime = new Date();
                //设置时间的格式
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //将给定的 Date 转化为字符串表示
                String timeStamp = formatter.format(currentTime);
                String pingMessage = "PING"+i+" "+timeStamp;
                sendData = pingMessage.getBytes();
                long begin = System.currentTimeMillis();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,IPAddress,8808);
                //通过该套接字发送数据包
                clientSocket.send(sendPacket);
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                //通过该套接字接受数据包
                clientSocket.setSoTimeout(1000);
                clientSocket.receive(receivePacket);
                long after = System.currentTimeMillis();
                String reply = new String(receivePacket.getData());
                System.out.println("From Server:"+reply.trim()+" -------- RTT:"+(after-begin));
            }catch(java.net.SocketTimeoutException  e) {
                System.out.println("No reply.");
            }
        }
    }
}

