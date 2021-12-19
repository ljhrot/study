package com.github.rpc.client;

import com.github.rpc.handler.RpcClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * create by ljhrot
 * create at 2021/12/19 20:26
 */
public class RpcClient {

    private NioEventLoopGroup executors;
    private Channel channel;
    private final RpcClientHandler rpcClientHandler;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public RpcClient(String ip, int port) {
        rpcClientHandler = new RpcClientHandler();
        init(ip, port);
    }

    private void init(String ip, int port) {
        try {
            executors = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(executors)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 300)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                            pipeline.addLast(rpcClientHandler);
                        }
                    });
            channel = bootstrap.connect(ip, port).sync().channel();
        } catch (Exception e) {
            System.err.println("connect rpc server fail");
            e.printStackTrace();
            if (channel != null) {
                channel.close();
            }

            if (executors != null) {
                executors.shutdownGracefully();
            }
        }
    }

    public void close() {
        if (channel != null) {
            channel.close();
        }

        if (executors != null) {
            executors.shutdownGracefully();
        }
    }

    public Object send(String msg) throws ExecutionException, InterruptedException {
        rpcClientHandler.setRequestMsg(msg);
        Future future = executorService.submit(rpcClientHandler);
        return future.get();
    }
}
