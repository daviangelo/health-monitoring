package com.lessa.healthmonitoring.service

import com.lessa.healthmonitoring.domain.User
import com.lessa.healthmonitoring.persistence.repository.UserRepository
import com.lessa.healthmonitoring.service.impl.UserServiceImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import spock.lang.Specification

import java.time.LocalDate

class UserServiceSpec extends Specification {

    def "Should create an user"() {

        given:
        var userRepository = Mock(UserRepository)
        var userService = new UserServiceImpl(userRepository)

        when:
        userService.create(new User(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15)))

        then:
        1 * userRepository.save(new User(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15)))
    }

    def "Should get users"() {

        given:
        var userRepository = Mock(UserRepository)
        var userService = new UserServiceImpl(userRepository)

        when:
        userService.getUsers(PageRequest.of(0, 12, Sort.by("name").ascending()))

        then:
        1 * userRepository.findAll(PageRequest.of(0, 12, Sort.by("name").ascending()))
    }

    def "Should find user by id"() {

        given:
        var userRepository = Mock(UserRepository)
        var userService = new UserServiceImpl(userRepository)

        when:
        userService.findById(1L)

        then:
        1 * userRepository.findById(1L)
    }

    def "Should update user"() {

        given:
        var userRepository = Mock(UserRepository)
        var userService = new UserServiceImpl(userRepository)

        when:
        userService.update(new User(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15)))

        then:
        1 * userRepository.findById(1L)
        1 * userRepository.save(new User(1L, "Michael Gary Scott", LocalDate.of(1965, 3, 15)))

    }

    def "Should delete user"() {

        given:
        var userRepository = Mock(UserRepository)
        var userService = new UserServiceImpl(userRepository)

        when:
        userService.delete(1L)

        then:
        1 * userRepository.findById(1L)
        1 * userRepository.deleteById(1L)

    }
}
