package com.hcmut.voltrent.lib.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpMethod;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class HttpRequest {
    private String url;
    private Object body;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private HttpMethod httpMethod;
}
