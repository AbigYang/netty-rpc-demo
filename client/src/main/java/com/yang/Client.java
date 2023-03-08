package com.yang;

import com.yang.proxy.ProxyUtil;
import com.yang.vo.HelloVo;

public class Client {
    public static void main(String[] args) {
        HelloService helloService = ProxyUtil.proxy(HelloService.class);
        HelloVo vo = new HelloVo();
        vo.msg = "netty rpc good!";
        HelloVo result = helloService.hello(vo);
        System.out.println("rpc success!result:" + result);
    }
}