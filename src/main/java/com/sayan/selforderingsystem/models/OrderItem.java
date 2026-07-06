package com.sayan.selforderingsystem.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class OrderItem {
    private String menuItemId;
    private String name;
    private double price;
    private int quantity;
}
