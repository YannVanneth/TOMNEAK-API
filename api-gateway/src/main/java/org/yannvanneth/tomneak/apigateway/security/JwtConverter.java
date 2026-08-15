package org.yannvanneth.tomneak.apigateway.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Reactive converter to extract Keycloak realm roles from JWT token claims into Spring Security GrantedAuthorities.
 * Used by Spring Cloud Gateway WebFlux security.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Component
public class JwtConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    /**
     * Converts a Reactive JWT token into a Mono containing an AbstractAuthenticationToken with extracted authorities.
     *
     * @param jwt the Jwt token
     * @return Mono publishing JwtAuthenticationToken with extracted authorities
     */
    @Override
    public Mono<AbstractAuthenticationToken> convert(@NonNull Jwt jwt) {
        // Extract realm_access claim map from Keycloak JWT payload
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        Collection<GrantedAuthority> roles = Collections.emptySet();

        if (realmAccess != null && realmAccess.containsKey("roles")) {
            @SuppressWarnings("unchecked")
            Collection<String> rolesList = (Collection<String>) realmAccess.get("roles");
            roles = rolesList.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }

        // Return reactive Mono wrapping the authentication token
        return Mono.just(new JwtAuthenticationToken(jwt, roles));
    }
}
