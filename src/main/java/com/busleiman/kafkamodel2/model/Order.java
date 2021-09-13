package com.busleiman.kafkamodel2.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class Order {

    private String orderId;
    private String customerId;
    private String supplierId;
    private String firstName;
    private String lastName;
    private int items;
    private float price;
    private float weight;
    private boolean automatedEmail;
}
