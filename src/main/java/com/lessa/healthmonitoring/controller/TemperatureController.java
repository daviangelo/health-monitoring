package com.lessa.healthmonitoring.controller;


import com.lessa.healthmonitoring.domain.TemperatureScale;
import com.lessa.healthmonitoring.dto.TemperatureRecordDto;
import com.lessa.healthmonitoring.service.TemperatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/temperature")
@RequiredArgsConstructor
public class TemperatureController {

    private final TemperatureService temperatureService;

    @PostMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TemperatureRecordDto> create(@PathVariable Long userId, @RequestBody TemperatureRecordDto temperatureRecordDto) {
        var temperatureCreated = TemperatureRecordDto.fromDomain(temperatureService.recordTemperature(userId, temperatureRecordDto.toDomain()));
        return ResponseEntity.ok(temperatureCreated);
    }

    @GetMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TemperatureRecordDto>> getTemperatureRecordsPerDay(@PathVariable Long userId, @RequestParam("date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam("scale") TemperatureScale scale) {
        var temperatureRecords = temperatureService.getTemperatureRecordsPerDay(userId, date, scale).stream()
                .map(TemperatureRecordDto::fromDomain).toList();

        if (!temperatureRecords.isEmpty()) {
            return ResponseEntity.ok(temperatureRecords);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/{temperatureRecordId}")
    public ResponseEntity<Void> delete(@PathVariable Long temperatureRecordId) {
        if (temperatureService.delete(temperatureRecordId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
