package com.yang.serializer;

import com.yang.vo.NameServerVo;
import com.yang.vo.RpcRequestVo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Serializer {

    public static NameServerVo parseNameServerVo(byte[] bytes) {
        NameServerVo nameServerVo = new NameServerVo();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int len = buffer.getInt();
        byte[] tmp = new byte[len];
        buffer.get(tmp);
        nameServerVo.setServiceName(new String(tmp, StandardCharsets.UTF_8));

        len = buffer.getInt();
        if (len != 0) {
            tmp = new byte[len];
            buffer.get(tmp);
            nameServerVo.setAddress(new String(tmp, StandardCharsets.UTF_8));
        }

        len = buffer.getInt();
        if (len != 0) {
            nameServerVo.setPort(len);
        }
        return nameServerVo;
    }

    public static byte[] serializeNameServerVo(NameServerVo vo) {
        int len = Integer.BYTES + vo.getServiceName().getBytes(StandardCharsets.UTF_8).length + Integer.BYTES + Integer.BYTES;
        if (vo.getAddress() != null) {
            len += vo.getAddress().getBytes(StandardCharsets.UTF_8).length;
        }
        if (vo.getPort() != null) {
            len += vo.getPort();
        }
        byte[] bytes = new byte[len];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        byte[] tmpBytes = vo.getServiceName().getBytes(StandardCharsets.UTF_8);
        buffer.putInt(tmpBytes.length);
        buffer.put(tmpBytes);

        if (vo.getAddress() != null) {
            tmpBytes = vo.getAddress().getBytes(StandardCharsets.UTF_8);
            buffer.putInt(tmpBytes.length);
            buffer.put(tmpBytes);
        } else {
            buffer.putInt(0);
        }

        if (vo.getPort() != null) {
            buffer.putInt(vo.getPort());
//            buffer.put(vo.getPort().byteValue());
        } else {
            buffer.putInt(0);
        }
        return bytes;
    }

    public static byte[] serializeRpcRequestVo(RpcRequestVo request) {
        byte[] bytes = new byte[request.length()];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        byte[] tmpBytes = request.getInterfaceName().getBytes(StandardCharsets.UTF_8);
        buffer.putInt(tmpBytes.length);
        buffer.put(tmpBytes);

        tmpBytes = request.getMethodName().getBytes(StandardCharsets.UTF_8);
        buffer.putInt(tmpBytes.length);
        buffer.put(tmpBytes);

        tmpBytes = request.getSerializedArguments();
        buffer.putInt(tmpBytes.length);
        buffer.put(tmpBytes);
        return bytes;
    }

    public static RpcRequestVo parseRpcRequestVo(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int len = buffer.getInt();
        byte[] tmpBytes = new byte[len];
        buffer.get(tmpBytes);
        String interfaceName = new String(tmpBytes, StandardCharsets.UTF_8);

        len = buffer.getInt();
        tmpBytes = new byte[len];
        buffer.get(tmpBytes);
        String methodName = new String(tmpBytes, StandardCharsets.UTF_8);

        len = buffer.getInt();
        tmpBytes = new byte[len];
        buffer.get(tmpBytes);
        byte[] serializedArgs = tmpBytes;

        return new RpcRequestVo(interfaceName, methodName, serializedArgs);
    }

    public static byte[] serializeObjectArray(Object... array) {
        byte[][] tmp = new byte[array.length][];
        int l = 0;
        for (int i = 0; i < array.length; i++) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(array[i]);
                oos.flush();
                byte[] bytes = bos.toByteArray();
                tmp[i] = bytes;
                l += bytes.length;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 参数数量 +
        int len = Integer.BYTES + array.length * Integer.BYTES + l;
        byte[] bytes = new byte[len];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putInt(array.length);//参数数量
        for (byte[] by : tmp) {
            buffer.putInt(by.length);
            buffer.put(by);
        }
        return bytes;
    }

    public static Object[] parseObjectArray(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int arrayLen = buffer.getInt();
        Object[] result = new Object[arrayLen];
        for (int i = 0; i < result.length; i++) {
            int len = buffer.getInt();
            byte[] tmp = new byte[len];
            buffer.get(tmp);
            ByteArrayInputStream bis = new ByteArrayInputStream(tmp);
            try (ObjectInputStream ois = new ObjectInputStream(bis)) {
                result[i] = ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
