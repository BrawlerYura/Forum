package org.example.common.security;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.common.security.props.SecurityProps;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
@EnableConfigurationProperties(SecurityProps.class)
public class SecurityConfig {

    private final SecurityProps securityProps;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Шифровальщик паролей
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Цепочка для фильтрации запросов с JWT.
     * Ловит все запросы, которые начинаются на jwt-rootPath (указан в конфиге),
     * и применяет к ним фильтр для запросов с JWT,
     * исключая запросы, которые мы перечислили в конфиге jwtToken-permitAll
     */
    @Bean
    public SecurityFilterChain filterChainJwt(HttpSecurity http) throws Exception {
        http = http
                .cors()
                .and()
                .securityMatcher(createCustomReqMatcher(
                        securityProps.getJwtToken().getRootPath(),
                        securityProps.getJwtToken().getPermitAll()))
                .addFilterBefore(
                        new JwtTokenFilter(securityProps.getJwtToken().getSecret()),
                        UsernamePasswordAuthenticationFilter.class
                )

                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();
        return http.build();
    }

    /**
     * <p>Цепочка для фильтрации интеграционных запросов </p>
     * <p>
     * Она ловит все запросы, которые начинаются на integration-rootPath (указан в конфиге)
     * и применяет к ним фильтр интеграций
     */
    @SneakyThrows
    @Bean
    public SecurityFilterChain filterChainIntegration(HttpSecurity http) {
        http = http
                .cors()
                .and()
                .securityMatcher(createCustomReqMatcher(securityProps.getIntegrations().getRootPath()))
                .addFilterBefore(
                        new IntegrationFilter(securityProps.getIntegrations().getApiKey()),
                        UsernamePasswordAuthenticationFilter.class
                )
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();
        return http.build();
    }

    /**
     * Метод для проверки пути сервлета, проверяет, что сервлет вообще есть, начинается c /root-path и отсутствует в permit-all
     *
     * @param rootPath   паттерн для заданного пути
     * @param ignorePath паттерн(ы) для игнорируемых путей
     * @return {@link RequestMatcher}
     */
    private RequestMatcher createCustomReqMatcher(String rootPath, String... ignorePath) {
        return req -> {
            boolean result = Objects.nonNull(req.getServletPath())
                    && req.getServletPath().startsWith(rootPath)
                    && Arrays.stream(ignorePath).noneMatch(item -> req.getServletPath().startsWith(item));
            return result;
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
