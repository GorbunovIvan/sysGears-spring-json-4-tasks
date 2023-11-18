package com.example.task1UnitsOfLength.service;

import com.example.task1UnitsOfLength.model.Distance;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnitsService {

    // Key is the abbreviation of unit, value is conversion to mm
    private final Map<String, Double> units = new HashMap<>();

    private static final String fileName = "unitsOfLength.json";

    private final ObjectMapper objectMapper;

    protected void uploadUnits() {

        var resource = new ClassPathResource(fileName);

        try {
            Map<String, Double> unitsLoaded = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});

            units.clear();
            units.putAll(unitsLoaded);

            log.info("Units were uploaded");

            if (units.isEmpty()) {
                log.info("No units in file '" + fileName + "' were found");
            } else {
                log.info("Units were uploaded");
            }
        } catch (IOException e) {
            var error = "Unable to read file '" + fileName + "' from resources";
            log.error(error);
            throw new RuntimeException(error, e);
        }
    }

    public double convertTo(Distance distance, String unitToConvertTo) {

        if (units.isEmpty()) {
            uploadUnits();
            if (units.isEmpty()) {
                throw new RuntimeException("No units were found");
            }
        }

        var valueInMMFrom = units.get(distance.getUnit());
        var valueInMMTo   = units.get(unitToConvertTo);

        if (valueInMMFrom == null) {
            throw new RuntimeException("Unknown unit of distance - '" + distance.getUnit() + "'");
        }
        if (valueInMMTo == null) {
            throw new RuntimeException("Unknown unit to convert to - '" + unitToConvertTo + "'");
        }

        var conversionCoefficient = valueInMMFrom / valueInMMTo;

        var length = distance.getValue();

        return conversionCoefficient * length;
    }
}
