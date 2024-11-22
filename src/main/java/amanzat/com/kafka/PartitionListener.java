package amanzat.com.kafka;

import amanzat.com.exception.KafkaRequestFailedException;
import amanzat.com.kafka.proto.tutorial.KafkaMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PartitionListener {
    private final KafkaSender kafkaSender;
    private static final Logger logger = LoggerFactory.getLogger(PartitionListener.class);

    public PartitionListener(KafkaSender kafkaSender) {
        this.kafkaSender = kafkaSender;
    }

    // Listener for partition 0
    @KafkaListener(topics = "amanzat.box-api.request.block-get-cost", groupId = "group-0")
    public void blockGetCost(ConsumerRecord<String, byte[]> messageBeforeParse) {
        logger.info("LISTENPARTITION0: Received message from topic 'amanzat.box-api.request.block-get-cost'");
        KafkaMessage messageAfterParse;

        try {
            messageAfterParse = KafkaMessage.parseFrom(messageBeforeParse.value());
        } catch (InvalidProtocolBufferException e) {
            throw new KafkaRequestFailedException("LISTENPARTITION0: Failed to parse Kafka message: " + e.getMessage());
        }

        // todo: The box should be set to block
        logger.info("LISTENPARTITION0: Correlation ID: {}", messageAfterParse.getCorrelationId());

        KafkaMessage responseMessage = KafkaMessage.newBuilder()
                .setCorrelationId(messageAfterParse.getCorrelationId())
                .setRequestType(KafkaMessage.RequestType.RESPONSE)
                .setRequestResult(KafkaMessage.RequestResult.SUCCESS)
                .setBody("" + 6300d) // todo: This should fetch data from the database
                .build();

        logger.info("LISTENPARTITION0: Response message sent with topic: {}", messageAfterParse.getReplyTo());
        kafkaSender.sendMessage(messageAfterParse.getReplyTo(), responseMessage);
        logger.info("LISTENPARTITION0: Response message sent with body: {}", responseMessage.getBody());
    }

    // Listener for partition 1
    @KafkaListener(topics = "amanzat.box-api.get-cost", groupId = "group-1")
    public void getCost(ConsumerRecord<String, byte[]> messageBeforeParse) {
        logger.info("LISTENPARTITION1: Received message from topic 'amanzat.order-api.response'");
        KafkaMessage messageAfterParse;

        try {
            messageAfterParse = KafkaMessage.parseFrom(messageBeforeParse.value());
        } catch (InvalidProtocolBufferException e) {
            throw new KafkaRequestFailedException("LISTENPARTITION1: Failed to parse Kafka message: " + e.getMessage());
        }

        logger.info("LISTENPARTITION1: Correlation ID: {}", messageAfterParse.getCorrelationId());

        KafkaMessage responseMessage = KafkaMessage.newBuilder()
                .setCorrelationId(messageAfterParse.getCorrelationId())
                .setRequestType(KafkaMessage.RequestType.RESPONSE)
                .setRequestResult(KafkaMessage.RequestResult.SUCCESS)
                .setBody("" + 6300d) // todo: This should fetch data from the database
                .build();

        kafkaSender.sendMessage(messageAfterParse.getReplyTo(), responseMessage);
        logger.info("LISTENPARTITION1: Response message sent with body: {}", responseMessage.getBody());
    }

    @KafkaListener(topics = "amanzat.box-api.Unblock", groupId = "group-1")
    public void Unblock(ConsumerRecord<String, byte[]> messageBeforeParse) {
        logger.info("LISTENPARTITION3: Received message from topic 'amanzat.order-api.response'");
        KafkaMessage messageAfterParse;

        try {
            messageAfterParse = KafkaMessage.parseFrom(messageBeforeParse.value());
        } catch (InvalidProtocolBufferException e) {
            throw new KafkaRequestFailedException("LISTENPARTITION3: Failed to parse Kafka message: " + e.getMessage());
        }

        logger.info("LISTENPARTITION3: Correlation ID: {}", messageAfterParse.getCorrelationId());

        KafkaMessage responseMessage = KafkaMessage.newBuilder()
                .setCorrelationId(messageAfterParse.getCorrelationId())
                .setRequestType(KafkaMessage.RequestType.RESPONSE)
                .setRequestResult(KafkaMessage.RequestResult.SUCCESS)
                .build();

        kafkaSender.sendMessage(messageAfterParse.getReplyTo(), responseMessage);
        logger.info("LISTENPARTITION3: Response message sent with body: {}", responseMessage.getBody());
    }

    @KafkaListener(topics = "amanzat.user-api.chek-jwt", groupId = "group-1")
    public void chekJwt(ConsumerRecord<String, byte[]> messageBeforeParse) {
        logger.info("LISTENPARTITION4: Received message from topic 'amanzat.user-api.chek-jwt'");
        KafkaMessage messageAfterParse;

        try {
            messageAfterParse = KafkaMessage.parseFrom(messageBeforeParse.value());
        } catch (InvalidProtocolBufferException e) {
            throw new KafkaRequestFailedException("LISTENPARTITION4: Failed to parse Kafka message: " + e.getMessage());
        }

        logger.info("LISTENPARTITION4: Correlation ID: {}", messageAfterParse.getCorrelationId());

        KafkaMessage responseMessage = KafkaMessage.newBuilder()
                .setCorrelationId(messageAfterParse.getCorrelationId())
                .setRequestType(KafkaMessage.RequestType.RESPONSE)
                .setRequestResult(KafkaMessage.RequestResult.SUCCESS)
                .setBody("{\"role\":\"USER\",\"space\":60013513}")
                .build();

        kafkaSender.sendMessage(messageAfterParse.getReplyTo(), responseMessage);
        logger.info("LISTENPARTITION4: Response message sent with body: {}", responseMessage.getBody());
    }
}


