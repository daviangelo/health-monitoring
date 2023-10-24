package com.lessa.healthmonitoring.controller;


import com.lessa.healthmonitoring.domain.TemperatureScale;
import com.lessa.healthmonitoring.dto.TemperatureRecordDto;
import com.lessa.healthmonitoring.service.TemperatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/temperature")
@RequiredArgsConstructor
@Tag(name = "Temperature API", description = "API to record, retrieve and delete temperature data")
public class TemperatureController {

    private final TemperatureService temperatureService;
    @Operation( summary = "Record temperature data",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Temperature to be recorded")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Temperature recorded",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = TemperatureRecordDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid temperature and/or user supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "User supplied not found", content = @Content) })
    @PostMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TemperatureRecordDto> create(@Parameter( description = "User id to be associated with the record") @PathVariable Long userId, 
                                                       @RequestBody TemperatureRecordDto temperatureRecordDto) {
        var temperatureCreated = TemperatureRecordDto.fromDomain(temperatureService.recordTemperature(userId, temperatureRecordDto.toDomain()));
        return ResponseEntity.ok(temperatureCreated);
    }

    @Operation(summary = "Get temperature records within a day by user id and date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Temperature records founded", useReturnTypeSchema = true,
                    content = { @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid user id / date / scale supplied ", content = @Content),
            @ApiResponse(responseCode = "404", description = "Temperature records not found with supplied user id and date",
                    content = @Content) })
    @GetMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TemperatureRecordDto>> getTemperatureRecordsPerDay(
            @Parameter( description = "User id associated with the record") @PathVariable Long userId,
            @Parameter( description = "Date (yyyy-MM-dd) to return records. Will be returned all the records in the select day between 00:00:00 - 23:59:59") @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter( description = "Scale to return the temperature records") @RequestParam("scale") TemperatureScale scale) {
        var temperatureRecords = temperatureService.getTemperatureRecordsPerDay(userId, date, scale).stream()
                .map(TemperatureRecordDto::fromDomain).toList();

        if (!temperatureRecords.isEmpty()) {
            return ResponseEntity.ok(temperatureRecords);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete temperature record by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Temperature record deleted", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Temperature record not found with supplied id", content = @Content) })
    @DeleteMapping(path = "/{temperatureRecordId}")
    public ResponseEntity<Void> delete(@Parameter(description = "id of temperature record to be deleted") @PathVariable Long temperatureRecordId) {
        if (temperatureService.delete(temperatureRecordId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
