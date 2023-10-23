package com.lessa.healthmonitoring.controller

import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import spock.lang.Shared
import spock.lang.Specification

class IntegrationSpec extends Specification {

    @Shared
    static GenericContainer redisContainer = new GenericContainer(DockerImageName.parse("redis:7.2.2-alpine"))
            .withExposedPorts(6379)

    def setupSpec() {
        redisContainer.start()
        System.setProperty("spring.data.redis.host", redisContainer.getHost())
        System.setProperty("spring.data.redis.port", redisContainer.getMappedPort(6379).toString())
    }
}
