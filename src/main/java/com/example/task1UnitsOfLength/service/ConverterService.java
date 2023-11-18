package com.example.task1UnitsOfLength.service;

import com.example.task1UnitsOfLength.model.RequestDistance;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConverterService {

    private final UnitsService unitsService;

    public double convert(@Nonnull RequestDistance requestDistance) {

        var distance = requestDistance.getDistance();
        var unitToConvertTo = requestDistance.getUnitToConvertTo();

        return unitsService.convertTo(distance, unitToConvertTo);
    }
}
