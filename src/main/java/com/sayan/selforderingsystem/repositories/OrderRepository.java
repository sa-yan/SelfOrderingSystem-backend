package com.sayan.selforderingsystem.repositories;

import com.sayan.selforderingsystem.models.Order;
import com.sayan.selforderingsystem.models.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByOrderStatus(OrderStatus orderStatus);
    Order findTopByOrderByOrderNumberDesc();
    List<Order> findByOrderStatusIn(List<OrderStatus> statuses);
}
