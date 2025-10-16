package com.hcmut.voltrent.utils;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtil {

    public static String getIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            var remoteAddr = request.getRemoteAddr();
            if (remoteAddr == null) {
                remoteAddr = "127.0.0.1";   // TODO: the ip of this BE app
            }

            return remoteAddr;
        }

        return xForwardedForHeader.split(",")[0].trim();
    }

    public static String getHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder("{ ");
        request.getHeaderNames().asIterator().forEachRemaining(name ->
                headers.append(name).append(": ").append(request.getHeader(name)).append(", ")
        );
        headers.append("}");
        return headers.toString();
    }

    public static String getParams(HttpServletRequest request) {
        StringBuilder params = new StringBuilder("{ ");
        request.getParameterMap().forEach((name, values) ->
                params.append(name).append(": ").append(String.join(", ", values)).append(", ")
        );
        params.append("}");
        return params.toString();
    }
}
