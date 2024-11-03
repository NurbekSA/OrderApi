package org.example.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.kafka.proto.tutorial.KafkaMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PartitionListener {
    private KafkaSender kafkaSender;

    public PartitionListener(KafkaSender kafkaSender) {
        this.kafkaSender = kafkaSender;
    }

    // Слушатель для партиции 0
    @KafkaListener(topics = "payment-topic", groupId = "group-1")
    public void listenPartition0(ConsumerRecord<String, byte[]> record) {
        System.out.println("Партиция 0 получила сообщение");
        try {
            KafkaMessage message = KafkaMessage.parseFrom(record.value());

            System.out.println("Принято сообщение: " + message);
            System.out.println("Тип запроса: " + message.getRequestType());

            if(message.getRequestType() == KafkaMessage.RequestType.REQUEST){
                KafkaMessage kafkaMessage = KafkaMessage.newBuilder()
                        .setId("12345")
                        .setRequestType(KafkaMessage.RequestType.RESPONSE)
                        .setBody(Payment(message.getBody()))
                        .build();

                String topicName = "investment-topic"; // Укажите название вашего топика
                String key = message.getId(); // Используем id как ключ


                kafkaSender.sendMessage(topicName, message);
            }

        } catch (InvalidProtocolBufferException e) {
            System.err.println("Ошибка десериализации: " + e.getMessage());
        }
    }

    String Payment(String credential){
        try {
            Thread.sleep(1000);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return "Длинный чек об оплате";
    }



    // Слушатель для партиции 1
    @KafkaListener(topics = "investment-topic", groupId = "group-1")
    public void listenPartition1(ConsumerRecord<String, byte[]> record) {
        System.out.println("Партиция 1 получила сообщение");
        try {
            KafkaMessage message = KafkaMessage.parseFrom(record.value());

            System.out.println("Принято сообщение: " + message);
            System.out.println("Тип запроса: " + message.getRequestType());

            if(message.getRequestType() == KafkaMessage.RequestType.RESPONSE){
                System.out.println(message.getBody());
            }
        } catch (InvalidProtocolBufferException e) {
            System.err.println("Ошибка десериализации: " + e.getMessage());
        }
    }
}

