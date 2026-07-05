package com.kafka_pushlish.demo.controller;

import com.kafka_pushlish.demo.dto.GreetingRequest;
import com.kafka_pushlish.demo.service.GreetingProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GreetingController {

  private final GreetingProducerService producerService;

  @PostMapping("/greetings")
  public ResponseEntity<String> sendGreeting(@RequestBody GreetingRequest request) {
    producerService.sendGreeting(request.getMessage());
    return ResponseEntity.ok("Message sent successfully");
  }
}
