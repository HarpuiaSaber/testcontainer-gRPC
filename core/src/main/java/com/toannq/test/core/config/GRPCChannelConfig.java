package com.toannq.test.core.config;

import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GRPCChannelConfig {

    @Bean
    public Channel mayjorChannel(@Value("${grpc.major.host}") String host,@Value("${grpc.major.port}") int port) {
        return Grpc.newChannelBuilderForAddress(host, port, InsecureChannelCredentials.create()).build();
    }

    @Bean
    public Channel mentorChannel(@Value("${grpc.mentor.host}") String host,@Value("${grpc.mentor.port}") int port) {
        return Grpc.newChannelBuilderForAddress(host, port, InsecureChannelCredentials.create()).build();
    }
}
