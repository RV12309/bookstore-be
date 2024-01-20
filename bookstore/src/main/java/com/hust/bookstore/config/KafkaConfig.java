package com.hust.bookstore.config;

import com.hust.bookstore.dto.notify.Notify;
import com.hust.bookstore.dto.notify.Request;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

@Configuration
public class KafkaConfig {

    @Value("${kafka-bootstrap-servers}")
    private String notificationServer;

    @Value("${kafka-consumer-group-id}")
    private String notifyConsumerGroupId;

    @Bean
    @Qualifier("internalProducerFactory")
    public ProducerFactory<String, Notify<Request>> internalProducerFactory(
            @Value("#{${spring.kafka.internal-producer.options:{}}}") Map<String, Object> options) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, notificationServer);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        if (nonNull(options)) props.putAll(options);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    @Qualifier("internalKafkaTemplate")
    public KafkaTemplate<String, Notify<Request>> internalKafkaTemplate(
            @Qualifier("internalProducerFactory") ProducerFactory<String, Notify<Request>> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }
    @Bean
    KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, notificationServer);
        return new KafkaAdmin(configs);
    }

}
