package com.hcmut.voltrent.dtos.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RestResponse<T> {
    private int code;
    private String message;
    private T data;


    public static ResponseEntity<RestResponse<?>> successResponse(String message, Object data) {
        return apiResponse(HttpStatus.OK.value(), message, data);
    }

    public static ResponseEntity<RestResponse<?>> apiResponse(int code, String message, Object data) {
        return ResponseEntity.ok(RestResponse.builder()
                .code(code)
                .message(message)
                .data(data)
                .build());
    }

}
