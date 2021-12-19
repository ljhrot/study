package com.github.rpc;


import com.github.rpc.server.RpcServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * create by ljhrot
 * create at 2021/12/19 18:15
 */
@SpringBootApplication
public class ProviderApplication implements CommandLineRunner {

    private final RpcServer rpcServer;

    public ProviderApplication(RpcServer rpcServer) {
        this.rpcServer = rpcServer;
    }

    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }

    @Override
    public void run(String... args) {
        new Thread(() -> rpcServer.start("127.0.0.1", 8080)).start();
    }
}
