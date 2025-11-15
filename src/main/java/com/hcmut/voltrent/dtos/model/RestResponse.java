package com.hcmut.voltrent.dtos.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RestResponse {
    private int code;
    private String message;
    private Object data;


}
