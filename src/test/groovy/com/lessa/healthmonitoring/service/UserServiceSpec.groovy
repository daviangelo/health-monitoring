package com.lessa.healthmonitoring.service

import com.lessa.healthmonitoring.domain.User
import com.lessa.healthmonitoring.persistence.entity.UserEntity
import com.lessa.healthmonitoring.persistence.repository.UserRepository
import com.lessa.healthmonitoring.service.impl.UserServiceImpl
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import spock.lang.Specification

import java.time.LocalDate

class UserServiceSpec extends Specification {

    def "Should create an user"() {

        given:
        var userRepository = Mock(UserRepository)
        var userService = new UserServiceImpl(userRepository)

        and:
        var userToCreate = new User(null, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var userEntityToCreate = new UserEntity(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var expectedUserCreated = new User(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15))

        when:
        var returnedUser = userService.create(userToCreate)

        then:
        1 * userRepository.save(_) >> userEntityToCreate
        expectedUserCreated == returnedUser

    }

    def "Should get users"() {

        given:
        var userRepository = Mock(UserRepository)
        var userService = new UserServiceImpl(userRepository)

        and:
        var pageRequest = PageRequest.of(0, 1, Sort.by("name").ascending())
        var pageEntityReturned = new PageImpl<UserEntity>([new UserEntity(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15))])
        var expectedPageResult = new PageImpl<User>([new User(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15))])

        when:
        var pageResult = userService.getUsers(pageRequest)

        then:
        1 * userRepository.findAll(pageRequest) >> pageEntityReturned
        expectedPageResult == pageResult
    }

    def "Should find user by id"() {

        given:
        var userRepository = Mock(UserRepository)
        var userService = new UserServiceImpl(userRepository)

        and:
        var userEntityReturned = Optional.of(new UserEntity(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15)))
        var expectedUser = Optional.of(new User(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15)))

        when:
        var userResult = userService.findById(1L)

        then:
        1 * userRepository.findById(1L) >> userEntityReturned
        expectedUser == userResult

    }

    def "Should update user"() {

        given:
        var userRepository = Mock(UserRepository)
        var userService = new UserServiceImpl(userRepository)

        and:
        var userEntityFounded = Optional.of(new UserEntity(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15)))
        var userEntityUpdated = new UserEntity(1L, "Michael Scott", LocalDate.of(1965, 3, 15))
        var expectedUserUpdated = Optional.of(new User(1L, "Michael Scott", LocalDate.of(1965, 3, 15)))

        when:
        var userUpdated = userService.update(1L, new User(1L, "Michael Scott", LocalDate.of(1965, 3, 15)))

        then:
        1 * userRepository.findById(1L) >> userEntityFounded
        1 * userRepository.save(_) >> userEntityUpdated
        expectedUserUpdated == userUpdated


    }

    def "Should delete user"() {

        given:
        var userRepository = Mock(UserRepository)
        var userService = new UserServiceImpl(userRepository)

        when:
        var isUserDeleted = userService.delete(userId)

        then:
        1 * userRepository.findById(userId) >> userEntityFounded
        numberOfDeletionCalls * userRepository.deleteById(userId)
        shouldUserBeDeleted == isUserDeleted

        where:
        shouldUserBeDeleted | numberOfDeletionCalls | userId | userEntityFounded
        true                | 1                     | 1L     | Optional.of(new UserEntity(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15)))
        false               | 0                     | 2L     | Optional.empty()


    }
}
