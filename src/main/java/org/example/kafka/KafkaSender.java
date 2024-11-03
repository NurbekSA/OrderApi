package org.example.kafka;

import org.example.kafka.proto.tutorial.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@EnableKafka
public class KafkaSender {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    @Autowired
    public KafkaSender(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topicName, KafkaMessage message) {
        byte[] messageBytes = message.toByteArray();
        kafkaTemplate.send(topicName, messageBytes);
        System.out.println("Отправлено сообщение: " + message);
    }
}
