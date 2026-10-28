package ru.lab1.dealership.security;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloakJwtGrantedAuthoritiesConverter
    implements Converter<Jwt, Collection<GrantedAuthority>> {
  @Override
  public Collection<GrantedAuthority> convert(Jwt jwt) {
    Set<String> roles = new LinkedHashSet<>();

    Object realmAccess = jwt.getClaims().get("realm_access");
    if (realmAccess instanceof Map<?, ?> realmAccessMap) {
      Object realmRoles = realmAccessMap.get("roles");
      if (realmRoles instanceof Collection<?> collection) {
        collection.stream()
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .forEach(roles::add);
      }
    }

    Object resourceAccess = jwt.getClaims().get("resource_access");
    if (resourceAccess instanceof Map<?, ?> resourceAccessMap) {
      for (Object value : resourceAccessMap.values()) {
        if (value instanceof Map<?, ?> clientMap) {
          Object clientRoles = clientMap.get("roles");
          if (clientRoles instanceof Collection<?> collection) {
            collection.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .forEach(roles::add);
          }
        }
      }
    }

    return roles.stream()
        .map(String::trim)
        .filter(role -> !role.isBlank())
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase(java.util.Locale.ROOT)))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
}
