package com.yang.netty.channelhandler;

import com.yang.vo.Header;
import com.yang.vo.ResponseHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;

public class ResponseEncoder extends CommandEncoder {
    @Override
    protected void encodeHeader(ChannelHandlerContext channelHandlerContext, Header header, ByteBuf byteBuf) throws Exception {
        super.encodeHeader(channelHandlerContext, header, byteBuf);
        if (header instanceof ResponseHeader) {
            ResponseHeader responseHeader = (ResponseHeader) header;
            byteBuf.writeInt(responseHeader.getCode());
            if (responseHeader.getError() != null) {
                byte[] errorBytes = responseHeader.getError().getBytes(StandardCharsets.UTF_8);
                byteBuf.writeInt(errorBytes.length);
                byteBuf.writeBytes(errorBytes);
            } else {
                byteBuf.writeInt(0);
                byteBuf.writeBytes(new byte[0]);
            }
        } else {
            throw new Exception(String.format("Invalid header type: %s!", header.getClass().getCanonicalName()));
        }
    }
}
