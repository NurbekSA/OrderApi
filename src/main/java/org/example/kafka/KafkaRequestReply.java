package org.example.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.kafka.proto.tutorial.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaRequestReply {

    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    public CompletableFuture<KafkaMessage> sendRequest(String data, String requestTopic, String responseTopic) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<KafkaMessage> futureResponse = new CompletableFuture<>();

        MessageListenerContainer container = registry.getListenerContainer("responseListener");
        if (container != null && !container.isRunning()) {
            container.start();
        }

        container.setupMessageListener((MessageListener<String, byte[]>) message -> {
            try {
                KafkaMessage kafkaMessage = KafkaMessage.parseFrom(message.value());
                if (kafkaMessage.getId().equals(requestId)) {
                    futureResponse.complete(kafkaMessage);
                    container.stop();
                }
            } catch (InvalidProtocolBufferException e) {
                futureResponse.completeExceptionally(e);
            }
        });

        KafkaMessage requestMessage = KafkaMessage.newBuilder()
                .setId(requestId)
                .setRequestType(KafkaMessage.RequestType.REQUEST)
                .setMethosType(KafkaMessage.MethodType.GET)
                .setBody(data)
                .setReplyTo(responseTopic)
                .build();

        byte[] messageBytes = requestMessage.toByteArray();
        kafkaTemplate.send(requestTopic, messageBytes);

        return futureResponse;
    }

    // Новый метод для прослушивания топика investment-topic
    @KafkaListener(id = "investment-topic", topics = "investment-topic")
    public void listenInvestmentTopic(ConsumerRecord<String, byte[]> record) {
        try {
            KafkaMessage kafkaMessage = KafkaMessage.parseFrom(record.value());
            System.out.println("Получено сообщение из investment-topic: " + kafkaMessage);
            // Здесь вы можете добавить логику обработки сообщений из investment-topic
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
}
