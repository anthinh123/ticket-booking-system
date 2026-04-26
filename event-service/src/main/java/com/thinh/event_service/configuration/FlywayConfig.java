package com.thinh.event_service.configuration;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class FlywayConfig {
    
    private final DataSource dataSource;

    @PostConstruct
    public void migrateFlyway() {
        Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .schemas("public")
                .load()
                .migrate();
    }
}
