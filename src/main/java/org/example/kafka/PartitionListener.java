package org.example.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.persistence.model.exception.KafkaRequestFailedException;
import org.example.kafka.proto.tutorial.KafkaMessage;
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
    public void listenPartition0(ConsumerRecord<String, byte[]> messageBeforeParse) {
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
    public void listenPartition1(ConsumerRecord<String, byte[]> messageBeforeParse) {
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
}
