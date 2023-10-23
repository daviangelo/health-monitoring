package com.lessa.healthmonitoring.service

import com.lessa.healthmonitoring.domain.HeartRateRecord
import com.lessa.healthmonitoring.domain.User
import com.lessa.healthmonitoring.persistence.entity.HeartRateRecordEntity
import com.lessa.healthmonitoring.persistence.entity.UserEntity
import com.lessa.healthmonitoring.persistence.repository.HeartRateRepository
import com.lessa.healthmonitoring.persistence.repository.UserRepository
import com.lessa.healthmonitoring.service.exception.UserNotFoundException
import com.lessa.healthmonitoring.service.impl.HeartRateServiceImpl
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDate

class HeartRateServiceSpec extends Specification {

    def "Should record a heart rate"() {

        given:
        var heartRateRepository = Mock(HeartRateRepository)
        var userRepository = Mock(UserRepository)
        var heartRateService = new HeartRateServiceImpl(heartRateRepository, userRepository)

        and:

        var heartRateRecordToCreate = new HeartRateRecord(null, null, Instant.parse("2023-10-22T18:00:00.00Z"), 80)
        var userEntityReturned = new UserEntity(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var heartRateRecordEntityCreated = new HeartRateRecordEntity(1L, userEntityReturned, Instant.parse("2023-10-22T18:00:00.00Z"), 80)

        var expectedUserReturned = new User(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var expectedHeartRateRecorded = new HeartRateRecord(1L, expectedUserReturned, Instant.parse("2023-10-22T18:00:00.00Z"), 80)

        when:
        var heartRateRecorded = heartRateService.recordHeartRate(1L, heartRateRecordToCreate)

        then:
        1 * userRepository.findById(1L) >> Optional.of(userEntityReturned)
        1 * heartRateRepository.save(_) >> heartRateRecordEntityCreated
        expectedHeartRateRecorded == heartRateRecorded

    }

    def "Should throw a exception when user is not found to record a heart rate"() {
        given:
        var heartRateRepository = Mock(HeartRateRepository)
        var userRepository = Mock(UserRepository)
        var heartRateService = new HeartRateServiceImpl(heartRateRepository, userRepository)

        and:
        var heartRateRecordToCreate = new HeartRateRecord(null, null, Instant.parse("2023-10-22T18:00:00.00Z"), 80)

        when:
        heartRateService.recordHeartRate(1L, heartRateRecordToCreate)

        then:
        1 * userRepository.findById(1L) >> Optional.empty()
        0 * heartRateRepository.save(_)
        var userNotFoundException = thrown UserNotFoundException
        userNotFoundException.getMessage() == "User with id 1 not found to record a heart rate."

    }

    def "Should get heart rate records within a day"() {

        given:
        var heartRateRepository = Mock(HeartRateRepository)
        var userRepository = Mock(UserRepository)
        var heartRateService = new HeartRateServiceImpl(heartRateRepository, userRepository)

        and:
        var startDate = Instant.parse("2023-10-22T00:00:00.00Z")
        var finalDate = Instant.parse("2023-10-22T23:59:59.00Z")

        var userEntityReturned = new UserEntity(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var heartRateEntityReturned = [
                new HeartRateRecordEntity(1L, userEntityReturned, Instant.parse("2023-10-22T18:01:00.00Z"), 80),
                new HeartRateRecordEntity(2L, userEntityReturned, Instant.parse("2023-10-22T18:02:00.00Z"), 75)
        ]

        var userReturned = new User(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var heartRateRecordsExpected = [
                new HeartRateRecord(1L, userReturned, Instant.parse("2023-10-22T18:01:00.00Z"), 80),
                new HeartRateRecord(2L, userReturned, Instant.parse("2023-10-22T18:02:00.00Z"), 75)
        ]

        when:
        var heartRateRecordsResult = heartRateService.getHeartRateRecordsPerDay(1L, LocalDate.of(2023, 10, 22))

        then:
        1 * heartRateRepository.findAllByUserIdAndRecordDateBetween(1L, startDate, finalDate) >> heartRateEntityReturned
        heartRateRecordsExpected == heartRateRecordsResult
    }


    def "Should delete a heart rate record"() {

        given:
        var heartRateRepository = Mock(HeartRateRepository)
        var userRepository = Mock(UserRepository)
        var heartRateService = new HeartRateServiceImpl(heartRateRepository, userRepository)

        when:
        var isUserDeleted = heartRateService.delete(heartRateRecordId)

        then:
        1 * heartRateRepository.findById(heartRateRecordId) >> heartRateRecordEntityFounded
        numberOfDeletionCalls * heartRateRepository.deleteById(heartRateRecordId)
        shouldUserBeDeleted == isUserDeleted

        where:
        shouldUserBeDeleted | numberOfDeletionCalls | heartRateRecordId | heartRateRecordEntityFounded
        true                | 1                     | 1L                  | Optional.of(new HeartRateRecordEntity(1L, new UserEntity(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15)) , Instant.parse("2023-10-22T18:01:00.00Z"), 80))
        false               | 0                     | 2L                  | Optional.empty()

    }
    
}
