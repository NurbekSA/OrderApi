//package org.example.persistence.service;
//
//import org.example.kafka.KafkaRequestReply;
//import org.example.persistence.model.dto.Status;
//import org.example.persistence.model.entity.OrderModel;
//import org.example.persistence.repository.OrderRepo;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class OrderSchedulerService {
//
//    private static final Logger logger = LoggerFactory.getLogger(OrderSchedulerService.class);
//
//    private final OrderRepo orderRepository;
//    private final KafkaRequestReply kafkaRequestReply;
//
//    public OrderSchedulerService(OrderRepo orderRepository, KafkaRequestReply kafkaRequestReply) {
//        this.orderRepository = orderRepository;
//        this.kafkaRequestReply = kafkaRequestReply;
//    }
//
//    @Scheduled(cron = "0 0 0 * * *")
//    public void checkOrdersToExpired() {
//        processOrders(
//                orderRepository.findExpiredOrders(System.currentTimeMillis()),
//                Status.EXPIRED,
//                "CHECKORDERS"
//        );
//    }
//
//    @Scheduled(cron = "0 0 0 * * *")
//    public void checkOrdersToUnpaid() {
//        processOrders(
//                orderRepository.findCreatedOrders(System.currentTimeMillis()),
//                Status.CANCELLED,
//                "CHECKORDERSTOUNPAID"
//        );
//    }
//
//    private void processOrders(List<OrderModel> orders, Status status, String logPrefix) {
//        if (orders.isEmpty()) {
//            logger.info("{}. No orders found for processing.", logPrefix);
//            return;
//        }
//
//        logger.info("{}. Found {} orders. Processing...", logPrefix, orders.size());
//
//        orders.forEach(order -> {
//            order.setStatus(status.getValue());
//            orderRepository.save(order);
//            logger.info("{}. Order with ID {} marked as {}", logPrefix, order.getId(), status);
//
//            kafkaRequestReply.sendRequest(order.getBoxId(), "amanzat.box-api.Unblock", "amanzat.order-api.response");
//            logger.info("{}. Sent message to amanzat.box-api.Unblock for Order ID {}", logPrefix, order.getId());
//        });
//
//        logger.info("{}. Completed processing orders.", logPrefix);
//    }
//}
