package com.tpe.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name="t_car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false)
    private String brand;

    @Column(length = 30, nullable = false)
    private String model;

    @Column(nullable = false)
    private Integer doors;

    @Column(nullable = false)
    private Double pricePerHour;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, unique = true)
    private String numberPlate;

    @ManyToMany
    @JoinTable(
            name = "car_image",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private Set<ImageFile> images = new HashSet<>();

    @Column(nullable = false)
    private boolean isAvailable;
    //TODO:rezervasyon olustururken bu field i setle.

}
