import os
import sys
import threading
from socket import *
from threading import Thread

from router import *


# 配置服务套接字
# json.loads()函数是将json格式数据转换为字典
def get_addr_settings():
    try:
        with open('settings.json', 'r') as settings:
            addr, port = json.load(settings)
    except:
        print("无法打开配置文件!")
    # 当 try 块没有出现异常时，程序会执行 else
    else:
        return addr, port


# 创建服务套接字
def build_socket():
    try:
        # 生成一个TCP的socket
        router_accept_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    except Exception as e:
        print(e)
        print("创建服务套接字失败！")
        sys.exit()
    else:
        try:
            addr, port = get_addr_settings()
            router_accept_socket.bind((addr, port))
        except:
            print("绑定套接字失败！")
            router_accept_socket.close()
            sys.exit()
        else:
            # listen使得一个进程可以接受其它进程的请求，从而成为一个服务器进程
            # listen(n)传入的值, n表示的是服务器拒绝(超过限制数量的)连接之前，操作系统可以挂起的最大连接数量。
            # n也可以看作是"排队的数量"
            router_accept_socket.listen(1000)
            return router_accept_socket


# 处理客户端输入
def deal_client_input(router):
    while True:
        router.print_date_and_name()
        cmd = input("请输入指令：1.查看路由表；2.发送消息;3.从网络中断开\n")
        if cmd == '1':
            router.print_table()
        elif cmd == '2':
            router.send_msg()
        elif cmd == '3':
            # 关闭socket
            print("从网络中断开！")
            router.offline(router.local_addr)
            router.router_socket.listen(0)
            router.router_socket.close()
            os._exit(0)
        else:
            print("请重新输入！")


# sou_addr 源地址 des_addr 目的地址
# unpack顾名思义，解包。比如pack打包，然后就可以用unpack解包了。
# 返回一个由解包数据(string)得到的一个元组(tuple), 即使仅有一个数据也会被解包成元组。
# 其中len(string) 必须等于 calcsize(fmt)，这里面涉及到了一个calcsize函数。struct.calcsize(fmt)：这个就是用来计算fmt格式所描述的结构的大小。

# unpack header
# 接收输入信息
def deal_router_io(router, connect_socket, addr):
    # 接收来自socket缓冲区字节数据
    header = connect_socket.recv(HEADER_SIZE)
    sou_addr, des_addr, msg_type, size, seq, passby = struct.unpack(HEADER_FORM, header)
    # strip('\0')删除字符串结束符
    # json.loads()函数是将json格式数据转换为字典
    sou_addr = tuple(json.loads(sou_addr.decode('utf-8').strip('\0')))
    des_addr = tuple(json.loads(des_addr.decode('utf-8').strip('\0')))
    recv_size = 0
    msg = ''
    # 接收的信息不为空
    # 一次接收小于等于1024个字节直到字节等于size
    while recv_size != size:
        if size - recv_size > 1024:
            data = connect_socket.recv(1024)
        else:
            data = connect_socket.recv(size - recv_size)

        recv_size += len(data)
        msg += data.decode('utf-8')
    if tuple(des_addr) == router.local_addr:
        # print("des_addr", sou_addr)
        if msg_type == Type.TABLE and seq == router.seq:
            is_new = router.route_table.update_table(router.neighbours, router.local_addr, sou_addr, msg,
                                                     router.routers_list)
            if is_new:
                router.forward_table()
        elif msg_type == Type.DATA:
            router.print_date_and_name()
            print("[%s : %s]" % sou_addr + "发来消息:", msg)
        elif msg_type == Type.OFFLINE and seq == router.seq + 1:
            # 创建一个锁,不允许其他线程读取table
            lock = threading.Lock()
            lock.acquire()
            router.print_date_and_name()
            print("[%s : %s]" % sou_addr + "下线！")
            router.deal_offline(sou_addr, seq)
            lock.release()
    else:
        protocol = Protocol(sou_addr, des_addr, msg_type, size, seq, passby + 1)
        header = protocol.make_header()
        next_hop = router.find_next_hop(des_addr)
        msg = msg.encode('utf-8')
        router.forward_data(next_hop, header + msg)
        router.print_date_and_name()
        print("从[%s : %s]" % sou_addr + "到[%s :%s]" % des_addr + "的信息。到这里经过了%s" % (passby + 1))


def wait_for_connection(router):
    while True:
        # accept()接收一个套接字中已建立的连接，返回  sock, addr
        connect_socket, addr = router.router_socket.accept()
        # print("connect_socket", connect_socket)
        deal = Thread(target=deal_router_io, args=(router, connect_socket, addr))
        deal.start()


def start_router():
    # 创建服务套接字
    # getsockname 获取本地套接字，包括它的IP和端口。
    router_socket = build_socket()
    # print("router_socket", router_socket)
    local_addr = router_socket.getsockname()
    print("本路由地址为%s,端口为%s" % local_addr)
    router = Router(router_socket, local_addr)
    # 运行两个线程
    router_client = Thread(target=deal_client_input, args=[router])
    router_client.start()
    router_server = Thread(target=wait_for_connection, args=[router])
    router_server.start()


if __name__ == "__main__":
    start_router()
