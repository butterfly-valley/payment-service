package com.bookanapp.domain.rest.dto;

import lombok.Data;

@Data
public class KeycloakTokenResponse{
    public String access_token;
    public int expires_in;
    public int refresh_expires_in;
    public String token_type;
    public String scope;
}
