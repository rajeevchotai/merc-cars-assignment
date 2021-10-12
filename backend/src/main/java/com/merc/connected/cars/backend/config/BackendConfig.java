package com.merc.connected.cars.backend.config;

import com.merc.connected.cars.common.protobuf.AESEncryptionManager;
import com.merc.connected.cars.common.protobuf.ProtobufJmsMessageConverter;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.ConnectionFactory;

@Configuration
public class BackendConfig {

    @Bean
    public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
                                                    DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    public MessageConverter protobufJmsMessageConverter() {
        return new ProtobufJmsMessageConverter();
    }

    @Bean
    public AESEncryptionManager encryptionManager() {
        return new AESEncryptionManager();
    }
}
