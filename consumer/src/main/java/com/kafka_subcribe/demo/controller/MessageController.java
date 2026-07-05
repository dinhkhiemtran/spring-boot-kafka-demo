package com.kafka_subcribe.demo.controller;

import com.kafka_subcribe.demo.service.KafkaReplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
  private final KafkaReplayService kafkaReplayService;

  @GetMapping("/replay")
  public List<String> replayAllMessages() {
    return kafkaReplayService.getAllMessages();
  }
}
