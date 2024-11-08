package org.example.controller;

import jakarta.validation.Valid;
import org.example.persistence.model.dto.OrderDTO;
import org.example.persistence.model.Transfer;
import org.example.persistence.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@Validated
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/user/{userId}")
    public OrderDTO getOrderByUserId(@PathVariable String userId) {
        return orderService.getByUserId(userId);
    }

    @GetMapping("/box/{boxId}")
    public OrderDTO getOrderByBoxId(@PathVariable String boxId) {
        return orderService.getByBoxId(boxId);
    }

    @GetMapping
    public List<OrderDTO> getAllOrders() {
        return orderService.findAll();
    }

    @PostMapping
    public OrderDTO createOrder(@RequestBody @Validated(Transfer.Create.class) OrderDTO order) {
        return orderService.create(order);
    }

    @PutMapping("/extend")
    public OrderDTO extendOrder(@RequestBody @Validated(Transfer.Extend.class) OrderDTO order) {
        return orderService.extend(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable String id) {
        orderService.delete(id);
        return ResponseEntity.ok("Successfully deleted");
    }
}
