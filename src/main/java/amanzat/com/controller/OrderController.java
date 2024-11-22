package amanzat.com.controller;

import amanzat.com.annotations.RoleRequired;
import amanzat.com.persistence.model.dto.OrderDTO;
import amanzat.com.persistence.service.OrderService;
import amanzat.com.exception.Transfer;
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


    @RoleRequired({"USER"})
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

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable String id) {
        orderService.delete(id);
        return ResponseEntity.ok("Successfully deleted");
    }
    @PatchMapping("/{id}/rentalPeriod")
    public ResponseEntity<OrderDTO> setPaid(@PathVariable String id, @RequestParam int rentalPeriod) {
        OrderDTO updatedOrder = orderService.extend(id, rentalPeriod);
        return ResponseEntity.ok(updatedOrder);
    }
}
