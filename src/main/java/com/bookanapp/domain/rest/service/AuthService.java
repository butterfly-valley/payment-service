package com.bookanapp.domain.rest.service;
import com.bookanapp.domain.rest.client.AuthClient;
import com.bookanapp.domain.rest.dto.KeycloakTokenRequest;
import com.bookanapp.domain.rest.dto.KeycloakTokenResponse;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.SecurityContext;

@ApplicationScoped
public class AuthService {


    @Inject
    @RestClient
    AuthClient authClient;

    /*
    Generate keycloak client JWT
     */
    public KeycloakTokenResponse returnKeycloakJWT(KeycloakTokenRequest request){
        return this.authClient.getAccessToken(request);
    }

    public long returnProviderIdFromClaims(SecurityContext context) {
        var principal = (OidcJwtCallerPrincipal)context.getUserPrincipal();
        var claimMap = principal.getClaims().getClaimsMap();
        return Long.parseLong(claimMap.get("providerId").toString());
    }
}
