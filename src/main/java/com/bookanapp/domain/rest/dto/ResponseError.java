package com.bookanapp.domain.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ResponseError {

    public final static int UNPROCESSABLE_ENTITY_STATUS = 422;

    private String message;
    private List<FieldError> errors;

    public ResponseError(String message) {
        this.message = message;
    }

    public static <T> ResponseError createFromValidationErrors(Set<ConstraintViolation<T>> violations, String message){
        var errors = violations.stream()
                .map(cv -> new FieldError(cv.getPropertyPath().toString(), cv.getMessage()))
                .collect(Collectors.toList());

        return new ResponseError(message, errors);

    }

    public static <T> ResponseError createFromCustomValidationErrors(List<FieldError> errors, String message){
        return new ResponseError(message, errors);

    }

    public static <T> ResponseError createFromServerError(String message){
        return new ResponseError(message);
    }


    public Response returnResponseWithStatusCode (int statusCode){
        return Response.status(statusCode).entity(this).build();
    }

}
