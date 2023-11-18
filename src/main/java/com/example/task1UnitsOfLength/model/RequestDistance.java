package com.example.task1UnitsOfLength.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class RequestDistance {

    private Distance distance;

    @JsonAlias("convert_to")
    private String unitToConvertTo;
}
