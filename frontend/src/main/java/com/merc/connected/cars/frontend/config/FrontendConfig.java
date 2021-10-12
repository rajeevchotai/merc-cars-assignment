package com.merc.connected.cars.frontend.config;

import com.merc.connected.cars.common.protobuf.AESEncryptionManager;
import com.merc.connected.cars.common.protobuf.ProtobufJmsMessageConverter;
import org.apache.activemq.broker.BrokerService;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.jms.ConnectionFactory;

@Configuration
public class FrontendConfig {

    @Bean
    public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
                                                    DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    public BrokerService activeMqBroker() throws Exception {
        BrokerService brokerService = new BrokerService();
        brokerService.setBrokerName("mercedes");
        brokerService.addConnector("tcp://0.0.0.0:61616");
        brokerService.start();

        return brokerService;
    }

    @Bean
    public MessageConverter protobufJmsMessageConverter() {
        return new ProtobufJmsMessageConverter();
    }

    @Bean
    public AESEncryptionManager encryptionManager() {
        return new AESEncryptionManager();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}
