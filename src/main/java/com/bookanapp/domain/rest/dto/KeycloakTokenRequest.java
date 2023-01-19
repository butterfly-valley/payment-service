package com.bookanapp.domain.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.RestForm;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class KeycloakTokenRequest {
    @RestForm
    @NotBlank
    private String client_id;
    @RestForm
    @NotBlank
    private String client_secret;
    @RestForm
    @NotBlank
    private String grant_type = "client_credentials";

}
