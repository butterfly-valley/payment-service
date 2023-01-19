package com.bookanapp.domain.rest.service;

import com.bookanapp.domain.model.Payment;
import com.bookanapp.domain.model.ScheduleServices;
import com.bookanapp.domain.rest.client.MultibancoClient;
import com.bookanapp.domain.rest.dto.*;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.UUID;


@ApplicationScoped
public class IfThenPayResourceService {

    @Inject
    @RestClient
    MultibancoClient multibancoClient;

    @Inject
    AppointmentService appointmentService;

    @Inject
    ScheduleService scheduleService;

    @Inject
    PaymentService paymentService;

    @ConfigProperty(name = "app.multibanco-key")
    String MULTIBANCO_KEY;

    public Response requestMultibancoReference(long providerId, MultibancoRequest request){

        //validate request
        var appointment = this.appointmentService.getAppointment(providerId, request.getAppointmentId(), request.getOffset());

        if (appointment == null)
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

            // create order
            var payment = Payment.builder()
                    .paymentMethod(Payment.PaymentMethod.MULTIBANCO)
                    .paymentProvider(Payment.PaymentProvider.IFTHENPAY)
                    .amount(amountToPay)
                    .created(Instant.now())
                    .multibancoReference("paymentRequestResponse.getReference()")
                    .paymentStatus(Payment.PaymentStatus.PENDING)
                    .build();
            this.paymentService.savePayment(payment);

            //send response with reference
            var response = MultibancoResponse.builder()
                    .reference(paymentRequestResponse.getReference())
                    .amount(paymentRequestResponse.getAmount())
                    .entity(paymentRequestResponse.getEntity())
                    .expiryDate(paymentRequestResponse.getExpiryDate())
                    .build();

            return Response.status(Response.Status.CREATED).entity(response).build();


        } else {
            return ResponseError.createFromServerError("ERROR")
                    .returnResponseWithStatusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }


    }
}
