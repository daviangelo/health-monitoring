package com.lessa.healthmonitoring.controller;


import com.lessa.healthmonitoring.dto.StepsRecordDto;
import com.lessa.healthmonitoring.service.StepsService;
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
@RequestMapping("/steps")
@Tag(name = "Steps API", description = "API to record, retrieve and delete steps data")
@RequiredArgsConstructor
public class StepsController {

    private final StepsService stepsService;

    @Operation( summary = "Record steps data",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Steps to be recorded")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Steps recorded",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = StepsRecordDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid steps and/or user supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "User supplied not found", content = @Content) })
    @PostMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StepsRecordDto> create(@Parameter( description = "User id to be associated with the record")
                                                     @PathVariable Long userId, @RequestBody StepsRecordDto stepsRecordDto) {
        var stepsCreated = StepsRecordDto.fromDomain(stepsService.recordSteps(userId, stepsRecordDto.toDomain()));
        return ResponseEntity.ok(stepsCreated);
    }


    @Operation(summary = "Get steps records within a day by user id and date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Steps records founded", useReturnTypeSchema = true,
                    content = { @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid user id supplied and/or date", content = @Content),
            @ApiResponse(responseCode = "404", description = "Steps records not found with supplied user id and date",
                    content = @Content) })
    @GetMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StepsRecordDto>> getStepsRecordsPerDay(
            @Parameter( description = "User id associated with the record") @PathVariable Long userId,
            @Parameter( description = "Date (yyyy-MM-dd) to return records. Will be returned all the records in the select day between 00:00:00 - 23:59:59") @RequestParam("date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var stepsRecords = stepsService.getStepsRecordsPerDay(userId, date).stream()
                .map(StepsRecordDto::fromDomain).toList();

        if (!stepsRecords.isEmpty()) {
            return ResponseEntity.ok(stepsRecords);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get the sum of steps records within a day by user id and date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Steps records sum", useReturnTypeSchema = true, content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid user id supplied and/or date", content = @Content)})
    @GetMapping(path = "/sum/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> getNumberOfStepsPerDay(
            @Parameter( description = "User id associated with the record") @PathVariable Long userId,
            @Parameter( description = "Date (yyyy-MM-dd) to return records. Will be returned all the records in the select day between 00:00:00 - 23:59:59") @RequestParam("date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(stepsService.getNumberOfStepsPerDay(userId, date));
    }

    @Operation(summary = "Delete steps record by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Steps record deleted", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Steps record not found with supplied id", content = @Content) })
    @DeleteMapping(path = "/{stepsRecordId}")
    public ResponseEntity<Void> delete(@Parameter(description = "id of steps record to be deleted") @PathVariable Long stepsRecordId) {
        if (stepsService.delete(stepsRecordId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
