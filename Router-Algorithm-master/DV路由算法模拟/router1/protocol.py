import struct
import json

HEADER_FORM = '30s30sllll'
# 计算给定的格式(HEADER_FORM)占用多少字节的内存  76
HEADER_SIZE = struct.calcsize(HEADER_FORM)


# print("HEADER", HEADER_SIZE)


class Type:
    DATA = 0
    TABLE = 1
    OFFLINE = 2


# 打包成传输的header
# 初始化地址，窗口大小，路过步数
# sou_addr源地址 des_addr目的地址 msg_type为Type.TABLE or Type.OFFLINE，seq默认为0 passby 经过步数默认为0

class Protocol:
    def __init__(self, sou_addr, des_addr, msg_type, size, seq=0, passby=0):
        self.sou_addr = sou_addr
        self.des_addr = des_addr
        self.size = size
        self.msg_type = msg_type
        self.seq = seq
        self.passby = passby

    def make_header(self):
        # json.dumps()用于将字典形式的数据转化为字符串，json.loads()用于将字符串形式的数据转化为字典
        sou_addr = json.dumps(self.sou_addr).encode('utf-8')
        des_addr = json.dumps(self.des_addr).encode('utf-8')
        header = struct.pack(HEADER_FORM, sou_addr, des_addr,
                             self.msg_type, self.size, self.seq, self.passby)
        return header
