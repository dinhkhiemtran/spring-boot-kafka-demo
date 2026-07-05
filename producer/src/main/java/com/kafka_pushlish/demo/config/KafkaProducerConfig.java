package com.kafka_pushlish.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
  @Bean
  public ProducerFactory<String, String> producerFactory() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv().getOrDefault("SPRING_KAFKA_BOOTSTRAP_SERVERS",
            "localhost:9092"));
    configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    return new DefaultKafkaProducerFactory<>(configs);
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate(
      ProducerFactory<String, String> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }

  @Bean
  public org.springframework.kafka.core.KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        System.getenv().getOrDefault(
            "SPRING_KAFKA_BOOTSTRAP_SERVERS",
            "localhost:9092"
        )
    );
    return new org.springframework.kafka.core.KafkaAdmin(configs);
  }

  @Bean
  public NewTopic greetingTopic() {
    return TopicBuilder.name("greeting")
        .partitions(3)
        .replicas(1)
        .build();
  }
}