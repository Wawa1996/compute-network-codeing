import socket
from time import ctime

from protocol import *


# 实例化列表，每个地址对应的{'cost': float("inf"), 'next_hop': ()}
# tuple 元组 例如让["127.0.0.1", 49999]成为一个整体
def init_each_row(routers_list):
    destination = {}
    for router in routers_list:
        destination[tuple(router)] = {'cost': float("inf"), 'next_hop': ()}
    return destination


def init_route_table(neighbours):
    with open("routers_list.json", 'r') as file:
        routers_list = json.load(file)
    # 初始化table字典,里面是neighbour的路由表
    table = {}
    for neighbour in neighbours:
        table[tuple(neighbour[0])] = init_each_row(routers_list)  # routers_list是一个字典
        # print("tuple(neighbour[0])", table[tuple(neighbour[0])])

    return table


# 初始化dest=local的行  令value={'cost': 0, 'next_hop': local_addr}
def init_own_row(local_addr, routers_list):
    destination = init_each_row(routers_list)
    # for neighbour in neighbours:
    #	destination[tuple(neighbour[0])] = {'cost' : neighbour[1], 'next_hop' : tuple(neighbour[0])}
    destination[local_addr] = {'cost': 0, 'next_hop': local_addr}
    return destination


class RouteTable:
    # 打开routers_list 初始化own_table嵌套字典
    #     初始化own_table 例子
    #          {"['127.0.0.1', 49999]": {'cost': 1, 'next_hop': ['127.0.0.1', 49999]},
    #         # "['127.0.0.1', 50000]": {'cost': 1, 'next_hop': ['127.0.0.1', 50000]},
    #         # "['127.0.0.1', 50001]": {'cost': inf, 'next_hop': []},
    #         # "['127.0.0.1', 50002]": {'cost': 2, 'next_hop': ['127.0.0.1', 50000]},
    #         # "['127.0.0.1', 50003]": {'cost': 0, 'next_hop': ['127.0.0.1', 50003]}}
    def __init__(self, local_addr, neighbours):
        self.table = init_route_table(neighbours)
        with open("routers_list.json", 'r') as file:
            self.routers_list = json.load(file)
        self.own_table = init_own_row(local_addr, self.routers_list)
        # routers_list=[["127.0.0.1", 49999],
        # ["127.0.0.1", 50000], ["127.0.0.1", 50001], ["127.0.0.1", 50002], ["127.0.0.1", 50003]]

    # 算法部分，更新own_table
    # 例如状态改变 会更新一次，去49999要经过邻居50003{'cost': 2, 'next_hop': ('127.0.0.1', 50003)}
    def update_table(self, neighbours, local_addr, neighbour_addr, table, routers_list):
        has_change = 0
        cost = float('inf')
        for neighbour in neighbours:
            # 更新sou_addr == neighbour[0]
            if tuple(neighbour[0]) == neighbour_addr:
                cost = neighbour[1]
        # 先更新直接相邻的neighbour_addr
            if self.own_table[neighbour_addr]['cost'] == float('inf'):
                self.own_table[neighbour_addr]['cost'] = cost
                self.own_table[neighbour_addr]['next_hop'] = neighbour_addr
                # print("1111", neighbour_addr)
                # print(self.own_table[neighbour_addr])
                has_change = 1
        # 这处的table是msg发送来的

        table = json.loads(table)
        # print(table)
        # 定义neighbour_table字典
        neighbour_table = {}
        for key, value in table.items():
            key = key.replace("'", '\"')  # 用双引号代替单引号
            # print(key)
            neighbour_table[tuple(json.loads(key))] = value
        self.table[neighbour_addr] = neighbour_table
        for router in routers_list:
            if self.own_table[tuple(router)]['cost'] > self.own_table[neighbour_addr]['cost'] + \
                    neighbour_table[tuple(router)]['cost']:
                self.own_table[tuple(router)]['cost'] = self.own_table[neighbour_addr]['cost'] + \
                                                        neighbour_table[tuple(router)]['cost']
                self.own_table[tuple(router)]['next_hop'] = neighbour_addr
                print('[%s]' % ctime(), '[%s : %s]' % local_addr, end=' : ')
                print("路由表有变化！")
                print(self.own_table[tuple(router)])

                has_change = 1
        if has_change == 1:
            return True
        else:
            return False

    # 重新设置路由表
    def reset_table(self, neighbours, local_addr):
        self.table = init_route_table(neighbours)
        self.own_table = init_own_row(local_addr, self.routers_list)


# with open("neighbours.json", 'r') as file:
#   neighbours = json.load(file)
#  print(neighbours)

# test = RouteTable(('127.0.0.1', 49999), neighbours)
# for i, j in test.table.items():
#   print(i, j)


# 连接des_addr 发送msg
def forward_data(des_addr, msg):
    while True:
        try:
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.connect(des_addr)
        except:
            break
        else:
            sock.sendall(msg)
            sock.close()
            break


class Router:
    # 初始化
    def __init__(self, router_socket, local_addr):
        with open('neighbours.json') as file:
            self.neighbours = json.load(file)
        self.local_addr = local_addr
        # route_table类有own_table和table
        self.route_table = RouteTable(self.local_addr, self.neighbours)
        self.router_socket = router_socket
        with open("routers_list.json", 'r') as file:
            self.routers_list = json.load(file)
        self.seq = 0
        self.forward_table()

    # 在own_table里面找下一个地址
    def find_next_hop(self, des_addr):
        next_hop = self.route_table.own_table[des_addr]['next_hop']
        return next_hop

    # 发送信息和自己的表格给neighbours
    def forward_table(self):
        table = {}
        for key, value in self.route_table.own_table.items():
            table[str(list(key))] = value
        # json.dumps()用于将字典形式的数据转化为字符串，json.loads()用于将字符串形式的数据转化为字典
        own_table = json.dumps(table).encode('utf-8')
        table_size = len(own_table)
        # 改变的标志
        has_change = 0
        for neighbour in self.neighbours:
            protocol = Protocol(self.local_addr, tuple(neighbour[0]), Type.TABLE, table_size, self.seq)
            header = protocol.make_header()
            while True:
                try:
                    # self.print_date_and_name()
                    # print("正在连接%s : %s" % tuple(neighbour[0]))
                    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                    # 端口50000倾听其他线程
                    # sock.bind(("127.0.0.1", 11111))
                    sock.connect(tuple(neighbour[0]))
                except ConnectionRefusedError as e:
                    self.route_table.own_table[tuple(neighbour[0])]['cost'] = float('inf')
                    self.route_table.own_table[tuple(neighbour[0])]['next_hop'] = ()
                    self.print_date_and_name()
                    print("该路由器不在线上")
                    break
                except Exception as e:
                    print(e)
                    continue
                else:
                    # neighbour 例子
                    # neighbour=[['127.0.0.1', 50002], 1]
                    # neighbour[0]=['127.0.0.1', 50002],neighbour[1]=1
                    if self.route_table.own_table[tuple(neighbour[0])]['cost'] == float('inf'):
                        self.route_table.own_table[tuple(neighbour[0])]['cost'] = neighbour[1]
                        self.route_table.own_table[tuple(neighbour[0])]['next_hop'] = tuple(neighbour[0])
                        has_change = 1
                    # print(neighbour)
                    # 发送信息和自己的表格
                    # # 发送TCP数据，sendall（）尝试发送string的所有数据，成功则返回None,失败则抛出异常。
                    sock.sendall(header)
                    sock.sendall(own_table)
                    sock.close()
                    break
        # 如果发送成功继续发送，直至neighbours下线
        if has_change == 1:
            self.forward_table()

    def send_msg(self):
        self.print_date_and_name()
        msg = input("请输入你想要发送的信息：")
        self.print_date_and_name()
        ip = input("请输入目标ip地址（格式为xxx.xxx.xxx.xxx）:")
        self.print_date_and_name()
        port = int(input("请输入目标端口（格式为xxxxx）："))
        des_addr = (ip, port)
        if list(des_addr) in self.routers_list and self.route_table.own_table[des_addr]['cost'] != float('inf'):
            next_hop = self.find_next_hop(des_addr)
            msg = msg.encode('utf-8')
            data_size = len(msg)
            protocol = Protocol(self.local_addr, des_addr, Type.DATA, data_size)
            header = protocol.make_header()
            forward_data(next_hop, header + msg)
            self.print_date_and_name()
            print("总花费为%s" % self.route_table.own_table[des_addr]['cost'])
        else:
            self.print_date_and_name()
            print("目的地不能到达！")

    def print_table(self):
        self.print_date_and_name()

        print()
        # 输出own_table
        for key, value in self.route_table.own_table.items():
            print("从本地路由[%s ： %s]" % self.local_addr, end="")
            print("到另一个路由[%s ： %s]" % key, end='')
            print("的最短路程为%s" % value['cost'], end='')
            if value['cost'] == float('inf'):
                print(",下一跳路由为----")
            else:
                print(",下一跳路由为[%s : %s]" % value['next_hop'])

    # 下线offline_addr
    def offline(self, offline_addr):
        self.seq += 1
        for neighbour in self.neighbours:
            protocol = Protocol(offline_addr, tuple(neighbour[0]), Type.OFFLINE, 0, self.seq)
            header = protocol.make_header()  # fooline时，sou_addr代表下线的路由
            forward_data(tuple(neighbour[0]), header)

    # 重新设置路由表
    def deal_offline(self, addr, seq):
        self.print_date_and_name()
        print("重新设置路由表！")
        self.route_table.reset_table(self.neighbours, self.local_addr)
        self.offline(addr)
        self.forward_table()

    # 打印信息，以“：”结束且不换行
    def print_date_and_name(self):
        print('[%s]' % ctime(), '[%s : %s]' % self.local_addr, end=' : ')
