package com.company;

import java.io.*;                       //导入输入输出流包，且是导入包中所有类
import java.io.BufferedReader;        //字符缓冲输入流包导入
import java.io.File;                   //文件流包导入
import java.io.IOException;           //输入输出流异常处理包导入
import java.io.InputStream;           //字节输入流包导入
import java.io.PrintStream;           //字节输出打印流包导入，它是OutputStream的子类
import java.net.Socket;               //网络功能包套接字子包导入


public class Processor extends Thread { //声明线程子类Processor,他的超类是 thread
    private PrintStream out;                  //声明私有成员变量—out(输出流)
    private InputStream input;               //声明私有成员变量—input(输入流)
    public static final String WEB_ROOT = "C:\\test";
    //声明并初始化静态最终类字符串常量---服务器提供文件存储位置

    public Processor(Socket socket) {          //声明构造成员方法---创建线程
        try {                                  //创建异常处理机制
            input = socket.getInputStream();  //赋值表达式—从套接字获得客户端连接输入，返回字节输入流inputstream对象并赋值给input
            out = new PrintStream(socket.getOutputStream());//实例化输出流对象—输出到客户端
        } catch (IOException e) {           //捕捉匹配异常
            e.printStackTrace();            //显示异常信息
        }
    }
    public void run() {     //创建run方法，它是对象的线程体（这里涉及子类对超类的方法覆盖）-----在线程被创建启动后，直接调用
        try {            //构造监视块，捕捉异常
            String fileName=parse(input); // 调用方法parse解析客户端请求信息获取请求文件名
            readFile(fileName);           // 调用方法readFile打开,读取文件
        } catch (IOException e) {        //捕捉匹配异常
            e.printStackTrace();         //显示异常信息
        }
    }

    public void readFile(String fileName) throws IOException {//构造方法读取文件内容，若发生错误抛出所有异常，并将异常处理交给调用者
        File file=new File(Processor.WEB_ROOT+fileName);//实例化文件对象并与实际文件建立关联
        if (!file.exists()) {                             //判断文件是否存在
            sendError(404,"File Not Found");//文件不存在则输出错误信息—是输出显示在客户端
            return;
        }

        InputStream in=new FileInputStream(file);//创建文件输入流对象并打开源文件
        byte content[]=new byte[(int) file.length()];     //实例化字节数组         in.read(content);        //从输入流对象中读入数据到字节数组content中
        out.println("HTTP/1.1 200 sendFile");              //输出流打印信息
        out.println("Content-length:"+content.length);
        out.println();//这段打印信息并不会显示在web浏览器网页源代码中，应该是给http协议理解的，过滤掉了
        out.write(content);    //将字节数组内容写入输出流—真正的请求文件信息
        out.flush();                          //强制输出缓冲区数据
        out.close();                          //关闭打印流
        in.close();                           //关闭输入流
    }
    public void sendError(int errNum,String errMsg) {//构造错误信息发送方法
        out.println("HTTP/1.1"+errNum+" "+errMsg);
        out.println("Content-type:text/html");
        out.println();      //输出信息---不会在客户端请求web页面原代码中出现
        out.println("<html>");   //以下信息会出现在客户端请求web页面原代码中
        out.println("<head><title>Error"+errNum+"--"+errMsg+"</title></head>");                                                       //输出HTML语言信息
        out.println("<h1"+errNum+" "+errMsg+"</h1>");
        out.println("</html>");
        out.println();
        out.flush();                                     //强制输出缓冲区数据
        out.close();                                     //关闭输出流
    }
    public String parse(InputStream input) throws IOException {  //声明并构造方法parse解析客户端输入请求，并抛出可能引发的所有异常
        BufferedReader in=new BufferedReader(new InputStreamReader(input));
        //声明并实例化字符缓冲输入流对象—用于服务器缓冲输入
        String inputContent=in.readLine();//声明成员变量，用于存储调用in的成员方法读入的客户端输入数据
        if (inputContent==null||inputContent.length()==0) {
            sendError(400,"Client invoke error");//如果变量值为空或零，输出客户端调用错误信息
            return null;                           //并返回空值，关闭连接
        }


        String request[]=inputContent.split(" "); //声明字符串数组，存放成员变量inputstream中内容以空格为界分割后的数据信息
        if(request.length !=3) {                 //判断字符串数组长度是否为3
            sendError(400,"Client invoke error");//若不为3，输出错误信息
            return null;
        }

        String method=request[0];//声明成员变量method并赋值第一个字符数组中内容
        String fileName=request[1];

        String httpVersion=request[2];

        System.out.println("Method:"+method+",file name:"
                +fileName +", HTTP version:"+httpVersion);  //显示输出信息
        return fileName;
    }


}