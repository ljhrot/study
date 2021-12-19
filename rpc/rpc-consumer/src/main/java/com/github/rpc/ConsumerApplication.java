package com.github.rpc;

import com.github.rpc.api.IUserService;
import com.github.rpc.pojo.User;
import com.github.rpc.proxy.RpcClientProxy;

/**
 * create by ljhrot
 * create at 2021/12/19 20:26
 */
public class ConsumerApplication {
    public static void main(String[] args) {
        IUserService userService = RpcClientProxy.createProxy(IUserService.class);
        User user = userService.getById(89);
        System.out.println(user);
    }
}
