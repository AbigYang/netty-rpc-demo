package com.yang.vo;

import java.nio.charset.StandardCharsets;

public class RpcRequestVo {
    private final String interfaceName;
    private final String methodName;
    private final byte[] serializedArguments;

    public RpcRequestVo(String interfaceName, String methodName, byte[] serializedArguments) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.serializedArguments = serializedArguments;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public byte[] getSerializedArguments() {
        return serializedArguments;
    }

    public int length() {
        return Integer.BYTES + interfaceName.getBytes(StandardCharsets.UTF_8).length +
                Integer.BYTES + methodName.getBytes(StandardCharsets.UTF_8).length +
                Integer.BYTES + serializedArguments.length;
    }
}
