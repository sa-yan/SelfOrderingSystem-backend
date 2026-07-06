package com.sayan.selforderingsystem.controllers;

import com.sayan.selforderingsystem.dto.ErrorDto;
import com.sayan.selforderingsystem.models.Order;
import com.sayan.selforderingsystem.models.OrderStatus;
import com.sayan.selforderingsystem.services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order/")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        Order createdOrder = orderService.createOrder(order);
        if(createdOrder != null){
            return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getOrder(@PathVariable String id) {
        Order order = orderService.getOrderById(id);
        if(order != null){
            return new ResponseEntity<>(order, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ErrorDto("Order with this corresponding ID is not available!", 404),HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
    }

    @GetMapping("status")
    public ResponseEntity<List<Order>> getOrdersByStatus(@RequestParam List<OrderStatus> statuses) {
        return ResponseEntity.ok(orderService.getOrdersByStatusList(statuses));
    }

    @PatchMapping("{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable String id,
                                               @RequestBody OrderStatus status) {
        Order updatedOrder = orderService.updateOrderStatus(id, status);
        if(updatedOrder!=null){
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }
}
