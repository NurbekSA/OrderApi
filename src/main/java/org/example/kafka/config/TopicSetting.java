package org.example.kafka.config;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Component
public class TopicSetting {

    @PostConstruct
    public void topicCreater() {
        // Настройка подключения к Kafka
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");



        // Создание AdminClient для управления топиками
        try (AdminClient adminClient = AdminClient.create(props)) {

            // Создание топика в Kafka
            adminClient.createTopics(Collections.singletonList(new NewTopic("amanzat.order-api.response", 4, (short) 1)));
            adminClient.createTopics(Collections.singletonList(new NewTopic("amanzat.box-api.request.block-get-cost", 1, (short) 1)));
            adminClient.createTopics(Collections.singletonList(new NewTopic("amanzat.box-api.get-cost", 1, (short) 1)));
            System.out.println("Топик создан успешно!");
        } catch (Exception e) {
            System.err.println("Ошибка при создании топика: " + e.getMessage());
        }
    }


    public void info() {
        // Настройка подключения к Kafka
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // Создание AdminClient для управления топиками
        try (AdminClient adminClient = AdminClient.create(props)) {
            // Получение информации о всех топиках
            Set<String> topicNames = adminClient.listTopics().names().get();
            System.out.println("Список топиков: " + topicNames);

            // Получение подробной информации о конкретном топике
            TopicDescription description = adminClient.describeTopics(
                    Collections.singletonList("order-topic")).all().get().get("order-topic");
            System.out.println("Описание топика: " + description);
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Ошибка при получении информации о топиках: " + e.getMessage());
            Thread.currentThread().interrupt(); // Восстановление флага прерывания
        }
    }
}
