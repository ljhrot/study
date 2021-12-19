package com.github.rpc.proxy;

import com.alibaba.fastjson.JSON;
import com.github.rpc.client.RpcClient;
import com.github.rpc.common.RpcRequest;
import com.github.rpc.common.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * create by ljhrot
 * create at 2021/12/19 20:50
 */
public class RpcClientProxy {

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> serviceClass) {

        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{serviceClass}, (proxy, method, args) -> {
            RpcRequest rpcRequest = RpcRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .className(method.getDeclaringClass().getName())
                    .methodName(method.getName())
                    .parameterTypes(method.getParameterTypes())
                    .parameters(args).build();
            RpcClient rpcClient = new RpcClient("127.0.0.1", 8080);
            try {
                Object responseMsg = rpcClient.send(JSON.toJSONString(rpcRequest));
                RpcResponse rpcResponse = JSON.parseObject(responseMsg.toString(), RpcResponse.class);
                if (rpcResponse.getError() != null) {
                    throw new RuntimeException(rpcResponse.getError());
                }
                Object result = rpcResponse.getResult();
                return JSON.parseObject(result.toString(), method.getReturnType());
            } catch (Exception e) {
                throw e;
            } finally {
                rpcClient.close();
            }
        });
    }
}
