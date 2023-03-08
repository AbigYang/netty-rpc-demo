package com.yang.proxy;

import com.yang.nameserver.NameServer;
import com.yang.netty.NettyClient;
import com.yang.serializer.Serializer;
import com.yang.vo.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ProxyUtil {

    private static final Map<String, NettyClient> CLIENT_MAP = new HashMap<>();

    public static <T> T proxy(Class<T> clazz) {
        Object instance = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (proxy, method, args) -> {
            String requestId = UUID.randomUUID().toString();
            String serviceName = clazz.getSimpleName();
            NameServerVo serverVo = NameServer.find(serviceName);
            NettyClient client = CLIENT_MAP.get(serviceName);
            if (client == null) {
                client = new NettyClient();
                CLIENT_MAP.put(serviceName, client);
            }
            byte[] payload = client.send(serverVo.getAddress(), serverVo.getPort(),
                    new Command(new Header(2, 1, requestId),
                            Serializer.serializeRpcRequestVo(new RpcRequestVo(serviceName, method.getName(), Serializer.serializeObjectArray(args)))));
            Object[] results = Serializer.parseObjectArray(payload);
            return results[0];//阻塞等待返回
        });
        return (T) instance;
    }

}
