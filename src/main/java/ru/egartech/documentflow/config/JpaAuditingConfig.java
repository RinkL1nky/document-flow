package ru.egartech.documentflow.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import ru.egartech.documentflow.entity.Employee;
import ru.egartech.documentflow.util.AuthenticationFacade;

@RequiredArgsConstructor
@EnableJpaAuditing
@Configuration
public class JpaAuditingConfig {

    private final AuthenticationFacade authenticationFacade;

    @Bean
    public AuditorAware<Employee> auditorProvider() {
        return authenticationFacade::findCurrentEmployee;
    }

}
