package com.bookanapp.domain.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MbWayPaymentResponse {

    private String paymentCodeMessage;

    private String code;


    public static <T> MbWayPaymentResponse createFromCode(String code) {
        return new MbWayPaymentResponse(getCodeMessage(code), code);
    }

    private static String getCodeMessage(String code) {
        return switch (code) {
            case "020" -> "Operação financeira cancelada pelo utilizador";
            case "023" -> "Operação financeira devolvida pelo Comerciante";
            case "048" -> "Operação financeira anulada pelo Comerciante";
            case "100" -> "Não foi possível concluir a Operação";
            case "104" -> "Operação financeira não permitida";
            case "111" -> "O formato do número de telemóvel não se encontrava no formato correto";
            case "113" -> "O número de telemóvel usado como identificador não foi encontrado";
            case "122" -> "Operação recusada ao utilizador";
            case "123" -> "Operação financeira não encontrada";
            case "125" -> "Operação recusada ao utilizador";
            default -> "Operação financeira inicializada com sucesso";
        };
    }
}
