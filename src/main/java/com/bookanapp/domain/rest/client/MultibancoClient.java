package com.bookanapp.domain.rest.client;

import com.bookanapp.domain.rest.dto.MultibancoPaymentRequest;
import com.bookanapp.domain.rest.dto.MultibancoPaymentResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.POST;
import java.util.HashMap;

@ApplicationScoped
@RegisterRestClient(configKey="multibanco-client")
public interface MultibancoClient {

    @POST
    MultibancoPaymentResponse requestMultibancoReference(MultibancoPaymentRequest request);


}
