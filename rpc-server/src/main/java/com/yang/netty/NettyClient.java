package com.yang.netty;

import com.yang.netty.channelhandler.RequestEncoder;
import com.yang.netty.channelhandler.ResponseDecoder;
import com.yang.netty.channelhandler.ResponseInvocation;
import com.yang.vo.Code;
import com.yang.vo.Command;
import com.yang.vo.ResponseHeader;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NettyClient {

    private final Map<String, CompletableFuture<Command>> resultFutureMap = new HashMap<>();
    private Channel channel;
    private EventLoopGroup nioEventLoopGroup;
    private Bootstrap bootstrap;

    public byte[] send(String host, int port, Command command) throws Exception {
        CompletableFuture<Command> future = new CompletableFuture<>();
        resultFutureMap.put(command.getHeader().getRequestId(), future);
        if (channel == null) {
            channel = createChannel(host, port);
        }
        channel.writeAndFlush(command).addListener((ChannelFutureListener) channelFuture -> {
            if (!channelFuture.isSuccess()) {
                future.completeExceptionally(channelFuture.cause());
                channel.close();
            }
        });
        Command result = future.get();
        ResponseHeader responseHeader = (ResponseHeader) result.getHeader();
        if (responseHeader.getCode() == Code.SUCCESS.getCode()) {
            return result.getPayload();
        } else {
            throw new Exception(responseHeader.getError());
        }
    }


    private Channel createChannel(String host, int port) throws Exception {
        if (nioEventLoopGroup == null) {
            nioEventLoopGroup = new NioEventLoopGroup();
        }
        if (bootstrap == null) {
            bootstrap = new Bootstrap();
            bootstrap.group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(host, port)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new RequestEncoder())
                                    .addLast(new ResponseDecoder())
                                    .addLast(new ResponseInvocation(resultFutureMap));
                        }
                    });
        }
        return bootstrap.connect().sync().channel();
    }
}
