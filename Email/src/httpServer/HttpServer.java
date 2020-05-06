package httpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * 
 * @author sunyuhu
 * @category this is a program that simulates HTTP protocol,which is sent by brown.
 * @version v20180914
 */
public class HttpServer {
	
	ServerSocket server;               //本服务器
	Socket client;                     //发请求的客户端
	//构造函数
	HttpServer(){
		try {
			server = new ServerSocket(5555);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//main 函数
	public static void main(String[] args){
         HttpServer myserver = new HttpServer();
         myserver.begin();
	}

	//在此接受客户端的请求，并作响应
	private void begin() {
		 String httpRequest;
		 String urlRequest;
         while(true){
        	 try {
        		 //开始监听
				client = this.server.accept();
				System.out.println("one has connected to this server!!" + client.getLocalPort());
                BufferedReader bf = new BufferedReader(new InputStreamReader(client.getInputStream()));				
                httpRequest = bf.readLine();
                System.out.println(httpRequest);
                //获取到url地址,请求头中是/index.html ，因此需要将/去掉
                urlRequest = httpRequest.split(" ")[1].substring(1);    
                System.out.println(urlRequest);
                //检查是否有该文件，有则返回，无则报错404
                PrintWriter out = new PrintWriter(client.getOutputStream());
       		    //返回一个状态行
               out.println("HTTP/1.0 200 OK"); 
               //返回一个首部
               out.println("Content-Type:text/html;charset=GBK");  
               // 根据 HTTP 协议, 空行将结束头信息  
               out.println();
               
               
               
               // 输出请求资源
               out.println("<h1 style='color: green'> Hello Http Server</h1>");  
               out.println("你好, 这是一个 Java HTTP 服务器 demo 应用.<br>");  
               out.println("您请求的路径是: " + urlRequest + "<br>");  
               out.close();  
        	 } catch (IOException e) {
				e.printStackTrace();
			}
         }
	}
}