package com.drivewealth.dbproxy.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
@Data
@Configuration
public class DBProxyConfig {

    @Value("${db.flag}")
    private String dbServiceMode;

    @Value("${db.service}")
    private String dbServiceType;
}
