package com.merc.connected.cars.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class PersonModel {

    private int id;

    @NotBlank
    @Size(min = 5, max = 50)
    @ApiModelProperty(example = "Hello")
    private String name;

    @Past
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dob;

    @NotNull
    @DecimalMin("0.1")
    @ApiModelProperty(example = "122111241.150")
    private double salary;

    @NotNull
    @DecimalMax("100") @DecimalMin("1")
    @ApiModelProperty(example = "20")
    private int age;
}
