import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public final class Webserver {
    public static void main(String[] args) throws Exception {
        int port = 6666;

        //server创立接听端口
        ServerSocket welcomeSocket = new ServerSocket(port);

        //处理一个死循环中的 HTTP 服务请求
        while(true)
        {
            //client监听一个 TCP 连接请求
            Socket connectionSocket = welcomeSocket.accept();

            // 构造一个对象来处理 HTTP 请求消息
            HttpRequest request = new HttpRequest(connectionSocket);

            //创建一个新的线程来处理请求
            Thread thread = new Thread(request);

            //开始新线程
            thread.start();
        }
    }
}

final class HttpRequest implements Runnable {

    //http空白行结束标志
    final static String CRLF = "\r\n";
    Socket socket;
//构造函数
    public HttpRequest(Socket socket) {
        this.socket = socket;
    }

    private void processRequest() throws Exception {
        //  获取套接字的输入和输出流的引用
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        //设置输入流的缓冲
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        //获取请求的 HTTP 请求消息的行
        String requestline = br.readLine();
        //显示请求行
        System.out.println();
        System.out.println(requestline);

        //得到且显示获取的头部
        String headerline = null;
        while ((headerline = br.readLine()).length() != 0) {
            System.out.println(headerline);
        }
        //从请求行中提取文件名。
        StringTokenizer tokens = new StringTokenizer(requestline);
        tokens.nextToken();
        String fileName = tokens.nextToken();
        //前面加上“.”所以，在当前目录下的文件的请求
        fileName = '.' + fileName;
//打开文件流和文件信息
        FileInputStream fis = null;
        boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }

        //构建相应信息
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;
        if (fileExists) {
            statusLine = "HTTP/1.0 200 OK";
            contentTypeLine = "Content-type:" + contentType(fileName) + CRLF;
        } else {
            statusLine = "HTTP/1.0 404 Not Found";
            contentTypeLine = "Content-type: text/html" + CRLF;
            entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY>Not Found</BODY></BODY>";
        }
        //发送状态线
        os.writeBytes(statusLine);
        //发送链接类型
        os.writeBytes(contentTypeLine);
        ///发送一个空白行，以指示头行的结束
        os.writeBytes(CRLF);

        if (fileExists) {
            sendBytes(fis, os);
            fis.close();
        } else {
            os.writeBytes(entityBody);
        }
        os.close();
        br.close();
        socket.close();
    }

    private void sendBytes(FileInputStream fis, DataOutputStream os) throws IOException {

        //构建1K缓冲的方式字节
        byte[] buffer = new byte[1024];
        int bytes = 0;//桶装为0

        //将请求的文件复制到套接字的输出流中
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    private static String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        if (fileName.endsWith(".jpg")) {
            return "text/jpg";
        }
        if (fileName.endsWith(".gif")) {
            return "text/gif";
        }

        if (fileName.endsWith(".mp3")) {
            return "audio/mp3";
        }
        if (fileName.endsWith(".mp4")) {
            return "video/mpeg4";
            }
        return "application/octet-stram";
    }
//实现runnable接口的run函数
    @Override
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

}

