package com.yang.netty.channelhandler;

import com.yang.vo.Command;
import com.yang.vo.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class CommandDecoder extends ByteToMessageDecoder {
    //二进制补码
    private static final int LENGTH_FIELD_LENGTH = Integer.BYTES;
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (!byteBuf.isReadable(LENGTH_FIELD_LENGTH)) {
            return;
        }
        byteBuf.markReaderIndex();
        int length = byteBuf.readInt() - LENGTH_FIELD_LENGTH;
        if (byteBuf.readableBytes() < length) {
            byteBuf.resetReaderIndex();
            return;
        }
        Header header = decodeHeader(channelHandlerContext, byteBuf);
        int payloadLength  = length - header.length();
        byte [] payload = new byte[payloadLength];
        byteBuf.readBytes(payload);
        list.add(new Command(header, payload));
    }

    protected Header decodeHeader(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf){
        int type = byteBuf.readInt();
        int version = byteBuf.readInt();
        int requestIdLen = byteBuf.readInt();
        byte[] bytes = new byte[requestIdLen];
        byteBuf.readBytes(bytes);
        String requestId = new String(bytes, StandardCharsets.UTF_8);
        return new Header(
                type, version, requestId
        );
    }
}
