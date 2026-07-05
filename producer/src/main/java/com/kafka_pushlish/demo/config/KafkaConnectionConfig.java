package com.kafka_pushlish.demo.config;

import org.apache.kafka.clients.producer.Producer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConnectionConfig {
  @Bean
  public CommandLineRunner kafkaConnectionChecker(ProducerFactory<String, String> producerFactory) {
    return args -> {
      try (Producer<String, String> producer =
               producerFactory.createProducer()) {
        producer.partitionsFor("greeting");
        System.out.println("Connected to Kafka");
      } catch (Exception e) {
        throw new IllegalStateException("Cannot connect to Kafka", e);
      }
    };
  }
}