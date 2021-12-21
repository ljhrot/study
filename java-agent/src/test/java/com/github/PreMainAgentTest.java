package com.github;

import java.util.concurrent.locks.LockSupport;

/**
 * create by ljhrot
 * create at 2021/12/21 23:34
 */
public class PreMainAgentTest {

    public static void main(String[] args) {
        System.out.println("main thread parking");
        LockSupport.park();
    }
}
