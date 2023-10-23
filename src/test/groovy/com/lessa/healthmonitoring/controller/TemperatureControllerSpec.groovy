package com.lessa.healthmonitoring.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.lessa.healthmonitoring.HealthMonitoringApplication
import com.lessa.healthmonitoring.domain.TemperatureScale
import com.lessa.healthmonitoring.dto.ApiError
import com.lessa.healthmonitoring.dto.TemperatureRecordDto
import com.lessa.healthmonitoring.persistence.entity.TemperatureRecordEntity
import com.lessa.healthmonitoring.persistence.entity.UserEntity
import com.lessa.healthmonitoring.persistence.repository.TemperatureRepository
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete

@SpringBootTest(classes = HealthMonitoringApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
class TemperatureControllerSpec extends IntegrationSpec {

    @Autowired
    private MockMvc mvc

    @Autowired
    private UserRepository userRepository

    @Autowired
    private TemperatureRepository temperatureRepository

    @Autowired
    ObjectMapper mapper

    void setup() {
        temperatureRepository.deleteAll()
        userRepository.deleteAll()
    }

    def "Should record a temperature"() {

        setup:
        var userEntity = new UserEntity(null, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var userCreated = userRepository.save(userEntity)

        var temperatureRecordToCreate = new TemperatureRecordDto(null, Instant.parse("2023-10-22T18:00:00.00Z"), 37.0d, TemperatureScale.CELSIUS)

        when:
        var result = mvc.perform(
                post("/temperature/{userId}", userCreated.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(temperatureRecordToCreate))
        ).andReturn()

        var temperatureCreated = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), TemperatureRecordDto)
        temperatureRecordToCreate.setId(temperatureCreated.getId())


        then:
        result.getResponse().getStatus() == 200
        temperatureRecordToCreate == temperatureCreated

    }

    def "Should not create a temperature record when the user is not found"() {

        setup:
        var temperatureRecordToCreate = new TemperatureRecordDto(null, Instant.parse("2023-10-22T18:00:00.00Z"), 37.0d, TemperatureScale.CELSIUS)
        var expectedApiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "User with id 1 not found to record a temperature.", ["error occurred"])

        when:
        var result = mvc.perform(
                post("/temperature/{userId}", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(temperatureRecordToCreate))
        ).andReturn()

        var apiErrorReturned = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ApiError)


        then:
        result.getResponse().getStatus() == 500
        expectedApiError == apiErrorReturned

    }

    def "Should get temperature records within a day and return with the correct scale"() {

        setup:
        var userEntity = new UserEntity(null, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        userEntity = userRepository.save(userEntity)

        var temperaturesEntityToCreate = [
                new TemperatureRecordEntity(null, userEntity, Instant.parse("2023-10-22T18:01:00.00Z"), 36.0d, TemperatureScale.CELSIUS),
                new TemperatureRecordEntity(null, userEntity, Instant.parse("2023-10-22T18:02:00.00Z"), 97.7d, TemperatureScale.FAHRENHEIT),
                new TemperatureRecordEntity(null, userEntity, Instant.parse("2023-10-22T18:03:00.00Z"), 310.15d, TemperatureScale.KELVIN)
        ]

        temperatureRepository.saveAll(temperaturesEntityToCreate)

        when:
        var result = mvc.perform(
                get("/temperature/{userId}", userEntity.getId())
                        .param("scale", temperatureScale.name().toLowerCase())
                        .param("date", "2023-10-22")
        ).andReturn()

        var temperatureReturned = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeReference<List<TemperatureRecordDto>>() {})

        var temperaturesRecordsExpected = [
                new TemperatureRecordDto(null, Instant.parse("2023-10-22T18:01:00.00Z"), equivalentThirtSixDegreesCelsius, temperatureScale),
                new TemperatureRecordDto(null, Instant.parse("2023-10-22T18:02:00.00Z"), equivalentThirtSixAndHalfDegreesCelsius, temperatureScale),
                new TemperatureRecordDto(null, Instant.parse("2023-10-22T18:03:00.00Z"), equivalentThirtSevenDegreesCelsius, temperatureScale)
        ]

        clearTemperatureIds(temperatureReturned)

        then:
        result.getResponse().getStatus() == 200
        temperaturesRecordsExpected == temperatureReturned

        where:
        temperatureScale            | equivalentThirtSixDegreesCelsius | equivalentThirtSixAndHalfDegreesCelsius | equivalentThirtSevenDegreesCelsius
        TemperatureScale.CELSIUS    | 36.0d                            | 36.5d                                   | 37.0d
        TemperatureScale.FAHRENHEIT | 96.8d                            | 97.7d                                   | 98.6d
        TemperatureScale.KELVIN     | 309.15d                          | 309.65d                                 | 310.15d

    }

    def "Should delete a temperature record"() {

        setup:
        var userEntity = new UserEntity(null, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        userEntity = userRepository.save(userEntity)

        var temperatureRecordEntity = new TemperatureRecordEntity(1L, userEntity, Instant.parse("2023-10-22T18:01:00.00Z"), 36.0d, TemperatureScale.CELSIUS)
        temperatureRecordEntity = temperatureRepository.save(temperatureRecordEntity)

        when:
        var result = mvc.perform(
                delete("/temperature/{temperatureRecordId}", temperatureRecordEntity.getId())
        ).andReturn()

        var temperaturesFoundAfterDeletion = temperatureRepository.findById(temperatureRecordEntity.getId())

        then:
        result.getResponse().getStatus() == 204
        Optional.empty() == temperaturesFoundAfterDeletion

    }

    def 'Should return 404 when try to delete an temperature with nonexistent id'() {
        when:
        var result = mvc.perform(
                delete("/temperature/{temperatureRecordId}", 1l)
        ).andReturn()

        then:
        result.getResponse().getStatus() == 404
    }

    def clearTemperatureIds(List<TemperatureRecordDto> temperatures) {
        temperatures.forEach {temperature -> temperature.setId(null)}
    }

}
