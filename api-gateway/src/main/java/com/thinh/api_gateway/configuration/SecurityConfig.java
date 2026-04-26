package com.thinh.api_gateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS = {
            "/identity/auth/login", "/identity/auth/register",
            "/identity/auth/**"
    };

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(exchanges ->
                exchanges
                        .pathMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .pathMatchers(HttpMethod.GET, "/event/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/event/**").hasAuthority("SCOPE_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/event/**").hasAuthority("SCOPE_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/event/**").hasAuthority("SCOPE_ADMIN")
                        .anyExchange().authenticated());

        http.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtSpec -> jwtSpec.jwtDecoder(jwtDecoder()))
        );

        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
        return NimbusReactiveJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }
}
