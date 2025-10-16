package com.hcmut.voltrent.filter;

import com.hcmut.voltrent.utils.RequestUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.Filter;
import jakarta.servlet.annotation.WebFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Logs all HTTP requests and responses.
 */
@Component
@WebFilter("/*")
public class HttpLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Wrap request and response to read multiple times
        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(req);
        CachedBodyHttpServletResponse wrappedResponse = new CachedBodyHttpServletResponse(res);

        logHttpRequest(wrappedRequest, req);

        long start = System.currentTimeMillis();
        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - start;
            // Log response
            String responseBody = new String(wrappedResponse.getCachedBody(), StandardCharsets.UTF_8);

            logHttpResponse(duration, responseBody, res);
            // Copy body back to the real response
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logHttpRequest(CachedBodyHttpServletRequest wrappedRequest, HttpServletRequest req) {
        // Log request
        String requestBody = new String(wrappedRequest.getCachedBody(), StandardCharsets.UTF_8);
        log.info(""" 
                        HTTP REQUEST:
                        IP: {} 
                        Method: {}
                        URI: {}
                        Headers: {}
                        Params: {}
                        Body: {}
                        """,
                RequestUtil.getIpAddress(req),
                req.getMethod(),
                req.getRequestURI(),
                RequestUtil.getHeaders(req),
                RequestUtil.getParams(req),
                requestBody.isEmpty() ? "(empty)" : requestBody
        );
    }

    private void logHttpResponse(long duration, String responseBody, HttpServletResponse res) {
        log.info("""
                        HTTP RESPONSE:
                        Http Status: {}
                        Duration: {} ms
                        Body: {}
                        """,
                res.getStatus(),
                duration,
                responseBody.isEmpty() ? "(empty)" : responseBody
        );

    }


}
