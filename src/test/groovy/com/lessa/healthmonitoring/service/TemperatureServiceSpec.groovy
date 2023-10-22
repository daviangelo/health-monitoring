package com.lessa.healthmonitoring.service

import com.lessa.healthmonitoring.domain.TemperatureRecord
import com.lessa.healthmonitoring.domain.TemperatureScale
import com.lessa.healthmonitoring.domain.User
import com.lessa.healthmonitoring.persistence.entity.TemperatureRecordEntity
import com.lessa.healthmonitoring.persistence.entity.UserEntity
import com.lessa.healthmonitoring.persistence.repository.TemperatureRepository
import com.lessa.healthmonitoring.persistence.repository.UserRepository
import com.lessa.healthmonitoring.service.exception.UserNotFoundException
import com.lessa.healthmonitoring.service.impl.TemperatureServiceImpl
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDate


class TemperatureServiceSpec extends Specification {

    def "Should record a temperature"() {

        given:
        var temperatureRepository = Mock(TemperatureRepository)
        var userRepository = Mock(UserRepository)
        var temperatureService = new TemperatureServiceImpl(temperatureRepository, userRepository)

        and:

        var temperatureRecordToCreate = new TemperatureRecord(null, null, Instant.parse("2023-10-22T18:00:00.00Z"), 37.0d, TemperatureScale.CELSIUS)
        var userEntityReturned = new UserEntity(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var temperatureRecordEntityCreated = new TemperatureRecordEntity(1L, userEntityReturned, Instant.parse("2023-10-22T18:00:00.00Z"), 37.0d, TemperatureScale.CELSIUS)

        var expectedUserReturned = new User(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var expectedTemperatureRecorded = new TemperatureRecord(1L, expectedUserReturned, Instant.parse("2023-10-22T18:00:00.00Z"), 37.0d, TemperatureScale.CELSIUS)

        when:
        var temperatureRecorded = temperatureService.recordTemperature(1L, temperatureRecordToCreate)

        then:
        1 * userRepository.findById(1L) >> Optional.of(userEntityReturned)
        1 * temperatureRepository.save(_) >> temperatureRecordEntityCreated
        expectedTemperatureRecorded == temperatureRecorded

    }

    def "Should throw a exception when user is not found to record a temperature"() {
        given:
        var temperatureRepository = Mock(TemperatureRepository)
        var userRepository = Mock(UserRepository)
        var temperatureService = new TemperatureServiceImpl(temperatureRepository, userRepository)

        and:
        var temperatureRecordToCreate = new TemperatureRecord(null, null, Instant.parse("2023-10-22T18:00:00.00Z"), 37.0d, TemperatureScale.CELSIUS)

        when:
        temperatureService.recordTemperature(1L, temperatureRecordToCreate)

        then:
        1 * userRepository.findById(1L) >> Optional.empty()
        0 * temperatureRepository.save(_)
        var userNotFoundException = thrown UserNotFoundException
        userNotFoundException.getMessage() == "User with id 1 not found to record a temperature."

    }

    def "Should get temperature records within a day and return with the correct scale"() {

        given:
        var temperatureRepository = Mock(TemperatureRepository)
        var userRepository = Mock(UserRepository)
        var temperatureService = new TemperatureServiceImpl(temperatureRepository, userRepository)

        and:
        var startDate = Instant.parse("2023-10-22T00:00:00.00Z")
        var finalDate = Instant.parse("2023-10-22T23:59:59.00Z")

        var userEntityReturned = new UserEntity(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var temperaturesEntityReturned = [
                new TemperatureRecordEntity(1L, userEntityReturned, Instant.parse("2023-10-22T18:01:00.00Z"), 36.0d, TemperatureScale.CELSIUS),
                new TemperatureRecordEntity(2L, userEntityReturned, Instant.parse("2023-10-22T18:02:00.00Z"), 97.7d, TemperatureScale.FAHRENHEIT),
                new TemperatureRecordEntity(3L, userEntityReturned, Instant.parse("2023-10-22T18:03:00.00Z"), 310.15d, TemperatureScale.KELVIN)
        ]

        var userReturned = new User(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var temperaturesRecordsExpected = [
                new TemperatureRecord(1L, userReturned, Instant.parse("2023-10-22T18:01:00.00Z"), equivalentThirtSixDegreesCelsius, temperatureScale),
                new TemperatureRecord(2L, userReturned, Instant.parse("2023-10-22T18:02:00.00Z"), equivalentThirtSixAndHalfDegreesCelsius, temperatureScale),
                new TemperatureRecord(3L, userReturned, Instant.parse("2023-10-22T18:03:00.00Z"), equivalentThirtSevenDegreesCelsius, temperatureScale)
        ]

        when:
        var temperatureRecordsResult = temperatureService.getTemperatureRecordsPerDay(1L, LocalDate.of(2023, 10, 22), temperatureScale)

        then:
        1 * temperatureRepository.findAllByUserIdAndRecordDateBetween(1L, startDate, finalDate) >> temperaturesEntityReturned
        temperaturesRecordsExpected == temperatureRecordsResult

        where:
        temperatureScale            | equivalentThirtSixDegreesCelsius | equivalentThirtSixAndHalfDegreesCelsius | equivalentThirtSevenDegreesCelsius
        TemperatureScale.CELSIUS    | 36.0d                            | 36.5d                                   | 37.0d
        TemperatureScale.FAHRENHEIT | 96.8d                            | 97.7d                                   | 98.6d
        TemperatureScale.KELVIN     | 309.15d                          | 309.65d                                 | 310.15d
    }

    def "Should delete a temperature record"() {

        given:
        var temperatureRepository = Mock(TemperatureRepository)
        var userRepository = Mock(UserRepository)
        var temperatureService = new TemperatureServiceImpl(temperatureRepository, userRepository)

        and:
        var userEntityReturned = new UserEntity(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15))

        when:
        var isUserDeleted = temperatureService.delete(temperatureRecordId)

        then:
        1 * temperatureRepository.findById(temperatureRecordId) >> temperatureRecordEntityFounded
        numberOfDeletionCalls * temperatureRepository.deleteById(temperatureRecordId)
        shouldUserBeDeleted == isUserDeleted

        where:
        shouldUserBeDeleted | numberOfDeletionCalls | temperatureRecordId | temperatureRecordEntityFounded
        true                | 1                     | 1L                  | Optional.of(new TemperatureRecordEntity(1L, userEntityReturned, Instant.parse("2023-10-22T18:01:00.00Z"), 36.0d, TemperatureScale.CELSIUS))
        false               | 0                     | 2L                  | Optional.empty()

    }
}
