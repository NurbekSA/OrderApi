package amanzat.com.kafka;

import amanzat.com.kafka.proto.tutorial.KafkaMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class KafkaRequestReply {
    private static final Logger logger = LoggerFactory.getLogger(KafkaRequestReply.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ConcurrentHashMap<String, CompletableFuture<KafkaMessage>> pendingRequests = new ConcurrentHashMap<>();

    /**
     * Отправляет запрос в Kafka и возвращает CompletableFuture для получения ответа.
     *
     * @param data          Данные запроса.
     * @param requestTopic  Топик для отправки запроса.
     * @param responseTopic Топик для получения ответа.
     * @return CompletableFuture, которое завершится при получении ответа.
     */
    public CompletableFuture<KafkaMessage> sendRequest(String data, String requestTopic, String responseTopic) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<KafkaMessage> futureResponse = new CompletableFuture<>();
        pendingRequests.put(requestId, futureResponse);

        logger.info("SENDREQUEST: Отправка запроса. requestId={}, requestTopic={}, responseTopic={}, data={}",
                requestId, requestTopic, responseTopic, data);

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

    /**
     * Слушает ответы из Kafka и обрабатывает их.
     *
     * @param messageBytes Полученные байты сообщения.
     */
    @KafkaListener(topics = "amanzat.order-api.response", groupId = "shared-listener-group")
    public void listen(byte[] messageBytes) {
        logger.debug("LISTEN: Получено сообщение для обработки.");

        try {
            KafkaMessage kafkaMessage = KafkaMessage.parseFrom(messageBytes);
            String requestId = kafkaMessage.getCorrelationId();

            logger.info("LISTEN: Обработка сообщения. requestId={}, body={}", requestId, kafkaMessage.getBody());

            // Завершение future и удаление из карты ожидания
            CompletableFuture<KafkaMessage> futureResponse = pendingRequests.remove(requestId);
            if (futureResponse != null) {
                futureResponse.complete(kafkaMessage);
                logger.info("LISTEN: Будущее завершено для requestId={}", requestId);
            } else {
                logger.warn("LISTEN: Не найдено ожидающееся будущее для requestId={}", requestId);
            }
        } catch (InvalidProtocolBufferException e) {
            // Обработка ошибки
            logger.error("LISTEN: Ошибка при разборе сообщения: {}", e.getMessage(), e);
        } catch (Exception e) {
            // Обработка любых других ошибок
            logger.error("LISTEN: Неизвестная ошибка при обработке сообщения: {}", e.getMessage(), e);
        }
    }
}
