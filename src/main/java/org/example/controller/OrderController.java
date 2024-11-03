package org.example.controller;


import org.example.entity.model.OrderModel;
import org.example.entity.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/user/{userId}")
    public OrderModel getOrderByUserId(@PathVariable String userId) {
        return orderService.getByUserId(userId);
    }

    @GetMapping("/box/{boxId}")
    public OrderModel getOrderByBoxId(@PathVariable String boxId) {
        return orderService.getByBoxId(boxId);
    }

    @GetMapping
    public List<OrderModel> getAllOrders() {
        return orderService.findAll();
    }

    @PostMapping
        public OrderModel createOrder(@RequestBody OrderModel order, @RequestParam String credential) {
        return orderService.create(order, credential);
    }

    @PutMapping("/extend")
    public OrderModel extendOrder(@RequestBody OrderModel order) {
        return orderService.extend(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable String id) {
        orderService.delete(id);
        return ResponseEntity.ok("Successfully deleted");
    }
}
