package com.sayan.selforderingsystem.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private int orderNumber;
    private List<OrderItem> items;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private double totalAmount;
    int tableNumber;

    private boolean paymentSuccessful;
    private String razorpayPaymentId;

    private String email;

}
