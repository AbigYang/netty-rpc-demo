package com.yang.netty;

import com.yang.requesthandler.RequestHandler;
import com.yang.netty.channelhandler.RequestDecoder;
import com.yang.netty.channelhandler.RequestInvocation;
import com.yang.netty.channelhandler.ResponseEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Map;

public class NettyServer {

    public static void start(Map<Integer, RequestHandler> requestHandlerMap, int port) {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(loopGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(port)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new RequestDecoder())
                                    .addLast(new ResponseEncoder())
                                    .addLast(new RequestInvocation(requestHandlerMap));
                        }
                    });
            ChannelFuture f = bootstrap.bind().sync();
            //获取Channel的CloseFuture，并且阻塞当前线程直到它完成
            f.channel().closeFuture().sync();
        } catch (Exception e) {

        } finally {
            loopGroup.shutdownGracefully();
        }
    }
}
