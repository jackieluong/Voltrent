package com.hcmut.voltrent.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RestResponse {
    private int code;
    private String message;
    private Object data;


}
