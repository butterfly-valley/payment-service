package com.bookanapp.domain.rest.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MbWayServerResponse {

    @JsonProperty("IdPedido")
    public String orderId;
    @JsonProperty("Valor")
    public String amount;
    @JsonProperty("CodigoMoeda")
    public String currencyCode;
    @JsonProperty("Estado")
    public String status;
    @JsonProperty("DataHora")
    public String dateTime;
    @JsonProperty("MsgDescricao")
    public String message;


}
