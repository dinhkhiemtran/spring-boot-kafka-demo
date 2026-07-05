package com.kafka_pushlish.demo.service;

import com.kafka_pushlish.demo.common.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GreetingProducerService {
  private final KafkaTemplate<String, String> kafkaTemplate;

  public void sendGreeting(String message) {
    kafkaTemplate.send(KafkaTopics.GREETING, message)
        .whenComplete((result, ex) -> {
          if (ex != null) {
            System.err.println("Failed to send: " + ex.getMessage());
          } else {
            System.out.println(
                "Sent to partition " + result.getRecordMetadata().partition()
                    + ", offset " + result.getRecordMetadata().offset());
          }
        });
  }
}