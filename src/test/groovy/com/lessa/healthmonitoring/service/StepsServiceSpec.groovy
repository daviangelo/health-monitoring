package com.lessa.healthmonitoring.service

import com.lessa.healthmonitoring.domain.StepsRecord
import com.lessa.healthmonitoring.domain.User
import com.lessa.healthmonitoring.persistence.entity.StepsRecordEntity
import com.lessa.healthmonitoring.persistence.entity.UserEntity
import com.lessa.healthmonitoring.persistence.repository.StepsRepository
import com.lessa.healthmonitoring.persistence.repository.UserRepository
import com.lessa.healthmonitoring.service.exception.UserNotFoundException
import com.lessa.healthmonitoring.service.impl.StepsServiceImpl
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDate

class StepsServiceSpec extends Specification {
    
    def "Should record steps"() {

        given:
        var stepsRepository = Mock(StepsRepository)
        var userRepository = Mock(UserRepository)
        var stepsService = new StepsServiceImpl(stepsRepository, userRepository)

        and:

        var stepsRecordToCreate = new StepsRecord(null, null, Instant.parse("2023-10-22T18:00:00.00Z"), 8000l)
        var userEntityReturned = new UserEntity(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var stepsRecordEntityCreated = new StepsRecordEntity(1L, userEntityReturned, Instant.parse("2023-10-22T18:00:00.00Z"), 8000l)

        var expectedUserReturned = new User(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var expectedStepsRecorded = new StepsRecord(1L, expectedUserReturned, Instant.parse("2023-10-22T18:00:00.00Z"), 8000l)

        when:
        var stepsRecorded = stepsService.recordSteps(1L, stepsRecordToCreate)

        then:
        1 * userRepository.findById(1L) >> Optional.of(userEntityReturned)
        1 * stepsRepository.save(_) >> stepsRecordEntityCreated
        expectedStepsRecorded == stepsRecorded

    }

    def "Should throw a exception when user is not found to record a steps"() {
        given:
        var stepsRepository = Mock(StepsRepository)
        var userRepository = Mock(UserRepository)
        var stepsService = new StepsServiceImpl(stepsRepository, userRepository)

        and:
        var stepsRecordToCreate = new StepsRecord(null, null, Instant.parse("2023-10-22T18:00:00.00Z"), 8000l)

        when:
        stepsService.recordSteps(1L, stepsRecordToCreate)

        then:
        1 * userRepository.findById(1L) >> Optional.empty()
        0 * stepsRepository.save(_)
        var userNotFoundException = thrown UserNotFoundException
        userNotFoundException.getMessage() == "User with id 1 not found to record steps."

    }

    def "Should get steps records within a day"() {

        given:
        var stepsRepository = Mock(StepsRepository)
        var userRepository = Mock(UserRepository)
        var stepsService = new StepsServiceImpl(stepsRepository, userRepository)

        and:
        var startDate = Instant.parse("2023-10-22T00:00:00.00Z")
        var finalDate = Instant.parse("2023-10-22T23:59:59.00Z")

        var userEntityReturned = new UserEntity(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var stepsEntityReturned = [
                new StepsRecordEntity(1L, userEntityReturned, Instant.parse("2023-10-22T18:01:00.00Z"), 8000l),
                new StepsRecordEntity(2L, userEntityReturned, Instant.parse("2023-10-22T18:02:00.00Z"), 2000l)
        ]

        var userReturned = new User(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var stepsRecordsExpected = [
                new StepsRecord(1L, userReturned, Instant.parse("2023-10-22T18:01:00.00Z"), 8000l),
                new StepsRecord(2L, userReturned, Instant.parse("2023-10-22T18:02:00.00Z"), 2000l)
        ]

        when:
        var stepsRecordsResult = stepsService.getStepsRecordsPerDay(1L, LocalDate.of(2023, 10, 22))

        then:
        1 * stepsRepository.findAllByUserIdAndRecordDateBetween(1L, startDate, finalDate) >> stepsEntityReturned
        stepsRecordsExpected == stepsRecordsResult
    }

    def "Should get the number of steps within a day"() {

        given:
        var stepsRepository = Mock(StepsRepository)
        var userRepository = Mock(UserRepository)
        var stepsService = new StepsServiceImpl(stepsRepository, userRepository)

        and:
        var startDate = Instant.parse("2023-10-22T00:00:00.00Z")
        var finalDate = Instant.parse("2023-10-22T23:59:59.00Z")

        var userEntityReturned = new UserEntity(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var stepsEntityReturned = [
                new StepsRecordEntity(1L, userEntityReturned, Instant.parse("2023-10-22T18:01:00.00Z"), 8000l),
                new StepsRecordEntity(2L, userEntityReturned, Instant.parse("2023-10-22T18:02:00.00Z"), 2000l)
        ]

        when:
        var stepsRecordsResult = stepsService.getNumberOfStepsPerDay(1L, LocalDate.of(2023, 10, 22))

        then:
        1 * stepsRepository.findAllByUserIdAndRecordDateBetween(1L, startDate, finalDate) >> stepsEntityReturned
        10000l == stepsRecordsResult
    }

    def "Should delete a steps record"() {

        given:
        var stepsRepository = Mock(StepsRepository)
        var userRepository = Mock(UserRepository)
        var stepsService = new StepsServiceImpl(stepsRepository, userRepository)

        when:
        var isUserDeleted = stepsService.delete(stepsRecordId)

        then:
        1 * stepsRepository.findById(stepsRecordId) >> stepsRecordEntityFounded
        numberOfDeletionCalls * stepsRepository.deleteById(stepsRecordId)
        shouldUserBeDeleted == isUserDeleted

        where:
        shouldUserBeDeleted | numberOfDeletionCalls | stepsRecordId | stepsRecordEntityFounded
        true                | 1                     | 1L                  | Optional.of(new StepsRecordEntity(1L, new UserEntity(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15)) , Instant.parse("2023-10-22T18:01:00.00Z"), 8000l))
        false               | 0                     | 2L                  | Optional.empty()

    }
}
