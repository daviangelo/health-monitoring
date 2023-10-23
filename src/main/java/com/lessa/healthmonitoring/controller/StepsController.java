package com.lessa.healthmonitoring.controller;

import com.lessa.healthmonitoring.dto.StepsRecordDto;
import com.lessa.healthmonitoring.service.StepsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/steps")
@RequiredArgsConstructor
public class StepsController {

    private final StepsService stepsService;

    @PostMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StepsRecordDto> create(@PathVariable Long userId, @RequestBody StepsRecordDto stepsRecordDto) {
        var stepsCreated = StepsRecordDto.fromDomain(stepsService.recordSteps(userId, stepsRecordDto.toDomain()));
        return ResponseEntity.ok(stepsCreated);
    }

    @GetMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StepsRecordDto>> getStepsRecordsPerDay(@PathVariable Long userId, @RequestParam("date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var stepsRecords = stepsService.getStepsRecordsPerDay(userId, date).stream()
                .map(StepsRecordDto::fromDomain).toList();

        if (!stepsRecords.isEmpty()) {
            return ResponseEntity.ok(stepsRecords);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/sum/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> getNumberOfStepsPerDay(@PathVariable Long userId, @RequestParam("date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(stepsService.getNumberOfStepsPerDay(userId, date));
    }

    @DeleteMapping(path = "/{stepsRecordId}")
    public ResponseEntity<Void> delete(@PathVariable Long stepsRecordId) {
        if (stepsService.delete(stepsRecordId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
