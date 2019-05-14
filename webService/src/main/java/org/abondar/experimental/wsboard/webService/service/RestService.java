package org.abondar.experimental.wsboard.webService.service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
        info = @Info(
                title = "Web Scrum Board",
                version = "1.0",
                description = "Web Scrum Board REST API",
                license = @License(name = "MIT"),
                contact = @Contact(url = "https://abondar24.github.io/WebScrumBoard/", name = "Alex Bondar",
                        email = "abondar1992@gmail.com")
        ),
        tags = {
                @Tag(name = "WSB")
        },
        security = {
                @SecurityRequirement(name = "req 1", scopes = {"a", "b"}),
        },
        servers = {
                @Server(
                        description = "wsb",
                        url = "wsb:8024/cxf/wsboard")
        }
)
public interface RestService {
}
