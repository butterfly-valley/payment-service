package com.bookanapp.domain.rest.client;

import com.bookanapp.domain.rest.dto.CCPaymentRequest;
import com.bookanapp.domain.rest.dto.CCPaymentResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.POST;

@ApplicationScoped
@RegisterRestClient(configKey="itp-credit-card-client")
public interface ITPCreditCardClient {

    @POST
    CCPaymentResponse requestCreditCardPaymentURL(CCPaymentRequest request);


}
