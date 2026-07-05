# Spring Boot Kafka Demo

A simple example demonstrating communication between two Spring Boot applications using Apache Kafka.

## Architecture

```
                +------------------+
                |   REST Client    |
                +--------+---------+
                         |
                         | POST /api/v1/greetings
                         |
                +--------v---------+
                |    Producer      |
                | Spring Boot      |
                +--------+---------+
                         |
                         | Publish Message
                         |
                +--------v---------+
                |      Kafka       |
                |   Topic: greeting|
                +--------+---------+
                         |
                         | Consume Message
                         |
                +--------v---------+
                |    Consumer      |
                | Spring Boot      |
                +------------------+
```

---

# Project Structure

```
.
├── docker-compose.yml
├── producer/
│   └── Spring Boot Producer
└── consumer/
    └── Spring Boot Consumer
```

---

# Prerequisites

- Docker
- Docker Compose
- Java 21 (optional if running locally)
- Maven (optional if running locally)

---

# Running the Project

Start all services:

```bash
docker compose up --build
```

or run in detached mode:

```bash
docker compose up -d --build
```

Verify containers:

```bash
docker ps
```

Expected containers:

```
kafka
kafka-producer
kafka-consumer
```

---

# Stopping

Stop containers:

```bash
docker compose down
```

Remove volumes (optional):

```bash
docker compose down -v
```

---

# Producer API

Send a message to Kafka.

Example:

```
POST http://localhost:8080/api/v1/greetings
```

Request Body

```
Hello Kafka
```

Response

```
Message sent successfully
```

---

# Consumer

The consumer listens to the topic:

```
greeting
```

Every new message published to Kafka will be printed in the application log.

Example:

```
Realtime received: Hello Kafka
```

---

# Docker Compose Explanation

## Kafka Service

```yaml
kafka:
```

Starts a single Kafka broker.

---

### Image

```yaml
image: apache/kafka:latest
```

Uses the official Apache Kafka image.

---

### Ports

```yaml
ports:
  - "9092:9092"
```

Exposes Kafka on port **9092**.

- Inside Docker: `kafka:9092`
- From host machine: `localhost:9092`

---

### CLUSTER_ID

```yaml
CLUSTER_ID
```

Unique identifier for the Kafka KRaft cluster.

It should remain unchanged after the cluster has been initialized.

---

### KAFKA_NODE_ID

```yaml
KAFKA_NODE_ID: 1
```

Broker identifier.

Since this demo has only one broker, its ID is **1**.

---

### KAFKA_PROCESS_ROLES

```yaml
broker,controller
```

Kafka runs in **KRaft mode**.

The same node acts as:

- Broker
- Controller

No ZooKeeper is required.

---

### KAFKA_LISTENERS

```yaml
PLAINTEXT://:9092
CONTROLLER://:9093
```

Defines the network interfaces Kafka listens on.

- 9092 → client connections
- 9093 → controller communication

---

### KAFKA_ADVERTISED_LISTENERS

```yaml
PLAINTEXT://kafka:9092
```

The address Kafka tells clients to use.

Since Producer and Consumer run inside Docker Compose, they connect using:

```
kafka:9092
```

---

### KAFKA_CONTROLLER_QUORUM_VOTERS

```yaml
1@kafka:9093
```

Defines the KRaft controller quorum.

For a single-node cluster:

- Node ID = 1
- Controller = kafka:9093

---

### Replication Settings

```yaml
KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1

KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1

KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1

KAFKA_DEFAULT_REPLICATION_FACTOR=1
```

These settings are required because this project uses **only one Kafka broker**.

Without them, Kafka will attempt to create internal topics with a replication factor of **3**, resulting in errors such as:

```
INVALID_REPLICATION_FACTOR
```

---

### Default Partitions

```yaml
KAFKA_NUM_PARTITIONS=3
```

New topics will have **3 partitions** by default.

---

# Producer Service

```yaml
producer:
```

Builds the Producer Spring Boot application.

Environment variable:

```yaml
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
```

The Producer connects to Kafka through Docker's internal network.

---

# Consumer Service

```yaml
consumer:
```

Builds the Consumer Spring Boot application.

Environment variable:

```yaml
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
```

The Consumer also connects using the Docker network.

---

# Why use `kafka:9092` instead of `localhost:9092`?

Inside Docker Compose, each container has its own network namespace.

For example:

```
Producer
```

cannot reach Kafka using

```
localhost:9092
```

because `localhost` refers to the Producer container itself.

Docker automatically creates an internal DNS.

Therefore,

```
kafka
```

resolves to the Kafka container.

```
Producer  -----> kafka:9092
Consumer  -----> kafka:9092
```

Outside Docker (on your local machine), use:

```
localhost:9092
```

---

# Kafka Topic

This demo uses one topic:

```
greeting
```

It is automatically created by the Producer application using:

```java
@Bean
public NewTopic greetingTopic() {
    return TopicBuilder.name("greeting")
            .partitions(3)
            .replicas(1)
            .build();
}
```

- Partitions: **3**
- Replication Factor: **1**

---

# Useful Docker Commands

View running containers:

```bash
docker ps
```

View logs:

```bash
docker logs kafka

docker logs kafka-producer

docker logs kafka-consumer
```

Enter Kafka container:

```bash
docker exec -it kafka bash
```

List topics:

```bash
/opt/kafka/bin/kafka-topics.sh \
--bootstrap-server localhost:9092 \
--list
```

Consume messages manually:

```bash
/opt/kafka/bin/kafka-console-consumer.sh \
--bootstrap-server localhost:9092 \
--topic greeting \
--from-beginning
```

Describe a topic:

```bash
/opt/kafka/bin/kafka-topics.sh \
--bootstrap-server localhost:9092 \
--describe \
--topic greeting
```

---

# Summary

This project demonstrates:

- Spring Boot Kafka Producer
- Spring Boot Kafka Consumer
- Kafka running in KRaft mode
- Docker Compose networking
- Automatic topic creation
- Message publishing
- Real-time message consumption
- Single-node Kafka configuration suitable for local development