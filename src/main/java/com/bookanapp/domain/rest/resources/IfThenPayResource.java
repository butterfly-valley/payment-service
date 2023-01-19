package com.bookanapp.domain.rest.resources;

import com.bookanapp.domain.rest.dto.MultibancoRequest;
import com.bookanapp.domain.rest.service.AuthService;
import com.bookanapp.domain.rest.service.IfThenPayResourceService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/ifthenpay")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ROLE_API_CLIENT")
@Slf4j
public class IfThenPayResource {

    @Inject
    SecurityContext context;

    @Inject
    AuthService authService;

    @Inject
    IfThenPayResourceService ifThenPayResourceService;

    @POST
    @Path("/multibanco/request")
    public Response requestMultibancoReference(MultibancoRequest request){
        var providerId = this.authService.returnProviderIdFromClaims(context);
        return this.ifThenPayResourceService.requestMultibancoReference(providerId, request);
    }
}
