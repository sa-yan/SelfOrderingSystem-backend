package com.sayan.selforderingsystem.models;

public enum OrderStatus {
    PLACED, // Initial status when order is placed
    PREPARING, // Order is being prepared by the cook
    READY, // Order is ready for pickup
    DELIVERED // Order has been delivered to the customer
}
