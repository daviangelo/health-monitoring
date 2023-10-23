package com.lessa.healthmonitoring.controller;

import com.lessa.healthmonitoring.dto.HeartRateRecordDto;
import com.lessa.healthmonitoring.service.HeartRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/heart-rate")
@RequiredArgsConstructor
public class HeartRateController {

    private final HeartRateService heartRateService;

    @PostMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HeartRateRecordDto> create(@PathVariable Long userId, @RequestBody HeartRateRecordDto heartRateRecordDto) {
        var heartRateCreated = HeartRateRecordDto.fromDomain(heartRateService.recordHeartRate(userId, heartRateRecordDto.toDomain()));
        return ResponseEntity.ok(heartRateCreated);
    }

    @GetMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HeartRateRecordDto>> getHeartRateRecordsPerDay(@PathVariable Long userId, @RequestParam("date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var heartRateRecords = heartRateService.getHeartRateRecordsPerDay(userId, date).stream()
                .map(HeartRateRecordDto::fromDomain).toList();

        if (!heartRateRecords.isEmpty()) {
            return ResponseEntity.ok(heartRateRecords);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/{heartRateRecordId}")
    public ResponseEntity<Void> delete(@PathVariable Long heartRateRecordId) {
        if (heartRateService.delete(heartRateRecordId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}