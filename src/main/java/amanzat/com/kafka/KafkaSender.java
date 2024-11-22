package amanzat.com.kafka;

import amanzat.com.kafka.proto.tutorial.KafkaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@EnableKafka
public class KafkaSender {

    private static final Logger logger = LoggerFactory.getLogger(KafkaSender.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Autowired
    public KafkaSender(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topicName, KafkaMessage message) {
        logger.info("SEND_MESSAGE: Начало отправки сообщения в топик: {}", topicName);

        try {
            byte[] messageBytes = message.toByteArray();
            logger.debug("SEND_MESSAGE: Сообщение сериализовано в байты. Размер: {} байт.", messageBytes.length);

            kafkaTemplate.send(topicName, messageBytes);
            logger.info("SEND_MESSAGE: Сообщение успешно отправлено в топик: {}", topicName);
        } catch (Exception e) {
            logger.error("SEND_MESSAGE: Ошибка при отправке сообщения в топик: {}. Ошибка: {}", topicName, e.getMessage(), e);
            throw e;
        }
    }
}
