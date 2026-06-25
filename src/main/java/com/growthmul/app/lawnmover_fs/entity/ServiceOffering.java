package com.growthmul.app.lawnmover_fs.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_offerings")
@Data
public class ServiceOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String icon;
    private String name;

    @Column(length = 500)
    private String description;

    private String price;
    private String type; // "service", "plan", "addon"
    private boolean featured;
    private int sortOrder;

    @ElementCollection
    @CollectionTable(name = "plan_features",
            joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "feature")
    private List<String> features = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}