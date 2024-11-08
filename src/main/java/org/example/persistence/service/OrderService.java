package org.example.persistence.service;

import lombok.RequiredArgsConstructor;
import org.example.persistence.model.dto.OrderDTO;
import org.example.persistence.model.entity.OrderModel;
import org.example.persistence.model.exception.KafkaResponseFailedException;
import org.example.persistence.model.exception.ResourceNotFoundException;
import org.example.kafka.KafkaRequestReply;
import org.example.kafka.proto.tutorial.KafkaMessage;
import org.example.persistence.repository.OrderRepo;
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
        return new OrderDTO(order.getId(), order.getUserId(), order.getBoxId(), order.getIsPaid(),
                order.getIsAlive(), order.getBookingDateTime(), order.getRentalPeriod(), order.getSumm(), order.getItemCategory());
    }

    private OrderModel convertToEntity(OrderDTO orderDTO) {
        return new OrderModel(orderDTO.getId(), orderDTO.getUserId(), orderDTO.getBoxId(), orderDTO.getIsPaid(),
                orderDTO.getIsAlive(), orderDTO.getBookingDateTime(), orderDTO.getRentalPeriod(), orderDTO.getSumm(), orderDTO.getItemCategory());
    }

    public OrderDTO getByUserId(String id) {
        logger.info("GETBYUSERID: Fetching order by user ID: {}", id);
        OrderModel order = orderRepo.findByUserId(id).orElseThrow(() -> new ResourceNotFoundException("getByUserId. Order not found"));
        return convertToDto(order);
    }

    public OrderDTO getByBoxId(String id) {
        logger.info("GETBYBOXID: Fetching order by box ID: {}", id);
        OrderModel order = orderRepo.findByBoxId(id).orElseThrow(() -> new ResourceNotFoundException("getByBoxId. Order not found"));
        return convertToDto(order);
    }

    public List<OrderDTO> findAll() {
        logger.info("FINDALL: Fetching all orders");
        List<OrderModel> orders = orderRepo.findAll();
        if (orders.isEmpty()) throw new ResourceNotFoundException("No orders found");
        return orders.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO create(OrderDTO orderDTO) {
        logger.info("CREATE: Started. User ID: {}", orderDTO.getUserId());

        CompletableFuture<KafkaMessage> response = kafkaRequestReply.sendRequest("", "amanzat.box-api.request.block-get-cost", "amanzat.order-api.response");
        logger.info("CREATE: Sent request to topic - amanzat.box-api.request.block-get-cost");

        KafkaMessage result;
        try {
            result = response.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaResponseFailedException("CREATE: BoxService. InterruptedException.");
        } catch (ExecutionException e) {
            throw new KafkaResponseFailedException("CREATE: BoxService. ExecutionException. Message: " + e.getCause().getMessage());
        }

        String resultBody = result.getBody();
        logger.info("CREATE: Received message: {}", resultBody);

        if (result.getRequestResult() == KafkaMessage.RequestResult.FAILED || resultBody.isEmpty()) {
            throw new KafkaResponseFailedException("CREATE: BoxService -> FAILED or empty. Body: " + resultBody);
        } else {
            double summ;
            try {
                summ = Double.parseDouble(result.getBody());
            } catch (NumberFormatException exception) {
                throw new KafkaResponseFailedException("CREATE: BoxService -> incorrect number format. Body: " + resultBody);
            }

            OrderModel order = convertToEntity(orderDTO);
            order.setId(UUID.randomUUID().toString());
            order.setSumm(summ * order.getRentalPeriod());
            order.setIsAlive(false);
            order.setIsPaid(false);
            order.setBookingDateTime(System.currentTimeMillis());

            OrderModel savedOrder = orderRepo.save(order);
            logger.info("CREATE: Order saved successfully with ID: {}", savedOrder.getId());

            return convertToDto(savedOrder);
        }
    }

    @Transactional
    public OrderDTO setPaid(String id) {
        logger.info("SETPAID: Setting 'isPaid' flag for order with ID: {}", id);
        OrderModel existingOrder = orderRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("setPaid. Order not found"));

        existingOrder.setIsPaid(true);
        OrderModel updatedOrder = orderRepo.save(existingOrder);
        return convertToDto(updatedOrder);
    }

    public OrderDTO extend(OrderDTO orderDTO) {
        logger.info("EXTEND: Extending order with ID: {}", orderDTO.getId());

        CompletableFuture<KafkaMessage> response = kafkaRequestReply.sendRequest("", "amanzat.box-api.get-cost", "amanzat.order-api.response");
        logger.info("EXTEND: Sent request to topic - amanzat.box-api.get-cost");

        OrderModel extendOrder = orderRepo.findById(orderDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        KafkaMessage result;
        try {
            result = response.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaResponseFailedException("EXTEND: BoxService. InterruptedException.");
        } catch (ExecutionException e) {
            throw new KafkaResponseFailedException("EXTEND: BoxService. ExecutionException. Message: " + e.getCause().getMessage());
        }

        String resultBody = result.getBody();
        logger.info("EXTEND: Received message: {}", resultBody);

        if (result.getRequestResult() == KafkaMessage.RequestResult.FAILED || resultBody.isEmpty()) {
            throw new KafkaResponseFailedException("EXTEND: BoxService -> FAILED or empty. Body: " + resultBody);
        } else {
            double summ;
            try {
                summ = Double.parseDouble(result.getBody());
            } catch (NumberFormatException exception) {
                throw new KafkaResponseFailedException("EXTEND: BoxService -> incorrect number format. Body: " + resultBody);
            }

            extendOrder.setSumm(extendOrder.getSumm() + summ * orderDTO.getRentalPeriod());
            extendOrder.setRentalPeriod(extendOrder.getRentalPeriod() + orderDTO.getRentalPeriod());
            logger.info("EXTEND: Order extension completed, updated amount: {}, updated rental period: {}", extendOrder.getSumm(), extendOrder.getRentalPeriod());
            orderRepo.save(extendOrder);
            return convertToDto(extendOrder);
        }
    }

    public void delete(String id) {
        logger.info("DELETE: Deleting order with ID: {}", id);

        OrderModel order = orderRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (Boolean.TRUE.equals(order.getIsPaid())) {
            logger.info("DELETE: Logical deletion of order with ID: {}", id);
            order.setIsAlive(false);
            orderRepo.save(order);
        } else {
            logger.info("DELETE: Physical deletion of order with ID: {}", id);
            orderRepo.deleteById(id);
        }
    }
}
