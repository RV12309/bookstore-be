package com.ecomerce.postmaster.config;

import com.ecomerce.postmaster.common.GenericJsonDeserializer;
import com.ecomerce.postmaster.model.Notify;
import com.ecomerce.postmaster.model.Request;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${kafka-bootstrap-servers}")
    private String notificationServer;

    @Value("${kafka-consumer-group-id}")
    private String notifyConsumerGroupId;

    @Bean
    public ConsumerFactory<String, Notify<Request>> consumerFactory(
            @Qualifier("jsonMapper") ObjectMapper jsonMapper
    ) {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, notificationServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, notifyConsumerGroupId);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new GenericJsonDeserializer<>(jsonMapper, new TypeReference<>() {
                })
        );
    }

    @Bean
    @Qualifier("notifyListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Notify<Request>> notifyListenerContainerFactory(
            ConsumerFactory<String, Notify<Request>> consumerFactory
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Notify<Request>>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

}
