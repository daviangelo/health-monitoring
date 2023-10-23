package com.lessa.healthmonitoring.controller


import com.fasterxml.jackson.databind.ObjectMapper
import com.lessa.healthmonitoring.HealthMonitoringApplication

import com.lessa.healthmonitoring.dto.UserDto
import com.lessa.healthmonitoring.persistence.entity.UserEntity
import com.lessa.healthmonitoring.persistence.repository.UserRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest

import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

import org.testcontainers.spock.Testcontainers

import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@SpringBootTest(classes = HealthMonitoringApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
class UserControllerSpec extends Specification {

    @Autowired
    private MockMvc mvc

    @Autowired
    private UserRepository userRepository

    @Autowired
    ObjectMapper mapper

    void setup() {
        userRepository.deleteAll()
    }

    def "Should create an user"() {

        setup:
        var userToCreate = new UserDto(null, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var expectedUserEntityCreated = new UserEntity(null, "Michael Gary Scott", LocalDate.of(1965, 3, 15))

        when:
        var result = mvc.perform(
                post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userToCreate))
        ).andReturn()

        var userCreated = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), UserDto)
        expectedUserEntityCreated.setId(userCreated.getId())

        var userEntityResulted = userRepository.findById(userCreated.getId())

        then:
        result.getResponse().getStatus() == 200
        expectedUserEntityCreated == userEntityResulted.get()

    }

    def "Should get users"() {

        setup:
        var userToPersist = new UserEntity(null, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        userRepository.save(userToPersist)
        when:
        var result = mvc.perform(
                get("/user")
                        .queryParam("page", "0")
                        .queryParam("size", "12")
                        .queryParam("sort", "name,DESC")
        ).andExpect(jsonPath("\$.totalElements").value("1"))
                .andReturn()

        then:
        result.getResponse().getStatus() == 200
    }

    def "Should find user by id"() {

        setup:
        var userToPersist = new UserEntity(null, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var userId = userRepository.save(userToPersist).getId()
        var expectedUserReturn = new UserDto(userId, "Michael Gary Scott", LocalDate.of(1965, 3, 15))

        when:
        var result = mvc.perform(
                get("/user/{id}", userId)

        ).andReturn()

        var userReturned = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), UserDto)

        then:
        result.getResponse().getStatus() == 200
        expectedUserReturn == userReturned

    }

    def "Should return 404 when try to find user with nonexistent id "() {

        when:
        var result = mvc.perform(
                get("/user/{id}", 1)

        ).andReturn()

        then:
        result.getResponse().getStatus() == 404
    }

    def "Should update an user"() {

        setup:
        var userEntityToUpdate = new UserEntity(null, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var userId = userRepository.save(userEntityToUpdate).getId()
        var userToUpdate = new UserDto(userId, "Michael Scott", LocalDate.of(1965, 3, 15))


        when:
        var result = mvc.perform(
                put("/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userToUpdate))
        ).andReturn()

        var userUpdated = mapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), UserDto)

        then:
        result.getResponse().getStatus() == 200
        userToUpdate == userUpdated

    }

    def "Should return 404 when try update user with nonexistent id "() {

        when:
        var userToUpdate = new UserDto(1, "Michael Gary Scott", LocalDate.of(1965, 3, 15))

        var result = mvc.perform(
                put("/user/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userToUpdate))
        ).andReturn()

        then:
        result.getResponse().getStatus() == 404
    }

    def "Should update an user"() {

        setup:
        var userEntityToDelete = new UserEntity(null, "Michael Gary Scott", LocalDate.of(1965, 3, 15))
        var userId = userRepository.save(userEntityToDelete).getId()


        when:
        var result = mvc.perform(
                delete("/user/{id}", userId)
        ).andReturn()

        var userFoundAfterDeletion = userRepository.findById(userId)

        then:
        result.getResponse().getStatus() == 204
        Optional.empty() == userFoundAfterDeletion

    }

    def "Should return 404 when try update user with nonexistent id "() {
        when:
        var result = mvc.perform(
                delete("/user/{id}", 1)
        ).andReturn()

        then:
        result.getResponse().getStatus() == 404
    }

}
