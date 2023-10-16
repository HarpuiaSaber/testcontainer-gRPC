package com.toannq.test.core.config;

import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GRPCConfig {

    private final String host;
    private final int port;

    public GRPCConfig(@Value("${grpc.host}") String host, @Value("${grpc.port}") int port) {
        this.host = host;
        this.port = port;
    }

    @Bean
    public Channel channel() {
        return Grpc.newChannelBuilderForAddress(host, port, InsecureChannelCredentials.create()).build();
    }
}
