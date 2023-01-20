package com.bookanapp.domain.rest.service;

import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.SecurityContext;

@ApplicationScoped
public class AuthService {

    public long returnProviderIdFromClaims(SecurityContext context) {
        var principal = (OidcJwtCallerPrincipal)context.getUserPrincipal();
        var claimMap = principal.getClaims().getClaimsMap();
        return Long.parseLong(claimMap.get("providerId").toString());
    }
}
