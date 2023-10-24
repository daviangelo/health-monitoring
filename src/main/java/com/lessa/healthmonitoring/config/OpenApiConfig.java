package com.lessa.healthmonitoring.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Health Monitoring",
                description = """
                        Application to manage a user's health data, including:
                        User management; Record temperature data and obtain daily data according to specific date;
                        Record steps data and obtain daily data according to specific date;
                        Record heart beat data and obtain daily data according to specific date.""",
                contact = @Contact(
                        name = "Davi Lessa",
                        email = "daviarcangelo@gmail.com"
                ),
                license = @License(
                        name = "MIT Licence",
                        url = "https://github.com/daviangelo/health-monitoring/blob/main/LICENSE")),
        servers = @Server(url = "http://localhost:8080")
)
public class OpenApiConfig {
}
