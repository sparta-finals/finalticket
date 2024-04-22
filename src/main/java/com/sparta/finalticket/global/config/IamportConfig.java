package com.sparta.finalticket.global.config;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IamportConfig {

    @Value("${imp.api.key}")
    String apikey;

    @Value("${imp.api.secretKey}")
    String secretKey;

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(apikey, secretKey);
    }
}