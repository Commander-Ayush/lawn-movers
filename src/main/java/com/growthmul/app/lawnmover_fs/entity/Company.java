package com.growthmul.app.lawnmover_fs.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String city;
    private Integer foundedYear;
    private Integer yardsServed;
    private Integer yearsExperience;
    private Long phone;
    private String email;
}
