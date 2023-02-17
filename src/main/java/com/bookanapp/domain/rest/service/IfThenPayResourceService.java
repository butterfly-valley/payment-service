package com.bookanapp.domain.rest.service;

import com.bookanapp.config.FullStackConverter;
import com.bookanapp.domain.model.Payment;
import com.bookanapp.domain.model.ScheduleServices;
import com.bookanapp.domain.rest.client.ITPCreditCardClient;
import com.bookanapp.domain.rest.client.MbWayClient;
import com.bookanapp.domain.rest.client.MultibancoClient;
import com.bookanapp.domain.rest.dto.*;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Validator;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.UUID;


@ApplicationScoped
@Slf4j
public class IfThenPayResourceService {

    @Inject
    @RestClient
    MultibancoClient multibancoClient;

    @Inject
    @RestClient
    MbWayClient mbWayClient;

    @Inject
    @RestClient
    ITPCreditCardClient itpCreditCardClient;

    @Inject
    ScheduleService scheduleService;

    @Inject
    PaymentService paymentService;
    @Inject
    Validator validator;


    @ConfigProperty(name = "app.multibanco-key")
    String MULTIBANCO_KEY;

    @ConfigProperty(name = "app.mbway-key")
    String MBWAY_KEY;

    public Response requestMultibancoReference(long providerId, AppointmentPaymentRequest request){
        Payment payment = this.paymentService.findByAppointment(request.getAppointment().getId());

        //filter out paid orders
        if (payment != null && payment.getPaymentStatus().equals(Payment.PaymentStatus.PAID))
            return ResponseError.createFromServerError("PAYMENT_ALREADY_REQUESTED")
                    .returnResponseWithStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);

        //validate request
        Object validatedAmountToPayOrErrorResponse = this.validatePaymentRequest(providerId, request, false);

        if (validatedAmountToPayOrErrorResponse instanceof Response)
            return (Response) validatedAmountToPayOrErrorResponse;



        float amountToPay = (Float) validatedAmountToPayOrErrorResponse;


        // Order id needs be 25 char max as per ifthenpay specification
        String orderId = UUID.randomUUID().toString().replace("-", "").substring(0, 24);

        var paymentRequest = MultibancoPaymentRequest.builder()
                .amount(amountToPay)
                .mbKey(MULTIBANCO_KEY)
                .expiryDays(1)
                .orderId(orderId)
                .build();

        //request reference from ifthenpay server
        var paymentRequestResponse = this.multibancoClient.requestMultibancoReference(paymentRequest);

        if (paymentRequestResponse != null && paymentRequestResponse.getReference().length() == 9) {
            if (payment == null) {

                // create order when does not exist
                payment = Payment.builder()
                        .paymentMethod(Payment.PaymentMethod.MULTIBANCO)
                        .paymentProvider(Payment.PaymentProvider.IFTHENPAY)
                        .amount(amountToPay)
                        .created(Instant.now())
                        .multibancoReference(paymentRequestResponse.getReference())
                        .multibancoEntity(paymentRequestResponse.getEntity())
                        .paymentStatus(Payment.PaymentStatus.PENDING)
                        .orderId(orderId)
                        .appointmentId(request.getAppointment().getId())
                        .providerId(providerId)
                        .build();
            } else {
                payment.setUpdated(Instant.now());
                payment.setAmount(amountToPay);
                payment.setPaymentMethod(Payment.PaymentMethod.MULTIBANCO);
                payment.setPaymentProvider(Payment.PaymentProvider.IFTHENPAY);
                payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
                payment.setMultibancoReference(paymentRequestResponse.getReference());
                payment.setMultibancoEntity(paymentRequestResponse.getEntity());
                payment.setOrderId(orderId);
                payment.setMbwayPhone(null);
                payment.setCcRequestId(null);
            }

            this.paymentService.savePayment(payment);

            return Response.status(Response.Status.CREATED).entity(payment).build();


        } else {
            return ResponseError.createFromServerError("ERROR")
                    .returnResponseWithStatusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }


    }

    public Response requestMbWayPayment(long providerId, AppointmentPaymentRequest request){

        Payment payment = this.paymentService.findByAppointment(request.getAppointment().getId());

        //filter out paid orders
        if (payment != null && payment.getPaymentStatus().equals(Payment.PaymentStatus.PAID))
            return ResponseError.createFromServerError("PAYMENT_ALREADY_REQUESTED")
                    .returnResponseWithStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);


        //validate request
        Object validatedAmountToPayOrErrorResponse = this.validatePaymentRequest(providerId, request, true);

        if (validatedAmountToPayOrErrorResponse instanceof Response)
            return (Response) validatedAmountToPayOrErrorResponse;

        float amountToPay = (Float) validatedAmountToPayOrErrorResponse;

        // Order id needs be 15 char max as per ifthenpay specification
        String orderId = UUID.randomUUID().toString().replace("-", "").substring(0, 14);

        var paymentRequest = MbWayPaymentRequest.builder()
                .valor(Float.toString(amountToPay))
                .MbWayKey(MBWAY_KEY)
                .nrtlm(request.getPhoneNumber())
                .canal("03")
                .referencia(orderId)
                .email("")
                .descricao("")
                .build();
        try {


            var paymentRequestResponse = this.mbWayClient.requestMbWayPayment(paymentRequest);

            if (paymentRequestResponse != null) {
                if (paymentRequestResponse.getStatus().equals("000")) {


                    if (payment == null) {

                        // create order when does not exist
                        payment = Payment.builder()
                                .paymentMethod(Payment.PaymentMethod.MBWAY)
                                .paymentProvider(Payment.PaymentProvider.IFTHENPAY)
                                .amount(amountToPay)
                                .created(Instant.now())
                                .updated(Instant.now())
                                .mbwayPhone(request.getPhoneNumber())
                                .paymentStatus(Payment.PaymentStatus.PENDING)
                                .orderId(orderId)
                                .appointmentId(request.getAppointment().getId())
                                .providerId(providerId)
                                .build();
                    } else {
                        payment.setUpdated(Instant.now());
                        payment.setAmount(amountToPay);
                        payment.setMbwayPhone(request.getPhoneNumber());
                        payment.setPaymentMethod(Payment.PaymentMethod.MBWAY);
                        payment.setPaymentProvider(Payment.PaymentProvider.IFTHENPAY);
                        payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
                        payment.setOrderId(orderId);
                        payment.setMultibancoEntity(null);
                        payment.setMultibancoEntity(null);
                        payment.setCcRequestId(null);
                    }

                    this.paymentService.savePayment(payment);

                    return Response.status(Response.Status.CREATED).entity(payment).build();
                } else {
                    return ResponseError.createFromServerError(MbWayPaymentResponse.createFromCode(paymentRequestResponse.getStatus())
                            .getPaymentCodeMessage()).returnResponseWithStatusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
                }
            }
            return ResponseError.createFromServerError("ERROR")
                    .returnResponseWithStatusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } catch (Exception e) {
            log.error(FullStackConverter.fullStack(e));
            return ResponseError.createFromServerError("ERROR")
                    .returnResponseWithStatusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }



    }

    public Response requestCreditCardPayment(long providerId, AppointmentPaymentRequest request) {

        Payment payment = this.paymentService.findByAppointment(request.getAppointment().getId());

        //filter out paid orders
        if (payment != null && payment.getPaymentStatus().equals(Payment.PaymentStatus.PAID))
            return ResponseError.createFromServerError("PAYMENT_ALREADY_REQUESTED")
                    .returnResponseWithStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);

        //validate request
        Object validatedAmountToPayOrErrorResponse = this.validatePaymentRequest(providerId, request, false);

        if (validatedAmountToPayOrErrorResponse instanceof Response)
            return (Response) validatedAmountToPayOrErrorResponse;

        float amountToPay = (Float) validatedAmountToPayOrErrorResponse;

        // Order id needs be 15 char max as per ifthenpay specification
        String orderId = UUID.randomUUID().toString().replace("-", "").substring(0, 14);

        var paymentRequest = CCPaymentRequest.builder()
                .orderId(orderId)
                .amount(Float.toString(amountToPay))
                .successUrl(request.getSuccessUrl())
                .errorUrl(request.getErrorUrl())
                .cancelUrl(request.getCancelUrl())
                .language(request.getLanguage() != null ? request.getLanguage() : "en")
                .build();
        try {
            var response = this.itpCreditCardClient.requestCreditCardPaymentURL(paymentRequest);
            if (response != null && response.getPaymentUrl() != null && response.getPaymentUrl().length()>0) {

                if (payment == null) {

                    // create order when does not exist
                    payment = Payment.builder()
                            .paymentMethod(Payment.PaymentMethod.CREDIT_CARD)
                            .paymentProvider(Payment.PaymentProvider.IFTHENPAY)
                            .amount(amountToPay)
                            .created(Instant.now())
                            .updated(Instant.now())
                            .ccRequestId(response.getRequestId())
                            .paymentStatus(Payment.PaymentStatus.PENDING)
                            .orderId(orderId)
                            .appointmentId(request.getAppointment().getId())
                            .providerId(providerId)
                            .build();
                } else {
                    payment.setUpdated(Instant.now());
                    payment.setAmount(amountToPay);
                    payment.setPaymentMethod(Payment.PaymentMethod.CREDIT_CARD);
                    payment.setPaymentProvider(Payment.PaymentProvider.IFTHENPAY);
                    payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
                    payment.setOrderId(orderId);
                    payment.setCcRequestId(response.getRequestId());
                    payment.setPaymentUrl(response.getPaymentUrl());
                    payment.setMbwayPhone(null);
                    payment.setMultibancoEntity(null);
                    payment.setMultibancoEntity(null);
                }

                this.paymentService.savePayment(payment);
                payment.setPaymentUrl(response.getPaymentUrl());
                return Response.status(Response.Status.CREATED).entity(payment).build();

            } else {
                return ResponseError.createFromServerError("ERROR")
                        .returnResponseWithStatusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error requesting cc payment for appointment: " + request.getAppointment().getId() + ", error: " +
                    FullStackConverter.fullStack(e));
            return ResponseError.createFromServerError("ERROR")
                    .returnResponseWithStatusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }

    public Response confirmCreditCardPayment(long providerId, String requestId, @QueryParam("status") Payment.PaymentStatus status) {
        var payment = this.paymentService.findByRequestId(requestId, providerId);
        if (payment == null)
            return ResponseError.createFromServerError("INVALID_PAYMENT")
                    .returnResponseWithStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);

        payment.setPaymentStatus(status);
        payment.setUpdated(Instant.now());
        this.paymentService.savePayment(payment);

        return Response.status(Response.Status.OK).build();

    }

    public Object validatePaymentRequest(long providerId, AppointmentPaymentRequest request, boolean isPhoneMandatory) {

        var validationErrors = validator.validate(request);
        if (validationErrors.size()>0)
            return ResponseError.createFromValidationErrors(validationErrors, "VALIDATION_ERROR")
                    .returnResponseWithStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);

        if (isPhoneMandatory && request.getPhoneNumber() == null)
            return ResponseError.createFromValidationErrors(validationErrors, "VALIDATION_ERROR")
                    .returnResponseWithStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);

        var appointment = request.getAppointment();

        if (appointment == null ||  appointment.getProviderId() != providerId || appointment.getId() == null)
            return ResponseError.createFromServerError("INVALID_APPOINTMENT")
                    .returnResponseWithStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);

        var schedule = this.scheduleService.getScheduleById(appointment.getScheduleId(), providerId);

        if (schedule == null)
            return ResponseError.createFromServerError("INVALID_APPOINTMENT")
                    .returnResponseWithStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);

        float amountToPay = 0;


        //determine amount to pay
        if (appointment.getAppointmentServiceTypes().size()>0) {
            var bookedServices = appointment.getAppointmentServiceTypes().stream()
                    .map(service -> schedule.getScheduleServices().stream()
                            .filter(scheduleServices -> scheduleServices.getDescription().equals(service.getDescription())
                                    && scheduleServices.getDuration() == service.getDuration()).findFirst().orElse(null))
                    .toList();

            if (bookedServices.size()>0) {
                for (ScheduleServices scheduleService : bookedServices) {
                    amountToPay = amountToPay + scheduleService.getPrice();
                }

            } else {
                return ResponseError.createFromServerError("INVALID_APPOINTMENT")
                        .returnResponseWithStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
            }

        } else {

            var scheduleInvoicingProfile = this.scheduleService.getScheduleInvoicing(schedule.getId());
            if (scheduleInvoicingProfile == null)
                return ResponseError.createFromServerError("INVALID_APPOINTMENT")
                        .returnResponseWithStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);

            amountToPay = scheduleInvoicingProfile.getPrice();


        }

        if (amountToPay==0)
            return ResponseError.createFromServerError("INVALID_APPOINTMENT")
                    .returnResponseWithStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);

        return amountToPay;
    }

}
