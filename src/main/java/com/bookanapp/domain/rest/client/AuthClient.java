package com.bookanapp.domain.rest.client;



import com.bookanapp.domain.rest.dto.KeycloakTokenRequest;
import com.bookanapp.domain.rest.dto.KeycloakTokenResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@RegisterRestClient(configKey="keycloak-client")
public interface AuthClient {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    KeycloakTokenResponse getAccessToken(KeycloakTokenRequest request);
}
