package com.yang.requesthandler;

import com.yang.serializer.Serializer;
import com.yang.vo.Command;
import com.yang.vo.NameServerVo;
import com.yang.vo.ResponseHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegisterHandler implements RequestHandler {
    private Map<String, List<NameServerVo>> routeMap;

    public RegisterHandler(Map<String, List<NameServerVo>> routeMap) {
        this.routeMap = routeMap;
    }

    @Override
    public Command handle(Command requestCommand) {
        byte[] payload = requestCommand.getPayload();
        NameServerVo serverVo = Serializer.parseNameServerVo(payload);
        List<NameServerVo> list = routeMap.get(serverVo.getServiceName());
        if (list == null) {
            routeMap.put(serverVo.getServiceName(), new ArrayList<NameServerVo>() {{
                add(serverVo);
            }});
        } else {
            list.add(serverVo);
        }
        return new Command(new ResponseHeader(requestCommand.getHeader()), Serializer.serializeNameServerVo(serverVo));
    }

    @Override
    public int type() {
        return 0;
    }
}
