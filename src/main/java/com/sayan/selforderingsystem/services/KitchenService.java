package com.sayan.selforderingsystem.services;

import com.sayan.selforderingsystem.models.Order;
import com.sayan.selforderingsystem.models.OrderStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class KitchenService {
    private final OrderService orderService;

    public List<Order> getActiveOrders(){
        return orderService.getOrdersByStatusList(
                Arrays.asList(OrderStatus.PLACED, OrderStatus.PREPARING, OrderStatus.READY, OrderStatus.DELIVERED)
        );
    }


    public Order startPreparingOrder(String id){
        return orderService.updateOrderStatus(id, OrderStatus.PREPARING);
    }

    public Order readyOrder(String id){
        return orderService.updateOrderStatus(id, OrderStatus.READY);
    }

    public Order deliveredOrder(String id){
        return orderService.updateOrderStatus(id, OrderStatus.DELIVERED);
    }
}
