package com.lessa.healthmonitoring.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.lessa.healthmonitoring.HealthMonitoringApplication
import com.lessa.healthmonitoring.dto.ApiError
import com.lessa.healthmonitoring.dto.HeartRateRecordDto
import com.lessa.healthmonitoring.persistence.entity.HeartRateRecordEntity
import com.lessa.healthmonitoring.persistence.entity.UserEntity
import com.lessa.healthmonitoring.persistence.repository.HeartRateRepository
import com.lessa.healthmonitoring.persistence.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.spock.Testcontainers
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@SpringBootTest(classes = HealthMonitoringApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
class HeartRateControllerSpec extends IntegrationSpec {

    @Autowired
    private MockMvc mvc

    @Autowired
    private UserRepository userRepository

    @Autowired
    private HeartRateRepository heartRateRepository

    @Autowired
    ObjectMapper mapper

    void setup() {
        heartRateRepository.deleteAll()
        userRepository.deleteAll()
    }

    def "Should record a heart rate"() {

        setup:
        var userEntity = new UserEntity(null, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var userCreated = userRepository.save(userEntity)

        var heartRateRecordToCreate = new HeartRateRecordDto(null, Instant.parse("2023-10-22T18:00:00.00Z"), 80)

        when:
        var result = mvc.perform(
                post("/heart-rate/{userId}", userCreated.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(heartRateRecordToCreate))
        ).andReturn()

        var heartRateCreated = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), HeartRateRecordDto)
        heartRateRecordToCreate.setId(heartRateCreated.getId())


        then:
        result.getResponse().getStatus() == 200
        heartRateRecordToCreate == heartRateCreated

    }

    def "Should not create a heart rate record when the user is not found"() {

        setup:
        var heartRateRecordToCreate = new HeartRateRecordDto(null, Instant.parse("2023-10-22T18:00:00.00Z"), 80)
        var expectedApiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "User with id 1 not found to record a heart rate.", ["error occurred"])

        when:
        var result = mvc.perform(
                post("/heart-rate/{userId}", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(heartRateRecordToCreate))
        ).andReturn()

        var apiErrorReturned = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ApiError)


        then:
        result.getResponse().getStatus() == 500
        expectedApiError == apiErrorReturned

    }

    def "Should get heart rate records within a day"() {

        setup:
        var userEntity = new UserEntity(null, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        userEntity = userRepository.save(userEntity)

        var heartRateEntityToCreate = [
                new HeartRateRecordEntity(null, userEntity, Instant.parse("2023-10-22T18:01:00.00Z"), 80),
                new HeartRateRecordEntity(null, userEntity, Instant.parse("2023-10-22T18:02:00.00Z"),75)
        ]

        heartRateRepository.saveAll(heartRateEntityToCreate)

        when:
        var result = mvc.perform(
                get("/heart-rate/{userId}", userEntity.getId())
                        .param("date", "2023-10-22")
        ).andReturn()

        var heartRateReturned = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeReference<List<HeartRateRecordDto>>() {})

        var heartRateRecordsExpected = [
                new HeartRateRecordDto(null, Instant.parse("2023-10-22T18:01:00.00Z"), 80),
                new HeartRateRecordDto(null, Instant.parse("2023-10-22T18:02:00.00Z"), 75)
        ]

        clearHeartRateIds(heartRateReturned)

        then:
        result.getResponse().getStatus() == 200
        heartRateRecordsExpected == heartRateReturned

    }

    def "Should delete a heart rate record"() {

        setup:
        var userEntity = new UserEntity(null, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        userEntity = userRepository.save(userEntity)

        var heartRateRecordEntity = new HeartRateRecordEntity(1L, userEntity, Instant.parse("2023-10-22T18:01:00.00Z"), 80)
        heartRateRecordEntity = heartRateRepository.save(heartRateRecordEntity)

        when:
        var result = mvc.perform(
                delete("/heart-rate/{heartRateRecordId}", heartRateRecordEntity.getId())
        ).andReturn()

        var heartRateFoundAfterDeletion = heartRateRepository.findById(heartRateRecordEntity.getId())

        then:
        result.getResponse().getStatus() == 204
        Optional.empty() == heartRateFoundAfterDeletion

    }

    def "Should return 404 when try to delete an heart rate with nonexistent id "() {
        when:
        var result = mvc.perform(
                delete("/heart-rate/{heartRateRecordId}", 1l)
        ).andReturn()

        then:
        result.getResponse().getStatus() == 404
    }

    def clearHeartRateIds(List<HeartRateRecordDto> heartRate) {
        heartRate.forEach {heartRateRecord -> heartRateRecord.setId(null)}
    }
    
}
