package com.github.rpc.handler;

import com.alibaba.fastjson.JSON;
import com.github.rpc.anno.RpcService;
import com.github.rpc.common.RpcRequest;
import com.github.rpc.common.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * create by ljhrot
 * create at 2021/12/19 18:52
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class RpcServerHandler extends SimpleChannelInboundHandler<String> implements ApplicationContextAware {

    private static final Map<String, Object> SERVICE_INSTANCE_MAP = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (serviceMap.size() <= 0) {
            return;
        }

        Set<Map.Entry<String, Object>> entries = serviceMap.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            Object serviceBean = entry.getValue();
            if (serviceBean.getClass().getInterfaces().length == 0) {
                throw new RuntimeException("服务必须实现接口");
            }

            String name = serviceBean.getClass().getInterfaces()[0].getName();
            SERVICE_INSTANCE_MAP.put(name, serviceBean);
        }
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        RpcRequest rpcRequest = JSON.parseObject(s, RpcRequest.class);
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        try {
            rpcResponse.setResult(handler(rpcRequest));
        } catch (Exception e) {
            log.error("handler rpc request fail", e);
            rpcResponse.setError(e.getMessage());
        }
        channelHandlerContext.writeAndFlush(JSON.toJSONString(rpcResponse));
    }

    private Object handler(RpcRequest rpcRequest) throws InvocationTargetException {
        Object serviceBean = SERVICE_INSTANCE_MAP.get(rpcRequest.getClassName());
        if (serviceBean == null) {
            throw new RuntimeException(String.format("can't find BeanName:%s service", rpcRequest.getClassName()));
        }

        Class<?> serviceBeanClass = serviceBean.getClass();
        String methodName = rpcRequest.getMethodName();
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] parameters = rpcRequest.getParameters();

        FastClass fastClass = FastClass.create(serviceBeanClass);
        FastMethod method = fastClass.getMethod(methodName, parameterTypes);
        if (method == null) {
            throw new RuntimeException(String.format("Service: %s can't method: %s", rpcRequest.getClassName(), rpcRequest.getMethodName()));
        }
        return method.invoke(serviceBean, parameters);
    }

}
