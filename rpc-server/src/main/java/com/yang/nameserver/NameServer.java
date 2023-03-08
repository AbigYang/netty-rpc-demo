package com.yang.nameserver;

import com.yang.netty.NettyClient;
import com.yang.netty.NettyServer;
import com.yang.requesthandler.FindHandler;
import com.yang.requesthandler.RegisterHandler;
import com.yang.requesthandler.RequestHandler;
import com.yang.serializer.Serializer;
import com.yang.vo.Command;
import com.yang.vo.Header;
import com.yang.vo.NameServerVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NameServer {

    public static void main(String[] args) {
        Map<String, List<NameServerVo>> cache = new HashMap<>();
        NettyServer.start(new HashMap<Integer, RequestHandler>() {{
            put(0, new RegisterHandler(cache));
            put(1, new FindHandler(cache));
        }}, 8888);
    }

    private static final String HOST = "127.0.0.1";
    private static final Integer PORT = 8888;


    public static void register(NameServerVo nameServerVo) throws Exception {
        String requestId = UUID.randomUUID().toString();
        NettyClient client = new NettyClient();
        byte[] payload = client.send(HOST, PORT,
                new Command(new Header(0, 1, requestId), Serializer.serializeNameServerVo(nameServerVo)));
        NameServerVo serverVo = Serializer.parseNameServerVo(payload);
        System.out.println("register success:" + serverVo);
    }

    public static NameServerVo find(String serviceName) throws Exception {
        String requestId = UUID.randomUUID().toString();
        NettyClient client = new NettyClient();
        byte[] payload = client.send(HOST, PORT, new Command(new Header(1, 1, requestId), Serializer.serializeNameServerVo(new NameServerVo(serviceName, null, null))));
        NameServerVo serverVo = Serializer.parseNameServerVo(payload);
        System.out.println("find service:" + serverVo.getServiceName() + serverVo.getAddress() + ":" + serverVo.getPort());
        return serverVo;
    }
}
