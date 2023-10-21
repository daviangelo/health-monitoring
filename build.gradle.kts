plugins {
	java
	groovy
	id("org.springframework.boot") version "3.1.4"
	id("io.spring.dependency-management") version "1.1.3"
}

group = "com.lessa"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

object DependencyVersions {
	const val spock = "2.4-M1-groovy-4.0"
	const val testcontainers = "1.18.3"
	const val jackson = "2.14.0"
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	runtimeOnly("org.postgresql:postgresql")
	implementation("org.flywaydb:flyway-core")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.testcontainers:testcontainers:${DependencyVersions.testcontainers}")
	testImplementation("org.testcontainers:spock:${DependencyVersions.testcontainers}")
	testImplementation("org.testcontainers:kafka:${DependencyVersions.testcontainers}")
	testImplementation("org.testcontainers:jdbc:${DependencyVersions.testcontainers}")
	testImplementation("org.testcontainers:postgresql:${DependencyVersions.testcontainers}")
	implementation(project.dependencies.platform("org.apache.groovy:groovy-bom:4.0.5"))
	implementation("org.apache.groovy:groovy")
	testImplementation("org.spockframework:spock-core:${DependencyVersions.spock}")
	testImplementation("org.spockframework:spock-spring:${DependencyVersions.spock}")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
