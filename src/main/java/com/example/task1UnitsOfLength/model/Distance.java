package com.example.task1UnitsOfLength.model;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class Distance {
    private String unit;
    private double value;
}
