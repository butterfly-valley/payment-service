package com.bookanapp.domain.rest.resources;
import com.bookanapp.domain.model.Payment;
import com.bookanapp.domain.repository.PaymentRepository;
import com.bookanapp.domain.rest.dto.ResponseError;
import com.bookanapp.domain.rest.service.PaymentService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import java.time.Instant;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(CallbackResource.class)
class CallbackResourceTest {

    @Inject
    PaymentService paymentService;
    @Inject
    PaymentRepository paymentRepository;

    Payment multibancoPayment;

    Payment mbwayPayment;

    @ConfigProperty(name = "app.anti-fishing-key")
    String ANTI_PHISHING_KEY;


    @BeforeEach
    void setUp() {


        multibancoPayment = Payment.builder()
                .paymentMethod(Payment.PaymentMethod.MULTIBANCO)
                .paymentProvider(Payment.PaymentProvider.IFTHENPAY)
                .amount(300)
                .created(Instant.now())
                .multibancoReference("999999999")
                .multibancoEntity("99999")
                .paymentStatus(Payment.PaymentStatus.PENDING)
                .orderId("multibancoPayment")
                .appointmentId(1)
                .providerId(31)
                .build();

        this.paymentService.savePayment(multibancoPayment);

        mbwayPayment = Payment.builder()
                .paymentMethod(Payment.PaymentMethod.MBWAY)
                .paymentProvider(Payment.PaymentProvider.IFTHENPAY)
                .amount(300)
                .created(Instant.now())
                .paymentStatus(Payment.PaymentStatus.PENDING)
                .mbwayPhone("914749888")
                .orderId("mbwayPayment")
                .appointmentId(2)
                .providerId(31)
                .build();

        this.paymentService.savePayment(mbwayPayment);

    }

    @AfterEach
    void tearDown() {
        this.paymentRepository.delete(multibancoPayment);
        this.paymentRepository.delete(mbwayPayment);
    }


    @Test
    @DisplayName("Should return 200 when confirming multibanco payment")
    void multibancoCallback() {

        var response = given()
                .queryParam("key", ANTI_PHISHING_KEY)
                .queryParam("orderId", "multibancoPayment")
                .queryParam("amount", 300)
                .queryParam("entity", "99999")
                .queryParam("reference", "999999999")
                .queryParam("payment_datetime", "30-01-2022 10:55:21")
                .queryParam("entity", "99999")
                .get("/itp/multibanco")
                .thenReturn();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());

    }

    @Test
    @DisplayName("Should return 422 when confirming multibanco payment with invalid antiphishing key")
    void multibancoCallbackInvalidKey() {

        var response = given()
                .queryParam("key", "ANTI_PHISHING_KEY")
                .queryParam("orderId", "multibancoPayment")
                .queryParam("amount", 300)
                .queryParam("entity", "99999")
                .queryParam("reference", "999999999")
                .queryParam("payment_datetime", "30-01-2022 10:55:21")
                .queryParam("entity", "99999")
                .get("/itp/multibanco")
                .thenReturn();
        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatusCode());

    }

    @Test
    @DisplayName("Should return 200 when confirming mbway payment")
    void mbwayCallback() {

        var response = given()
                .queryParam("chave", ANTI_PHISHING_KEY)
                .queryParam("IdPedido", "mbwayPayment")
                .queryParam("valor", 300)
                    .queryParam("referencia", "999999999")
                .queryParam("datahorapag", "30-01-2022 10:55:21")
                .queryParam("estado", "PAGO")
                .get("/itp/mbway")
                .thenReturn();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());

    }

    @Test
    @DisplayName("Should return 422 when confirming mbway payment with invalid antiphishing key")
    void mbwayCallbackInvalidKey() {

        var response = given()
                .queryParam("chave", "ANTI_PHISHING_KEY")
                .queryParam("IdPedido", "mbwayPayment")
                .queryParam("valor", 300)
                .queryParam("referencia", "999999999")
                .queryParam("datahorapag", "30-01-2022 10:55:21")
                .queryParam("estado", "PAGO")
                .get("/itp/mbway")
                .thenReturn();
        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatusCode());

    }

    @Test
    @DisplayName("Should return 422 when confirming mbway payment when state is not PAGO")
    void mbwayCallbackInvalidState() {

        var response = given()
                .queryParam("chave", ANTI_PHISHING_KEY)
                .queryParam("IdPedido", "mbwayPayment")
                .queryParam("valor", 300)
                .queryParam("referencia", "999999999")
                .queryParam("datahorapag", "30-01-2022 10:55:21")
                .queryParam("estado", "PAG")
                .get("/itp/mbway")
                .thenReturn();
        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatusCode());

    }
}
