package com.company;

import java.io.IOException;           //导入输入输出流的文件包中的子包--异常处理包
import java.net.ServerSocket;         //导入网络包中的子包--服务器端套接字包
import java.net.Socket;


public class Webserver {
    public static final int HTTP_PORT=5030;    //声明并初始化一个静态最终类整型常量，作为服务器端的端口
    //静态成员变量可直接引用而不用实例化（其他必须实例化）
    //最终类不能被继承，避免被修改
    private ServerSocket serverSocket;     //声明一个私有的ServerSocket类的成员变量

    public void startServer(int port){     //声明构造一个共有的无返回值带参量输入的成员方法startServer
        try{                                            //捕捉异常方法体
            serverSocket=new ServerSocket(port);          //实例化建立服务器端监听套接字
            System.out.println("Web Server startup on "+port); //标准打印输出一行并换行
            while (true) {                               //循环体 （布尔型常量）
                Socket socket=serverSocket.accept();       //等待客户端连接呼叫，建立连接套接字
                new Processor(socket).start();            //实例化并启动线程，直接调用run
            }
        } catch (IOException e) {                         //匹配异常
            e.printStackTrace();                     //处理异常—在控制台上显示异常信息
        }
    }

    public static void main(String[] argv) throws Exception {  //声明构造主函数体（带用户输入参数），抛出所有异常，程序执行入口
        Webserver server=new Webserver();             //声明实例化Webserver类对象server
        if (argv.length==1) {
            server.startServer(Integer.parseInt(argv[0]));//数据类型强制转换为整型（默认ava将键盘输入的数据看作字符串）
        } else {
            server.startServer(Webserver.HTTP_PORT);//启动服务
        }
    }
}

