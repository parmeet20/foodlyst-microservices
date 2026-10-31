package com.service.balanceService.config;

import com.service.balanceService.dto.InventoryReserveResponseEvent;
import com.service.balanceService.dto.PaymentRequestEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Autowired
    private KafkaProperties kafkaProperties;

    // Common consumer properties WITHOUT JsonDeserializer config
    private Map<String, Object> commonConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    // === Topics ===
    @Bean
    public NewTopic paymentTransactionTopic() {
        return new NewTopic("payment-transaction-topic", 2, (short) 1);
    }

    @Bean
    public NewTopic inventoryReserveEvent() {
        return new NewTopic("inventory.reserve.event", 2, (short) 1);
    }

    @Bean
    public NewTopic inventoryResponseEvent() {
        return new NewTopic("inventory.response.event", 2, (short) 1);
    }

    // === PaymentRequestEvent Consumer - Using SETTERS only ===
    @Bean
    public ConsumerFactory<String, PaymentRequestEvent> paymentRequestEventConsumerFactory() {
        Map<String, Object> props = commonConsumerProps();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "balance-service-group");

        // Configure through SETTERS only (no properties)
        JsonDeserializer<PaymentRequestEvent> deserializer = new JsonDeserializer<>(PaymentRequestEvent.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        deserializer.setRemoveTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentRequestEvent> paymentRequestKafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentRequestEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(paymentRequestEventConsumerFactory());
        return factory;
    }

    // === InventoryReserveResponseEvent Consumer - Using SETTERS only ===
    @Bean
    public ConsumerFactory<String, InventoryReserveResponseEvent> inventoryResponseEventConsumerFactory() {
        Map<String, Object> props = commonConsumerProps();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "balance-service-group");

        // Configure through SETTERS only (no properties)
        JsonDeserializer<InventoryReserveResponseEvent> deserializer = new JsonDeserializer<>(InventoryReserveResponseEvent.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        deserializer.setRemoveTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryReserveResponseEvent> inventoryResponseKafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InventoryReserveResponseEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(inventoryResponseEventConsumerFactory());
        return factory;
    }

    // === String Consumer - Using PROPERTIES only ===
    @Bean
    public ConsumerFactory<String, String> stringConsumerFactory() {
        Map<String, Object> props = commonConsumerProps();
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "balance-group");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> stringKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stringConsumerFactory());
        return factory;
    }
}