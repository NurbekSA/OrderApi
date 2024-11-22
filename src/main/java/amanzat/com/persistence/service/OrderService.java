package amanzat.com.persistence.service;

import amanzat.com.exception.KafkaResponseFailedException;
import amanzat.com.exception.KafkaResponseParseException;
import amanzat.com.exception.ResourceNotFoundException;
import amanzat.com.kafka.proto.tutorial.KafkaMessage;
import amanzat.com.persistence.repository.OrderRepo;
import lombok.RequiredArgsConstructor;
import amanzat.com.persistence.model.dto.OrderDTO;
import amanzat.com.persistence.model.dto.Status;
import amanzat.com.persistence.model.entity.OrderModel;
import amanzat.com.kafka.KafkaRequestReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final KafkaRequestReply kafkaRequestReply;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private OrderDTO convertToDto(OrderModel order) {
        logger.debug("CONVERT_TO_DTO: Конвертация OrderModel в OrderDTO. ID: {}", order.getId());
        return new OrderDTO(order.getId(), order.getUserId(), order.getBoxId(),
                Status.fromValue(order.getStatus()), order.getBookingDateTime(), order.getRentalPeriod(), order.getOrderAmount());
    }

    private OrderModel convertToEntity(OrderDTO orderDTO) {
        logger.debug("CONVERT_TO_ENTITY: Конвертация OrderDTO в OrderModel. ID: {}", orderDTO.getId());
        return new OrderModel(orderDTO.getId(), orderDTO.getUserId(), orderDTO.getBoxId(), orderDTO.getStatus().getValue(),
                orderDTO.getBookingDateTime(), orderDTO.getRentalPeriod(), orderDTO.getOrderAmount());
    }

    public OrderDTO getByUserId(String id) {
        logger.info("GET_BY_USER_ID: Получение заказа по User ID: {}", id);
        OrderModel order = orderRepo.findByUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException("GET_BY_USER_ID: Заказ не найден."));
        return convertToDto(order);
    }

    public OrderDTO getByBoxId(String id) {
        logger.info("GET_BY_BOX_ID: Получение заказа по Box ID: {}", id);
        OrderModel order = orderRepo.findByBoxId(id)
                .orElseThrow(() -> new ResourceNotFoundException("GET_BY_BOX_ID: Заказ не найден."));
        return convertToDto(order);
    }

    public List<OrderDTO> findAll() {
        logger.info("FIND_ALL: Получение всех заказов.");
        List<OrderModel> orders = orderRepo.findAll();
        if (orders.isEmpty()) throw new ResourceNotFoundException("FIND_ALL: Заказы отсутствуют.");
        return orders.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO create(OrderDTO orderDTO) {
        logger.info("CREATE: Начало создания заказа. User ID: {}", orderDTO.getUserId());

        CompletableFuture<KafkaMessage> response = kafkaRequestReply.sendRequest("", "amanzat.box-api.request.block-get-cost", "amanzat.order-api.response");
        logger.info("CREATE: Запрос отправлен в топик - amanzat.box-api.request.block-get-cost");

        KafkaMessage result;
        try {
            result = response.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaResponseFailedException("CREATE: BoxService. Прерывание потока (InterruptedException).");
        } catch (ExecutionException e) {
            throw new KafkaResponseFailedException("CREATE: BoxService. Ошибка выполнения (ExecutionException). Message: " + e.getCause().getMessage());
        }

        String resultBody = result.getBody();
        logger.info("CREATE: Ответ получен: {}", resultBody);

        if (result.getRequestResult() == KafkaMessage.RequestResult.FAILED || resultBody.isEmpty()) {
            throw new KafkaResponseFailedException("CREATE: BoxService -> Ошибка или пустое тело ответа. Body: " + resultBody);
        }

        double summ;
        try {
            summ = Double.parseDouble(result.getBody());
        } catch (NumberFormatException exception) {
            throw new KafkaResponseParseException("CREATE: BoxService -> Некорректный формат числа. Body: " + resultBody);
        }

        orderDTO.setStatus(Status.CREATED);
        OrderModel order = convertToEntity(orderDTO);
        order.setId(UUID.randomUUID().toString());
        order.setOrderAmount(summ * order.getRentalPeriod());
        order.setStatus(Status.CREATED.getValue());
        order.setBookingDateTime(System.currentTimeMillis());

        logger.debug("CREATE: Создан объект OrderModel: {}", order);

        OrderModel savedOrder = orderRepo.save(order);
        logger.info("CREATE: Заказ успешно сохранен с ID: {}", savedOrder.getId());
        return convertToDto(savedOrder);
    }

    public OrderDTO extend(String id, int rentalPeriod) {
        logger.info("EXTEND: Увеличение срока аренды для заказа с ID: {}", id);

        OrderModel extendOrder = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EXTEND: Заказ не найден."));

        CompletableFuture<KafkaMessage> response = kafkaRequestReply.sendRequest(extendOrder.getBoxId(), "amanzat.box-api.get-cost", "amanzat.order-api.response");
        logger.info("EXTEND: Запрос отправлен в топик - amanzat.box-api.get-cost");

        KafkaMessage result;
        try {
            result = response.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaResponseFailedException("EXTEND: BoxService. Прерывание потока (InterruptedException).");
        } catch (ExecutionException e) {
            throw new KafkaResponseFailedException("EXTEND: BoxService. Ошибка выполнения (ExecutionException). Message: " + e.getCause().getMessage());
        }

        String resultBody = result.getBody();
        logger.info("EXTEND: Ответ получен: {}", resultBody);

        if (result.getRequestResult() == KafkaMessage.RequestResult.FAILED || resultBody.isEmpty()) {
            throw new KafkaResponseFailedException("EXTEND: BoxService -> Ошибка или пустое тело ответа. Body: " + resultBody);
        }

        double summ;
        try {
            summ = Double.parseDouble(result.getBody());
        } catch (NumberFormatException exception) {
            throw new KafkaResponseParseException("EXTEND: BoxService -> Некорректный формат числа. Body: " + resultBody);
        }

        extendOrder.setOrderAmount(extendOrder.getOrderAmount() + summ * rentalPeriod);
        extendOrder.setRentalPeriod(extendOrder.getRentalPeriod() + rentalPeriod);

        logger.info("EXTEND: Продление завершено. Новая сумма: {}, Новый срок аренды: {}", extendOrder.getOrderAmount(), extendOrder.getRentalPeriod());

        orderRepo.save(extendOrder);
        return convertToDto(extendOrder);
    }

    public void delete(String id) {
        logger.info("DELETE: Удаление заказа с ID: {}", id);

        OrderModel order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DELETE: Заказ не найден."));

        logger.info("DELETE: Логическое удаление заказа с ID: {}", id);
        order.setStatus(Status.DELETED.getValue());
        orderRepo.save(order);
    }
}
