package com.sayan.selforderingsystem.services;

import com.sayan.selforderingsystem.dto.ErrorDto;
import com.sayan.selforderingsystem.models.MenuItem;
import com.sayan.selforderingsystem.models.Order;
import com.sayan.selforderingsystem.models.OrderItem;
import com.sayan.selforderingsystem.models.OrderStatus;
import com.sayan.selforderingsystem.repositories.MenuItemRepository;
import com.sayan.selforderingsystem.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    public Order createOrder(Order order) {
        List<OrderItem> allItems = new ArrayList<>();
        double totalAmount = 0;
        for(OrderItem item : order.getItems()){
            Optional<MenuItem> items = menuItemRepository.findById(item.getMenuItemId());
            if(items.isPresent()){
                MenuItem menuItem = items.get();

                double itemTotal = menuItem.getPrice() * item.getQuantity();
                totalAmount += itemTotal;

                OrderItem orderItem = new OrderItem(
                        menuItem.getId(),
                        menuItem.getName(),
                        menuItem.getPrice(),
                        item.getQuantity()
                );
                allItems.add(orderItem);
            }else{
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Menu item with ID " + item.getMenuItemId() + " not found"
                );            }
        }
        order.setItems(allItems);
        order.setTotalAmount(totalAmount);

        Order lastOrder = orderRepository.findTopByOrderByOrderNumberDesc();
        int nextOrderNumber = (lastOrder != null) ? lastOrder.getOrderNumber() + 1 : 1000;
        order.setOrderNumber(nextOrderNumber);

        order.setOrderStatus(OrderStatus.PLACED);
        order.setOrderDate(LocalDateTime.now());
        order.setTableNumber(order.getTableNumber());
        return orderRepository.save(order);
    }


    public Order getOrderById(String id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        return orderOptional.orElse(null);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByStatusList(List<OrderStatus> statuses) {
        return orderRepository.findByOrderStatusIn(statuses);
    }

    public Order updateOrderStatus(String id, OrderStatus status) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if(orderOptional.isPresent()){
            Order order = orderOptional.get();
            order.setOrderStatus(status);
            return orderRepository.save(order);
        }
        return null;
    }
}
