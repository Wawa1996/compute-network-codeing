package emailClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.Scanner;

/**
 * 
 * @author wenwenrui
 * @category this is an email client.
 * @version v20180914
 */
public class Client {
	public static void main(String[] args){
		//新建一个tcp连接，和网易的
		try {
			Socket client = new Socket("smtp.qq.com",25);
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String response = br.readLine();
            String username;                             //用户名
            String password;                             //密码
            String receiver;                             //接收用户名
            String subject;                              //邮件主题
            String str;
            StringBuffer content = new StringBuffer();
            if(response.equals("220 smtp.qq.com Esmtp QQ Mail Server")){
            	System.out.println("客户端已经连接到腾讯邮件服务器！！");
            	
            	//输入EHLO指令
            	DataOutputStream dos = new DataOutputStream(client.getOutputStream());
    			dos.writeBytes("HELO wen\r\n");
    			dos.flush();
    	        response = br.readLine();
    	        if(!response.equals("250 smtp.qq.com")){
    	        	 System.out.println("命令错误！！！");
    	        }
    	        
    	        //输入认证指令，用户名和密码
    			dos.writeBytes("AUTH LOGIN\r\n");
    			dos.flush();
    	        response = br.readLine();
    	        if(!response.equals("334 VXNlcm5hbWU6")){
   	        	 	System.out.println("命令错误！！！");
   	            }else{
   	            	System.out.print("请输入用户名：");
   	            	Scanner sc = new Scanner(System.in);
   	            	username = sc.nextLine();
   	            	//Base64.getEncoder().encodeToString(username.getBytes());
   	            	//dos.writeBytes("MTI2Mzc1ODM4N0BxcS5jb20=\r\n");
   	            	dos.writeBytes(Base64.getEncoder().encodeToString(username.getBytes())+ "\r\n");
   	    			dos.flush();
   	    	        response = br.readLine();
	   	    	    if(!response.equals("334 UGFzc3dvcmQ6")){
	    	        	 	System.out.println("用户名输入错误！！！");
	    	        }else{
	    	        	    System.out.println("用户名输入成功！！！");
	    	            	System.out.print("请输入密码：");
	    	            	sc = new Scanner(System.in);
	    	            	password = sc.nextLine();
	    	            	dos.writeBytes(Base64.getEncoder().encodeToString(password.getBytes())+ "\r\n");
	    	    			dos.flush();
	    	    	        response = br.readLine();
	    	    	        if(!response.equals("235 Authentication successful")){
	    	    	        	System.out.println("密码输入错误！！！");
	    	    	        }else{
	    	    	        	System.out.println("登录成功！！！");
	    	    	        	System.out.print("请输入收件人：");
		    	            	sc = new Scanner(System.in);
		    	            	dos.writeBytes("mail from:<1263758387@qq.com>\r\n");
		    	    			dos.flush();
		    	    			response = br.readLine();
		    	    			receiver = sc.nextLine();
		    	    			dos.writeBytes("rcpt to:<" +receiver +">\r\n");
		    	    			dos.flush();
		    	    	        response = br.readLine();
		    	    	       if(!response.equals("250 Ok")){
		    	    	    	   System.out.println("输入错误！！！");
		    	    	       }else{
		    	    	    	   dos.writeBytes("data\r\n");
			    	    		   dos.flush();
			    	    		   response = br.readLine();
		    	    	    	   System.out.println("请输入邮件主题：");
		    	    	    	   subject = sc.nextLine();
		    	    	    	   System.out.println("请输入邮件内容(以0结束)：");
		    	    	    	   while(!"eof".equals(str = sc.nextLine())){
		    	    	    		   content.append(str);
		    	    	    	   }
		    	    	    	   dos.writeBytes("from:sunyuhu\r\n"+"to:<"+receiver+">\r\nsubject:"+ subject + "\r\n\r\n" + content.toString()+"\r\n.\r\n");
			    	    		   dos.flush();
			    	    		   
		    	    	    	   System.out.println(br.readLine()+ "\n发送成功！！");
		    	    	    	   
		    	    	       }
	    	    	        }
	    	        }
   	            }
            }
            
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
