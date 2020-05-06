package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient {
	public static void main(String[] args) throws IOException{
		
		String inFromUser;
		while(true){
			DatagramSocket client = new DatagramSocket();
			InetAddress IPAddr = InetAddress.getByName("localhost");//这一行其实是DNS查询
			
			Scanner sc = new Scanner(System.in);
		    inFromUser = sc.nextLine();
		    DatagramPacket sendData = new DatagramPacket(inFromUser.getBytes(),inFromUser.getBytes().length ,IPAddr ,20000);
		    client.send(sendData);
		   
		    byte[] receive = new byte[1024];
		    DatagramPacket receiveData = new DatagramPacket(receive,receive.length);
		    client.receive(receiveData);
		    String receiveFromServer =  new String(receiveData.getData());//这里必须是String构造函数，而不能是直接。toString()函数或者直接赋值给一个String类型的变量
		    System.out.println("From server:" + receiveFromServer);
		    client.close();
		}
	}
}
