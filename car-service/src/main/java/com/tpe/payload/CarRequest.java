package com.tpe.payload;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarRequest {

    @NotNull
    private String brand;

    @NotNull
    private String model;

    @NotNull
    private Integer doors;

    @NotNull
    private Double pricePerDay;

    @NotNull
    private Integer age;

    @NotNull
    //regex
    private String numberPlate;

}
