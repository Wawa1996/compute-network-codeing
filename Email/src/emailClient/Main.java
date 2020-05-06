package emailClient;

import java.awt.EventQueue;//EventQueue 是一个与平台无关的类，它将来自于底层同位体类和受信任的应用程序类的事件列入队列。
import javax.swing.JFrame;
import javax.swing.JPanel;//JPanel 是一种中间层容器，它能容纳组件并将组件组合在一起，但它本身必须添加到其他容器中使用。
import java.awt.BorderLayout;//BorderLayout布局是将屏幕分成上、下、左、右和中间五个部分，运行代码之后，中间占了最大的区域，四周是在确保能够显示出来的情况下，尽可能地少占空间。
import javax.swing.JTextField;//单行文本框组件
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;//从字符输入流中读取文本，缓冲各个字符，从而提供字符、数组和行的高效读取。
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.border.LineBorder;//实现单色、任意厚度线边框的类。
import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.JTextPane;
import java.awt.Font;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Main {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	private JTextPane txtpnPleaseLoginYour;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		String username;
		String password;
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 502);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JTextPane txtpnUsername = new JTextPane();
		txtpnUsername.setEditable(false);
		txtpnUsername.setFont(new Font("Consolas", Font.PLAIN, 16));
		txtpnUsername.setText("username:");
		txtpnUsername.setBounds(89, 148, 93, 32);
		frame.getContentPane().add(txtpnUsername);
		
		JTextPane txtpnPassword = new JTextPane();
		txtpnPassword.setEditable(false);
		txtpnPassword.setText("password:");
		txtpnPassword.setFont(new Font("Consolas", Font.PLAIN, 16));
		txtpnPassword.setBounds(89, 205, 93, 32);
		frame.getContentPane().add(txtpnPassword);
		
		textField = new JTextField();
		textField.setBounds(192, 148, 184, 32);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(192, 205, 184, 32);
		frame.getContentPane().add(textField_1);
		
		JButton button = new JButton("\u767B\u5F55");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = textField.getText();
				String password = textField_1.getText();
				System.out.println(username+ "  " + password);
				try {
					Socket client = new Socket("smtp.qq.com",25);
					BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
		            String response = br.readLine();
		            if(response.equals("220 smtp.qq.com Esmtp QQ Mail Server")){
		            	System.out.println("客户端已经连接到腾讯邮件服务器！！");
		            	//输入EHLO指令
		            	DataOutputStream dos = new DataOutputStream(client.getOutputStream());
		    			dos.writeBytes("HELO sunyuhu\r\n");
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
		   	            	dos.writeBytes(Base64.getEncoder().encodeToString(username.getBytes())+ "\r\n");
		   	    			dos.flush();
		   	    	        response = br.readLine();
			   	    	    if(!response.equals("334 UGFzc3dvcmQ6")){
			    	        	 	System.out.println("用户名输入错误！！！");
			    	        	 	textField.setText("");
			    	        	 	textField_1.setText("");
			    	        }else{
			    	        	    System.out.println("用户名输入成功！！！");
			    	            	System.out.print("请输入密码：");
			    	            	dos.writeBytes(Base64.getEncoder().encodeToString(password.getBytes())+ "\r\n");
			    	    			dos.flush();
			    	    	        response = br.readLine();
			    	    	        if(!response.equals("235 Authentication successful")){
			    	    	        	System.out.println("密码输入错误！！！");
			    	    	        	textField.setText("");
				    	        	 	textField_1.setText("");
			    	    	        }else{
			    	    	        	System.out.println("登录成功！！！");
			    	    	        	dos.writeBytes("quit\r\n");
				    	    			dos.flush();
			    	    	        	WriteAndSendEmail send = new WriteAndSendEmail(username,password);
			    	    				send.setVisible(true);
			    	    				frame.dispose();
			    	    	        }
			    	        }
		   	            }
		            }
		            else{
		            	System.out.println("未知错误！！！");
		            	frame.dispose();
		            }
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		button.setBounds(157, 303, 93, 37);
		frame.getContentPane().add(button);
		
		txtpnPleaseLoginYour = new JTextPane();
		txtpnPleaseLoginYour.setFont(new Font("Consolas", Font.BOLD, 18));
		txtpnPleaseLoginYour.setEditable(false);
		txtpnPleaseLoginYour.setText("Please login your QQ email");
		txtpnPleaseLoginYour.setBounds(84, 50, 266, 37);
		frame.getContentPane().add(txtpnPleaseLoginYour);
	}
}
