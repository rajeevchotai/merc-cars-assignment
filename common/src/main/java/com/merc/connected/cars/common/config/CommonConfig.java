package com.merc.connected.cars.common.config;

import com.merc.connected.cars.common.protobuf.AESEncryptionManager;
import com.merc.connected.cars.common.protobuf.ProtobufJmsMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MessageConverter;

@Configuration
public class CommonConfig
{
    @Bean
    public MessageConverter protobufJmsMessageConverter() {
        return new ProtobufJmsMessageConverter();
    }

    @Bean
    public AESEncryptionManager encryptionManager() {
        return new AESEncryptionManager();
    }

}
