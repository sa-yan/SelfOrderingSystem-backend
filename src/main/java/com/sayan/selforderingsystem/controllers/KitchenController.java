package com.sayan.selforderingsystem.controllers;

import com.sayan.selforderingsystem.models.Order;
import com.sayan.selforderingsystem.services.KitchenService;
import com.sayan.selforderingsystem.services.OrderService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kitchen")
@Data
@AllArgsConstructor
//@CrossOrigin(origins = "*")
public class KitchenController {
    private final KitchenService kitchenService;

    @GetMapping("/orders")
    public ResponseEntity<?> getOrders() {
        return ResponseEntity.ok(kitchenService.getActiveOrders());
    }

    @PatchMapping("/order/{id}/prepare")
    public ResponseEntity<?> prepareOrder(@PathVariable String id) {
        Order order = kitchenService.startPreparingOrder(id);
        if(order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/orders/{id}/ready")
    public ResponseEntity<?> readyOrder1(@PathVariable String id) {
        Order order = kitchenService.readyOrder(id);
        if(order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/orders/{id}/deliver")
    public ResponseEntity<?> deliverOrder(@PathVariable String id) {
        Order order = kitchenService.deliveredOrder(id);
        if(order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }
}
