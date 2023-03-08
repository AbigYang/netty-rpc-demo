package com.yang.requesthandler;

import com.yang.serializer.Serializer;
import com.yang.vo.Command;
import com.yang.vo.ResponseHeader;
import com.yang.vo.RpcRequestVo;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RpcServerHandler implements RequestHandler {
    private Map<String, Object> instanceMap = new HashMap<>();

    public RpcServerHandler(Map<String, Object> instanceMap) {
        this.instanceMap = instanceMap;
    }

    @Override
    public Command handle(Command requestCommand) {
        byte[] payload = requestCommand.getPayload();
        RpcRequestVo requestVo = Serializer.parseRpcRequestVo(payload);
        Object obj = instanceMap.get(requestVo.getInterfaceName());
        Object[] args = Serializer.parseObjectArray(requestVo.getSerializedArguments());
        List<? extends Class<?>> list = Arrays.stream(args).map(Object::getClass).collect(Collectors.toList());
        Class[] classes = list.toArray(new Class[list.size()]);
        Object methodResult;
        try {
            Method method = obj.getClass().getMethod(requestVo.getMethodName(), classes);
            methodResult = method.invoke(obj, args);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("invoke method error!");
        }
        return new Command(new ResponseHeader(requestCommand.getHeader()), Serializer.serializeObjectArray(methodResult));
    }

    @Override
    public int type() {
        return 2;
    }
}
