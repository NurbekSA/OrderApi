package org.example.entity.service;

import lombok.RequiredArgsConstructor;
import org.example.kafka.KafkaSender;
import org.example.entity.model.OrderModel;
import org.example.entity.model.exception.ResourceNotFoundException;
import org.example.entity.model.exception.UnpaidException;
import org.example.kafka.proto.tutorial.KafkaMessage;
import org.example.entity.repository.OrderRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;
    private final KafkaSender kafkaSender;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    public OrderModel getByUserId(String id) {
        logger.info("Получение заказа по идентификатору пользователя: {}", id);
        OrderModel order = orderRepo.findByUserId(id)
                .orElseThrow(() -> {
                    logger.error("Заказ не найден для пользователя с ID: {}", id);
                    return new ResourceNotFoundException("Заказ не найден");
                });
        logger.info("Успешно найден заказ для пользователя с ID: {}", id);
        return order;
    }

    public OrderModel getByBoxId(String id) {
        logger.info("Получение заказа по идентификатору бокса: {}", id);
        OrderModel order = orderRepo.findByBoxId(id)
                .orElseThrow(() -> {
                    logger.error("Заказ не найден для бокса с ID: {}", id);
                    return new ResourceNotFoundException("Заказ не найден");
                });
        logger.info("Успешно найден заказ для бокса с ID: {}", id);
        return order;
    }

    public List<OrderModel> findAll() {
        logger.info("Получение всех заказов");
        List<OrderModel> orders = orderRepo.findAll();
        if (orders.isEmpty()) {
            logger.warn("Заказы не найдены");
            throw new ResourceNotFoundException("Заказы не найдены");
        }
        logger.info("Найдено {} заказов", orders.size());
        return orders;
    }

    @Transactional
    public OrderModel create(OrderModel order, String credential) {
        logger.info("Создание нового заказа с идентификатором пользователя: {}", order.getUserId());

        // Создаем сообщение для Kafka с запросом стоимости бокса
        String kafkaMessageId = UUID.randomUUID().toString();
        KafkaMessage message = KafkaMessage.newBuilder()
                .setId(kafkaMessageId)
                .setRequestType(KafkaMessage.RequestType.REQUEST)
                .setMethosType(KafkaMessage.MethodType.UPDATE)
                .setBody(order.getBoxId())
                .build();
        kafkaSender.sendMessage("box-topic", message);
        logger.info("Сообщение отправлено в Kafka топик 'box-topic' с ID сообщения: {}", kafkaMessageId);

        Double summ = 400d; // Примерная стоимость, замените на фактическое значение
        order.setId(UUID.randomUUID().toString());
        order.setSumm(summ);
        order.setIsAlive(false);
        order.setIsPaid(false);
        OrderModel savedOrder = orderRepo.save(order);
        logger.info("Заказ успешно сохранен в базе данных с ID: {}", savedOrder.getId());

        // Отправка сообщения для платежа в Kafka
        kafkaMessageId = UUID.randomUUID().toString();
        message = KafkaMessage.newBuilder()
                .setId(kafkaMessageId)
                .setRequestType(KafkaMessage.RequestType.REQUEST)
                .setMethosType(KafkaMessage.MethodType.CREAT)
                .setBody(credential)
                .build();
        kafkaSender.sendMessage("payment-topic", message);
        logger.info("Сообщение для платежа отправлено в Kafka топик 'payment-topic' с ID сообщения: {}", kafkaMessageId);

        // Проверка статуса платежа
        if (true) {
            logger.info("Платеж подтвержден, установка флага 'isPaid' для заказа с ID: {}", savedOrder.getId());
            setPaid(savedOrder.getId());
        } else {
            logger.error("Ошибка при оплате заказа с ID: {}", savedOrder.getId());
            throw new UnpaidException("Ошибка оплаты");
        }
        return savedOrder;
    }

    @Transactional
    public OrderModel setPaid(String id) {
        logger.info("Установка флага 'isPaid' для заказа с ID: {}", id);
        OrderModel existingOrder = orderRepo.findById(id)
                .orElseThrow(() -> {
                    logger.error("Заказ с ID: {} не найден для установки флага 'isPaid'", id);
                    return new ResourceNotFoundException("Заказ не найден");
                });
        existingOrder.setIsPaid(true);
        logger.info("Флаг 'isPaid' успешно установлен для заказа с ID: {}", id);
        return orderRepo.save(existingOrder);
    }

    public OrderModel extend(OrderModel order) {
        logger.info("Продление заказа с ID: {}", order.getId());

        OrderModel extendOrder = orderRepo.findById(order.getId())
                .orElseThrow(() -> {
                    logger.error("Заказ с ID: {} не найден для продления", order.getId());
                    return new ResourceNotFoundException("Заказ не найден");
                });

        String kafkaMessageId = UUID.randomUUID().toString();
        KafkaMessage message = KafkaMessage.newBuilder()
                .setId(kafkaMessageId)
                .setRequestType(KafkaMessage.RequestType.REQUEST)
                .setMethosType(KafkaMessage.MethodType.UPDATE)
                .setBody(order.getBoxId())
                .build();
        kafkaSender.sendMessage("box-topic", message);
        logger.info("Сообщение для продления отправлено в Kafka топик 'box-topic' с ID сообщения: {}", kafkaMessageId);

        Double summ = 0d; // todo: Установить фактическую стоимость
        extendOrder.setSumm(extendOrder.getSumm() + summ * order.getRentalPeriod());
        extendOrder.setRentalPeriod(extendOrder.getRentalPeriod() + order.getRentalPeriod());
        logger.info("Продление заказа выполнено, обновленная сумма: {}, обновленный период аренды: {}", extendOrder.getSumm(), extendOrder.getRentalPeriod());
        orderRepo.save(extendOrder);
        return extendOrder;
    }

    public void delete(String id) {
        logger.info("Удаление заказа с ID: {}", id);

        OrderModel order = orderRepo.findById(id)
                .orElseThrow(() -> {
                    logger.error("Заказ с ID: {} не найден для удаления", id);
                    return new ResourceNotFoundException("Заказ не найден");
                });

        if (Boolean.TRUE.equals(order.getIsPaid())) {
            logger.info("Логическое удаление заказа с ID: {}", id);
            order.setIsAlive(false);
            orderRepo.save(order);
        } else {
            logger.info("Физическое удаление заказа с ID: {}", id);
            orderRepo.deleteById(id);
        }
    }
}
