package com.drivewealth.dbproxy.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;



@Configuration
public class CockroachDbConfig {


    @Value("${db.cr.grpc.server}")
    private String GRPC_SERVER_ADDRESS;

    @Bean
    public Supplier<ManagedChannel> createManagedChannelSupplier() {

        final Supplier<ManagedChannel> managedChannelSupplier = () -> ManagedChannelBuilder.forTarget(GRPC_SERVER_ADDRESS)
            .usePlaintext()
            .build();

        return managedChannelSupplier;
    }
}
