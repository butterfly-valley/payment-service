package com.bookanapp.domain.rest.resources;

import com.bookanapp.domain.model.Payment;
import com.bookanapp.domain.rest.dto.ResponseError;
import com.bookanapp.domain.rest.service.PaymentService;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.time.Instant;

@Path("/callback")
public class CallbackResource {
    @Inject
    PaymentService paymentService;

    @ConfigProperty(name = "app.anti-fishing-key")
    String ANTI_PHISHING_KEY;

    @Path("/itp/multibanco")
    @GET
    public Response multibancoCallback(@QueryParam("key") String antiphishingKey, @QueryParam("orderId") String orderId, @QueryParam("amount") Float amount,
                                       @QueryParam("requestId") String requestId, @QueryParam("entity") String entity,
                                       @QueryParam("reference") String reference, @QueryParam("payment_datetime") String payment_datetime) {

        if (!antiphishingKey.equals(ANTI_PHISHING_KEY))
            return Response.status(ResponseError.UNPROCESSABLE_ENTITY_STATUS).build();

        Payment payment = this.paymentService.findByOrderId(orderId);

        if (payment == null) {
            return Response.status(ResponseError.UNPROCESSABLE_ENTITY_STATUS).build();
        } else {
            if (payment.getAmount() == amount &&
                    payment.getMultibancoEntity().equals(entity) &&
                    payment.getMultibancoReference().equals(reference)) {
                payment.setPaymentStatus(Payment.PaymentStatus.PAID);
                payment.setUpdated(Instant.now());
                return Response.status(Response.Status.OK).build();
            } else {
                payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
                payment.setUpdated(Instant.now());
                return Response.status(ResponseError.UNPROCESSABLE_ENTITY_STATUS).build();
            }
        }
    }

    @Path("/itp/mbway")
    @GET
    public Response mbwayCallback(@QueryParam("chave") String antiphishingKey, @QueryParam("IdPedido") String orderId,
                                  @QueryParam("valor") Float amount, @QueryParam("estado") String paymentState,
                                       @QueryParam("referencia") String reference, @QueryParam("datahorapag") String payment_datetime) {

        if (!antiphishingKey.equals(ANTI_PHISHING_KEY))
            return Response.status(ResponseError.UNPROCESSABLE_ENTITY_STATUS).build();

        Payment payment = this.paymentService.findByOrderId(orderId);

        if (payment == null) {
            return Response.status(ResponseError.UNPROCESSABLE_ENTITY_STATUS).build();
        } else {
            if (payment.getAmount() == amount && paymentState.equals("PAGO")) {
                payment.setPaymentStatus(Payment.PaymentStatus.PAID);
                payment.setUpdated(Instant.now());
                return Response.status(Response.Status.OK).build();
            } else {
                payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
                payment.setUpdated(Instant.now());
                return Response.status(ResponseError.UNPROCESSABLE_ENTITY_STATUS).build();
            }
        }
    }
}
