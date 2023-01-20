package com.bookanapp.domain.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.RestForm;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MbWayPaymentRequest {
    @RestForm
    private String MbWayKey;
    @RestForm
    private String canal = "03";
    @RestForm
    private String valor;
    @RestForm
    private String referencia;
    @RestForm
    private String nrtlm;
    @RestForm
    private String descricao;
    @RestForm
    private String email;

}
