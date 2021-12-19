package com.github.rpc.api;

import com.github.rpc.pojo.User;

/**
 * create by ljhrot
 * create at 2021/12/19 18:08
 */
public interface IUserService {

    User getById(int id);
}
