package emailClient;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import java.awt.Font;
import java.awt.Color;
import java.awt.SystemColor;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;
import java.awt.event.ActionEvent;

public class WriteAndSendEmail extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Create the frame.
	 * @param username 
	 * @param password 
	 */
	public WriteAndSendEmail(String username, String password) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 498);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.activeCaption);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));//设置面板的边界，Border描述了面板四周的边界（属于面板内部），EmptyBorder是一个空白的边界；
		setContentPane(contentPane);
		contentPane.setLayout(null);//创建一个新的流布局管理器，具有指定的对齐方式以及指定的水平和垂直间隙。 
		
		textField = new JTextField();//JTextField 是一个轻量级组件，它允许编辑单行文本。
		textField.setBounds(105, 10, 319, 31);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JTextPane txtpnReceiver = new JTextPane();//jtextpane是java swing中的一个组件，是一个可以编辑和显示html，rtf和普通文本的富文本组件
		txtpnReceiver.setEditable(false);//Editable只是不能编辑
		txtpnReceiver.setBackground(new Color(173, 216, 230));
		txtpnReceiver.setFont(new Font("Consolas", Font.PLAIN, 15));//设计字体显示效果 Font mf = new Font(String 字体，int 风格，int 字号)
		txtpnReceiver.setText("subject:");//setText()方法在应用别处显示数据。
		txtpnReceiver.setBounds(10, 51, 85, 31);
		contentPane.add(txtpnReceiver);
		
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setText("receiver:");
		textPane.setFont(new Font("Consolas", Font.PLAIN, 15));
		textPane.setBackground(new Color(173, 216, 230));
		textPane.setBounds(10, 10, 85, 31);
		contentPane.add(textPane);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(105, 51, 319, 31);
		contentPane.add(textField_1);
		
		JTextPane txtpnContent = new JTextPane();
		txtpnContent.setEditable(false);
		txtpnContent.setText("content");
		txtpnContent.setFont(new Font("Consolas", Font.PLAIN, 15));
		txtpnContent.setBackground(new Color(173, 216, 230));
		txtpnContent.setBounds(10, 92, 62, 31);
		contentPane.add(txtpnContent);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(10, 133, 414, 256);
		contentPane.add(textArea);
		
		JButton btnCansel = new JButton("cancel");
		btnCansel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField.setText("");
        	 	textField_1.setText("");
        	 	textArea.setText("");
        	 	System.exit(0);
			}
		});
		btnCansel.setFont(new Font("Consolas", Font.BOLD, 16));
		btnCansel.setBounds(308, 399, 116, 38);
		contentPane.add(btnCansel);
		
		JButton btnClear = new JButton("clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField.setText("");
        	 	textField_1.setText("");
        	 	textArea.setText("");
			}
		});
		btnClear.setFont(new Font("Consolas", Font.BOLD, 16));//设置字体
		btnClear.setBounds(160, 399, 116, 38);
		contentPane.add(btnClear);
		
		JTextPane txtpnThisEmailIs = new JTextPane();
		txtpnThisEmailIs.setText(username +" sent this email.");
		txtpnThisEmailIs.setFont(new Font("Consolas", Font.PLAIN, 15));
		txtpnThisEmailIs.setEditable(false);
		txtpnThisEmailIs.setBackground(new Color(173, 216, 230));
		txtpnThisEmailIs.setBounds(82, 92, 342, 31);
		contentPane.add(txtpnThisEmailIs);
		
		JButton btnSend = new JButton("send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//获取receiver
				String receiver = textField.getText();
				//获取邮件主题
				String subject = textField_1.getText();
				//获取邮件内容
				String content = textArea.getText();
				//获取用户名
				String username = txtpnThisEmailIs.getText().split(" ")[0];
				
				try {
					@SuppressWarnings("resource")
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
				    	        	 	textArea.setText("");
			    	    	        }else{
			    	    	        	System.out.println("登录成功！！！");
				    	            	dos.writeBytes("mail from:<"+username+"\r\n");
				    	    			dos.flush();
				    	    			response = br.readLine();
				    	    			dos.writeBytes("rcpt to:<" +receiver +">\r\n");
				    	    			dos.flush();
				    	    	        response = br.readLine();
				    	    	       if(!response.equals("250 Ok")){
				    	    	    	   System.out.println("输入错误！！！");
				    	    	       }else{
				    	    	    	   dos.writeBytes("data\r\n");
					    	    		   dos.flush();
					    	    		   response = br.readLine();
				    	    	    	   dos.writeBytes("from:" + username + "\r\n"+"to:<"+receiver+">\r\nsubject:"+ subject + "\r\n\r\n" + content.toString()+"\r\n.\r\n");
					    	    		   dos.flush();
					    	    		   
				    	    	    	   System.out.println(br.readLine()+ "\n发送成功！！");
				    	    	       }
			    	    	        }
			    	        }
		   	            }
		            }
		            else{
		            	System.out.println("未知错误！！！");
		            }
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnSend.setFont(new Font("Consolas", Font.BOLD, 16));
		btnSend.setBounds(10, 399, 116, 38);
		contentPane.add(btnSend);
	}
}