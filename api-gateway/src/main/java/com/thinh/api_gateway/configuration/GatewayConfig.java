package com.thinh.api_gateway.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.WebFilter;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("identity-service", r -> r.path("/api/v1/auth/**", "/api/v1/users/**")
                        .uri("http://127.0.0.1:8081"))
                .route("inventory-service-seats", r -> r.path("/api/v1/events/*/seats", "/api/v1/seats/**", "/api/v1/reservations/**")
                        .uri("http://127.0.0.1:8083"))
                .route("event-service", r -> r.path("/api/v1/events/**")
                        .uri("http://127.0.0.1:8082"))
                .route("booking-service", r -> r.path("/api/v1/bookings/**")
                        .uri("http://127.0.0.1:8084"))
                .build();
    }

    /**
     * This filter extracts the userId from the JWT and adds it to the X-User-Id header
     * before forwarding the request to downstream services.
     */
    @Bean
    public WebFilter userHeaderFilter() {
        return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .filter(auth -> auth instanceof JwtAuthenticationToken)
                .cast(JwtAuthenticationToken.class)
                .map(jwtAuth -> {
                    String userId = jwtAuth.getTokenAttributes().get("userId").toString();
                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .header("X-User-Id", userId)
                            .build();
                    return exchange.mutate().request(modifiedRequest).build();
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }
}
