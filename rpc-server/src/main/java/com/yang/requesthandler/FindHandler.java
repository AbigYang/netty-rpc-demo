package com.yang.requesthandler;

import com.yang.serializer.Serializer;
import com.yang.vo.Command;
import com.yang.vo.NameServerVo;
import com.yang.vo.ResponseHeader;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.yang.serializer.Serializer.serializeNameServerVo;

public class FindHandler implements RequestHandler {
    private Map<String, List<NameServerVo>> routeMap;

    public FindHandler(Map<String, List<NameServerVo>> routeMap) {
        this.routeMap = routeMap;
    }

    @Override

    public Command handle(Command requestCommand) {
        byte[] payload = requestCommand.getPayload();
        NameServerVo serverVo = Serializer.parseNameServerVo(payload);
        List<NameServerVo> list = routeMap.get(serverVo.getServiceName());
        if (list == null || list.isEmpty()) {
            System.out.println("no found server:" + serverVo.getServiceName());
            return new Command(new ResponseHeader(requestCommand.getHeader(), 999, "未找到服务"), new byte[0]);
        }
        NameServerVo result = list.get(new Random().nextInt(list.size()));
        byte[] bytes = serializeNameServerVo(result);
        return new Command(new ResponseHeader(requestCommand.getHeader()), bytes);
    }

    @Override
    public int type() {
        return 1;
    }
}
