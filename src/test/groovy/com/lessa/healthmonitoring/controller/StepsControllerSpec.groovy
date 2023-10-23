package com.lessa.healthmonitoring.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.lessa.healthmonitoring.HealthMonitoringApplication
import com.lessa.healthmonitoring.dto.ApiError
import com.lessa.healthmonitoring.dto.StepsRecordDto
import com.lessa.healthmonitoring.persistence.entity.StepsRecordEntity
import com.lessa.healthmonitoring.persistence.entity.UserEntity
import com.lessa.healthmonitoring.persistence.repository.StepsRepository
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
class StepsControllerSpec extends Specification {

    @Autowired
    private MockMvc mvc

    @Autowired
    private UserRepository userRepository

    @Autowired
    private StepsRepository stepsRepository

    @Autowired
    ObjectMapper mapper

    void setup() {
        stepsRepository.deleteAll()
        userRepository.deleteAll()
    }

    def "Should record a steps"() {

        setup:
        var userEntity = new UserEntity(null, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var userCreated = userRepository.save(userEntity)

        var stepsRecordToCreate = new StepsRecordDto(null, Instant.parse("2023-10-22T18:00:00.00Z"), 8000l)

        when:
        var result = mvc.perform(
                post("/steps/{userId}", userCreated.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(stepsRecordToCreate))
        ).andReturn()

        var stepsCreated = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), StepsRecordDto)
        stepsRecordToCreate.setId(stepsCreated.getId())


        then:
        result.getResponse().getStatus() == 200
        stepsRecordToCreate == stepsCreated

    }

    def "Should not create a steps record when the user is not found"() {

        setup:
        var stepsRecordToCreate = new StepsRecordDto(null, Instant.parse("2023-10-22T18:00:00.00Z"), 8000l)
        var expectedApiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "User with id 1 not found to record steps.", ["error occurred"])

        when:
        var result = mvc.perform(
                post("/steps/{userId}", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(stepsRecordToCreate))
        ).andReturn()

        var apiErrorReturned = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ApiError)


        then:
        result.getResponse().getStatus() == 500
        expectedApiError == apiErrorReturned

    }

    def "Should get steps records within a day"() {

        setup:
        var userEntity = new UserEntity(null, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        userEntity = userRepository.save(userEntity)

        var stepsEntityToCreate = [
                new StepsRecordEntity(null, userEntity, Instant.parse("2023-10-22T18:01:00.00Z"), 8000l),
                new StepsRecordEntity(null, userEntity, Instant.parse("2023-10-22T18:02:00.00Z"),2000l)
        ]

        stepsRepository.saveAll(stepsEntityToCreate)

        when:
        var result = mvc.perform(
                get("/steps/{userId}", userEntity.getId())
                        .param("date", "2023-10-22")
        ).andReturn()

        var stepsReturned = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeReference<List<StepsRecordDto>>() {})

        var stepsRecordsExpected = [
                new StepsRecordDto(null, Instant.parse("2023-10-22T18:01:00.00Z"), 8000l),
                new StepsRecordDto(null, Instant.parse("2023-10-22T18:02:00.00Z"), 2000l)
        ]

        clearStepsIds(stepsReturned)

        then:
        result.getResponse().getStatus() == 200
        stepsRecordsExpected == stepsReturned

    }

    def "Should get steps records sum within a day"() {

        setup:
        var userEntity = new UserEntity(null, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        userEntity = userRepository.save(userEntity)

        var stepsEntityToCreate = [
                new StepsRecordEntity(null, userEntity, Instant.parse("2023-10-22T18:01:00.00Z"), 8000l),
                new StepsRecordEntity(null, userEntity, Instant.parse("2023-10-22T18:02:00.00Z"),2000l)
        ]

        stepsRepository.saveAll(stepsEntityToCreate)

        when:
        var result = mvc.perform(
                get("/steps/sum/{userId}", userEntity.getId())
                        .param("date", "2023-10-22")
        ).andReturn()

        var stepsReturned = result.getResponse().getContentAsString(StandardCharsets.UTF_8)

        then:
        result.getResponse().getStatus() == 200
        stepsReturned == "10000"

    }

    def "Should delete a steps record"() {

        setup:
        var userEntity = new UserEntity(null, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        userEntity = userRepository.save(userEntity)

        var stepsRecordEntity = new StepsRecordEntity(1L, userEntity, Instant.parse("2023-10-22T18:01:00.00Z"), 8000l)
        stepsRecordEntity = stepsRepository.save(stepsRecordEntity)

        when:
        var result = mvc.perform(
                delete("/steps/{stepsRecordId}", stepsRecordEntity.getId())
        ).andReturn()

        var stepsFoundAfterDeletion = stepsRepository.findById(stepsRecordEntity.getId())

        then:
        result.getResponse().getStatus() == 204
        Optional.empty() == stepsFoundAfterDeletion

    }

    def "Should return 404 when try to delete steps with nonexistent id "() {
        when:
        var result = mvc.perform(
                delete("/steps/{stepsRecordId}", 1l)
        ).andReturn()

        then:
        result.getResponse().getStatus() == 404
    }

    def clearStepsIds(List<StepsRecordDto> steps) {
        steps.forEach {stepsRecord -> stepsRecord.setId(null)}
    }
}
