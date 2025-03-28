package com.ing.dto;

import com.ing.model.OrderSideType;
import com.ing.model.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class OrderDTO {
    private Long id;
    private String assetName;
    private OrderSideType orderSideType;
    private int size;
    private double price;
    private OrderStatus orderStatus;
    private LocalDateTime createDate;
}