package com.example.task1UnitsOfLength.controller;

import com.example.task1UnitsOfLength.model.RequestDistance;
import com.example.task1UnitsOfLength.service.ConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;

@RestController
@RequestMapping("/api/v1/converter")
@RequiredArgsConstructor
public class ConverterController {

    private final ConverterService converterService;

    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    /**
     * Converts length from one unit to another
     * @param requestDistance - json like {
     *   "distance":
     *     {
     *       "unit": "m",
     *       "value": 0.5
     *     },
     *   "convert_to": "ft"
     * }
     * @return value after conversion
     */
    @PostMapping
    public ResponseEntity<?> convert(@RequestBody RequestDistance requestDistance) {
        var result = converterService.convert(requestDistance);
        var resultFormatted = decimalFormat.format(result);
        return ResponseEntity.ok(resultFormatted);
    }
}
