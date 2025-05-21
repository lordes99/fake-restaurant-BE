package systems.lordes.server.config;


import systems.lordes.server.utils.ControllerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@Profile(Profiles.SECURITY_DISABLED_PROFILE)
@Slf4j
public class DisabledWebSecurityConfig {

    public DisabledWebSecurityConfig() {
        log.warn("*** WARNING *** disabling web security: NOT FOR PRODUCTION");
    }

    @Bean
    public SecurityFilterChain web(HttpSecurity http) throws Exception {
        log.warn("Security is disabled! This profile should not be used in production due to potential security risks.");
        http
                .authorizeHttpRequests((requests) -> requests
                        .anyRequest().anonymous()
                ).csrf(httpSecurityCsrfConfigurer -> {
                    // Usa la costante MappingUtils.PREFIX_API per ignorare il CSRF sulle API
                    httpSecurityCsrfConfigurer.ignoringRequestMatchers(ControllerUtils.PREFIX_API_V1 + "/**");
                });

        return http.build();
    }

}
