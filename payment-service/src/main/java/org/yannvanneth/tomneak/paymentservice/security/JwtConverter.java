package org.yannvanneth.tomneak.paymentservice.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Converter to extract Keycloak realm roles from Jwt token claims into Spring Security GrantedAuthorities.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Component
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    /**
     * Converts a Jwt token into an AbstractAuthenticationToken with extracted authorities.
     *
     * @param jwt the Jwt token
     * @return JwtAuthenticationToken containing claims and granted authorities
     */
    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        Set<GrantedAuthority> roles = Collections.emptySet();

        if (realmAccess != null && realmAccess.containsKey("roles")) {
            @SuppressWarnings("unchecked")
            Set<String> rolesSet = (Set<String>) realmAccess.get("roles");
            roles = rolesSet.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }
        return new JwtAuthenticationToken(jwt, roles);
    }
}
