package com.growthmul.app.lawnmover_fs.entity;

import lombok.Data;

import java.util.List;

@Data
public class PricingPlan {
    private String name;
    private String subtitle;
    private int price;
    private boolean featured;
    private List<String> features;
}