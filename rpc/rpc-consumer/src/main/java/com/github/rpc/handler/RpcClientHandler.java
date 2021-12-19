package com.github.rpc.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.Callable;

/**
 * create by ljhrot
 * create at 2021/12/19 20:34
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<String> implements Callable {

    private ChannelHandlerContext context;

    private String requestMsg;
    private String responseMsg;

    public void setRequestMsg(String msg) {
        requestMsg = msg;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
    }

    @Override
    protected synchronized void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        responseMsg = s;
        notify();
    }

    @Override
    public synchronized Object call() throws Exception {
        context.writeAndFlush(requestMsg);
        wait();
        return responseMsg;
    }
}
