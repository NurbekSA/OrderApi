package org.example.kafka;



import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import org.example.kafka.proto.tutorial.KafkaMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class KafkaRequestReply{
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ConcurrentHashMap<String, CompletableFuture<KafkaMessage>> pendingRequests = new ConcurrentHashMap<>();


    public CompletableFuture<KafkaMessage> sendRequest(String data, String requestTopic, String responseTopic) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<KafkaMessage> futureResponse = new CompletableFuture<>();
        pendingRequests.put(requestId, futureResponse);

        // Создание и отправка сообщения
        KafkaMessage requestMessage = KafkaMessage.newBuilder()
                .setCorrelationId(requestId)
                .setRequestType(KafkaMessage.RequestType.REQUEST)
                .setBody(data)
                .setReplyTo(responseTopic)
                .build();

        kafkaTemplate.send(requestTopic, requestMessage.toByteArray());

        // Возвращаем future, который завершится при получении ответа
        return futureResponse;
    }

    @KafkaListener(topics = "amanzat.order-api.response", groupId = "shared-listener-group")
    public void listen(byte[] messageBytes) {
        try {
            KafkaMessage kafkaMessage = KafkaMessage.parseFrom(messageBytes);
            String requestId = kafkaMessage.getCorrelationId();

            // Завершение future и удаление из карты ожидания
            CompletableFuture<KafkaMessage> futureResponse = pendingRequests.remove(requestId);
            if (futureResponse != null) {
                futureResponse.complete(kafkaMessage);
            }
        } catch (InvalidProtocolBufferException e) {
            // Обработка ошибки
            e.printStackTrace();
        }
    }
}
