package com.github.rpc.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RPC 请求对象
 * create by ljhrot
 * create at 2021/12/19 18:09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest {

    /**
     * 请求对象 Id
     */
    private String requestId;
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 请求参数类型
     */
    private Class<?>[] parameterTypes;
    /**
     * 请求参数
     */
    private Object[] parameters;
}
