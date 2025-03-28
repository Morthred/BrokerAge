package com.ing.controller;

import com.ing.dto.OrderDTO;
import com.ing.model.Order;
import com.ing.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity createOrder(@RequestBody Order order) {
        try {
            OrderDTO createdOrder = orderService.createOrder(order);

            ResponseEntity<OrderDTO> body = ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
            log.info("Order created: {}", body);
            return body;
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> listOrders(@RequestParam Long customerId, @RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        List<OrderDTO> orders = orderService.listOrders(customerId, startDate, endDate);
        log.info("Orders fetched: {}", orders);
        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            log.info("Order deleted: {}", orderId);
        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/assets")
    public ResponseEntity<List<String>> listAssets(@RequestParam Long customerId) {
        List<String> assets = orderService.listAssets(customerId);
        log.info("Assets fetched for customer {}: {}", customerId, assets);
        return ResponseEntity.ok(assets);
    }
}