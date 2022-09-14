package com.simonov.voting.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.time.LocalTime.now;

@Configuration
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
@OpenAPIDefinition
        (
                info = @Info(
                        title = "voting API",
                        version = "1.0",
                        description = """
                                <p><b>Тестовые пользователи:</b><br>
                                 - admin@gmail.com / admin<br>
                                 - user@yandex.ru / password<br></p>
                                """,
                        contact = @Contact(
                                url = "https://github.com/simonovAlexey",
                                name = "a.simonov",
                                email = "3694369@gmail.com")
                ),
                security = @SecurityRequirement(name = "basicAuth")
        )
public class SwaggerConfig {

    static {
        Schema<LocalTime> schema = new Schema<>();
        schema.example(now().format(DateTimeFormatter.ISO_TIME));
        SpringDocUtils.getConfig().replaceWithSchema(LocalTime.class, schema);
    }

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("VOTING API")
                .pathsToMatch("/api/**")
                .build();
    }
}
