package com.bookanapp.domain.rest.resources;

import com.bookanapp.domain.model.Payment;
import com.bookanapp.domain.rest.dto.AppointmentPaymentRequest;
import com.bookanapp.domain.rest.service.AuthService;
import com.bookanapp.domain.rest.service.IfThenPayResourceService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/itp")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ROLE_API_CLIENT")
public class IfThenPayResource {

    @Inject
    SecurityContext context;

    @Inject
    AuthService authService;

    @Inject
    IfThenPayResourceService ifThenPayResourceService;

    @POST
    @Path("/multibanco/request")
    public Response requestMultibancoReference(AppointmentPaymentRequest request) {
        var providerId = this.authService.returnProviderIdFromClaims(context);
        return this.ifThenPayResourceService.requestMultibancoReference(providerId, request);
    }

    @POST
    @Path("/mbway/request")
    public Response requestMbWayPayment(AppointmentPaymentRequest request) {
        var providerId = this.authService.returnProviderIdFromClaims(context);
        return this.ifThenPayResourceService.requestMbWayPayment(providerId, request);
    }

    @POST
    @Path("/cc/request")
    public Response requestCreditCardPayment(AppointmentPaymentRequest request) {
        var providerId = this.authService.returnProviderIdFromClaims(context);
        return this.ifThenPayResourceService.requestCreditCardPayment(providerId, request);
    }

    @GET
    @Path("/cc/confirm")
    public Response requestCreditCardPayment(@QueryParam("requestId") String requestId, @QueryParam("status") Payment.PaymentStatus status) {
        var providerId = this.authService.returnProviderIdFromClaims(context);
        return this.ifThenPayResourceService.confirmCreditCardPayment(providerId, requestId, status);
    }
}
