package com.github.rpc.service;

import com.github.rpc.anno.RpcService;
import com.github.rpc.api.IUserService;
import com.github.rpc.pojo.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * create by ljhrot
 * create at 2021/12/19 20:14
 */
@RpcService
@Service
public class UserServiceImpl implements IUserService {

    private final Map<Integer, User> userMap;
    private static final User defaultUser = User.builder().id(0).name("Sam").build();

    public UserServiceImpl() {
        userMap = new HashMap<>();
        userMap.put(1, User.builder().id(1).name("Tom").build());
        userMap.put(2, User.builder().id(1).name("Jerry").build());
    }

    @Override
    public User getById(int id) {
        return Optional.ofNullable(userMap.get(id)).orElse(defaultUser);
    }
}
