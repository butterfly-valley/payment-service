package com.bookanapp.domain.rest.client;

import com.bookanapp.domain.rest.dto.MbWayPaymentRequest;
import com.bookanapp.domain.rest.dto.MbWayServerResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@RegisterRestClient(configKey="mbway-client")
public interface MbWayClient {

    @POST
    @Path("/SetPedidoJSON")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    MbWayServerResponse requestMbWayPayment(MbWayPaymentRequest request);

}
