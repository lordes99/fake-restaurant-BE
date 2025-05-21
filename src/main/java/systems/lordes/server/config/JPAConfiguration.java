package systems.lordes.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Instant;
import java.util.Optional;

@Configuration
@EnableJpaRepositories(basePackages = {"systems.lordes.server.repository"})
@EntityScan(basePackages = {
    "systems.lordes.server.entity",
})
@EnableTransactionManagement
@EnableJpaAuditing(dateTimeProviderRef = "utcDateTimeProvider")
@Slf4j
public class JPAConfiguration {

    @Bean
    public DateTimeProvider utcDateTimeProvider() {
        return () -> Optional.of(Instant.now());
    }

}
