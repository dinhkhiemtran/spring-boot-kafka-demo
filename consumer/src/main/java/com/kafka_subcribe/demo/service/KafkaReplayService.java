package com.kafka_subcribe.demo.service;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Service
public class KafkaReplayService {
  private static final String TOPIC = "greeting";

  public List<String> getAllMessages() {
    Properties props = new Properties();
    props.put(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
        System.getenv().getOrDefault(
            "SPRING_KAFKA_BOOTSTRAP_SERVERS",
            "localhost:9092"));
    props.put(
        ConsumerConfig.GROUP_ID_CONFIG,
        "replay-" + UUID.randomUUID());
    props.put(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class);
    props.put(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class);
    props.put(
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
        "earliest");
    List<String> messages = new ArrayList<>();
    try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
      List<PartitionInfo> partitionInfos = consumer.partitionsFor(TOPIC);
      List<TopicPartition> partitions = new ArrayList<>();
      for (PartitionInfo info : partitionInfos) {
        partitions.add(new TopicPartition(TOPIC, info.partition()));
      }
      consumer.assign(partitions);
      consumer.seekToBeginning(partitions);
      while (true) {
        ConsumerRecords<String, String> records =
            consumer.poll(Duration.ofSeconds(1));
        if (records.isEmpty()) {
          break;
        }
        for (ConsumerRecord<String, String> record : records) {
          messages.add(record.value());
        }
      }
    }
    return messages;
  }
}