package com.github.rpc.server;

import com.github.rpc.handler.RpcServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * create by ljhrot
 * create at 2021/12/19 18:41
 */
@Slf4j
@Component
public class RpcServer {

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private final RpcServerHandler rpcServerHandler;

    public RpcServer(RpcServerHandler rpcServerHandler) {
        this.rpcServerHandler = rpcServerHandler;
    }

    public void start(String ip, int port) {
        try {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();

            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                            pipeline.addLast(rpcServerHandler);
                        }
                    });

            ChannelFuture sync = serverBootstrap.bind(ip, port).sync();
            log.debug("rpc server started in port:{}", port);
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("start rpc server fail", e);
        } finally {
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
        }
    }

    @PreDestroy
    public void destroy() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
