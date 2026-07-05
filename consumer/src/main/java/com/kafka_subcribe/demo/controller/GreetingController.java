package com.kafka_subcribe.demo.controller;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {
  @KafkaListener(topics = "greeting", groupId = "greeting-group")
  public void consume(String message) {
    System.out.println("Received: " + message);
  }

  @GetMapping("/greeting")
  public String greeting() {
    return "Consumer is running";
  }
}
