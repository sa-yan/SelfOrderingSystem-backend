package com.sayan.selforderingsystem.repositories;

import com.sayan.selforderingsystem.models.Order;
import com.sayan.selforderingsystem.models.OrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByOrderStatus(OrderStatus orderStatus);
    Order findTopByOrderByOrderNumberDesc();
    List<Order> findByOrderStatusIn(List<OrderStatus> statuses);
}
