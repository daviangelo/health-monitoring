package com.lessa.healthmonitoring.controller;

import com.lessa.healthmonitoring.dto.HeartRateRecordDto;
import com.lessa.healthmonitoring.service.HeartRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Heart Rate API", description = "API to record, retrieve and delete heart rate data")
public class HeartRateController {

    private final HeartRateService heartRateService;

    @Operation( summary = "Record a heart rate measurement",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Heart rate to be recorded")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Heart rate recorded",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = HeartRateRecordDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid heart rate and/or user supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "User supplied not found", content = @Content) })
    @PostMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HeartRateRecordDto> create(@PathVariable Long userId, @RequestBody HeartRateRecordDto heartRateRecordDto) {
        var heartRateCreated = HeartRateRecordDto.fromDomain(heartRateService.recordHeartRate(userId, heartRateRecordDto.toDomain()));
        return ResponseEntity.ok(heartRateCreated);
    }


    @Operation(summary = "Get heart rate records within a day by user id and date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "heart rate records founded", useReturnTypeSchema = true,
                    content = { @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid user id supplied and/or date", content = @Content),
            @ApiResponse(responseCode = "404", description = "Heart rate records not found with supplied user id and date",
                    content = @Content) })
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

    @Operation(summary = "Delete an heart rate record by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Heart rate record deleted", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Heart rate record not found with supplied id", content = @Content) })
    @DeleteMapping(path = "/{heartRateRecordId}")
    public ResponseEntity<Void> delete(@PathVariable Long heartRateRecordId) {
        if (heartRateService.delete(heartRateRecordId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}