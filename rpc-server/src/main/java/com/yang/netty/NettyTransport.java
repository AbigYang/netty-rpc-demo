/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yang.netty;

import com.yang.vo.Code;
import com.yang.vo.Command;
import com.yang.vo.ResponseHeader;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NettyTransport {
    private final Channel channel;
    private final Map<String, CompletableFuture<Command>> resultFutureMap;

    public NettyTransport(Channel channel, Map<String, CompletableFuture<Command>> resultFutureMap) {
        this.channel = channel;
        this.resultFutureMap = resultFutureMap;
    }

    public byte[] send(Command request) throws Exception {
        // 构建返回值
        CompletableFuture<Command> completableFuture = new CompletableFuture<>();
        try {
            // 将在途请求放到inFlightRequests中
            resultFutureMap.put(request.getHeader().getRequestId(), completableFuture);
            // 发送命令
            channel.writeAndFlush(request).addListener((ChannelFutureListener) channelFuture -> {
                // 处理发送失败的情况
                if (!channelFuture.isSuccess()) {
                    completableFuture.completeExceptionally(channelFuture.cause());
                    channel.close();
                }
            });
        } catch (Throwable t) {
            // 处理发送异常
            resultFutureMap.remove(request.getHeader().getRequestId());
            completableFuture.completeExceptionally(t);
        }
        Command result = completableFuture.get();
        ResponseHeader responseHeader = (ResponseHeader) result.getHeader();
        if (responseHeader.getCode() == Code.SUCCESS.getCode()) {
            return result.getPayload();
        } else {
            throw new Exception(responseHeader.getError());
        }
    }


}
