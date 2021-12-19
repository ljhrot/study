package com.github.rpc.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 响应对象
 * create by ljhrot
 * create at 2021/12/19 18:11
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse {

    private String requestId;

    private String error;

    private Object result;
}
