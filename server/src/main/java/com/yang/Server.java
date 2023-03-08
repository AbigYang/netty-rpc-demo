package com.yang;

import com.yang.nameserver.NameServer;
import com.yang.netty.NettyServer;
import com.yang.requesthandler.RequestHandler;
import com.yang.requesthandler.RpcServerHandler;
import com.yang.vo.NameServerVo;

import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final String HOST = "127.0.0.1";
    private static final Integer PORT = 666;

    public static void main(String[] args) throws Exception {
        String serviceName = HelloService.class.getSimpleName();
        //register
        NameServer.register(new NameServerVo(serviceName, HOST, PORT));
        Map<String, Object> instanceMap = new HashMap<>();
        instanceMap.put(serviceName, new HelloServiceImpl());
        //start server
        NettyServer.start(new HashMap<Integer, RequestHandler>() {{
            put(2, new RpcServerHandler(instanceMap));
        }}, PORT);
    }
}
