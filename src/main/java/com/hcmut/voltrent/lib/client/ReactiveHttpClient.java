package com.hcmut.voltrent.lib.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ReactiveHttpClient {

    private final WebClient webClient;

    public ReactiveHttpClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public <T> Mono<T> execute(HttpRequest ctx, Class<T> responseType) {
        log.info("[OUTBOUND REQUEST] Method={}, Endpoint={}", ctx.getHttpMethod(), ctx.getUrl());

        WebClient.RequestBodySpec requestSpec = webClient.method(ctx.getHttpMethod())
                .uri(uri -> {
                    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(ctx.getUrl());
                    if (ctx.getQueryParams() != null && !ctx.getQueryParams().isEmpty()) {
                        ctx.getQueryParams().forEach((k, v) -> {
                            log.info("[OUTBOUND REQUEST] queryParams: [{}] = [{}]", k, v);
                            builder.queryParam(k, v);
                        });
                    }
                    return builder.build().toUri();
                });

        if (ctx.getHeaders() != null && !ctx.getHeaders().isEmpty()) {
            ctx.getHeaders().forEach((k, v) -> {
                requestSpec.header(k, v);
                log.info("[OUTBOUND REQUEST] Header: [{}] = [{}]", k, v);
            });
        }

        if (ctx.getBody() != null) {
            log.info("[OUTBOUND REQUEST] Body={}", ctx.getBody());
            requestSpec.bodyValue(ctx.getBody());
        }

        return requestSpec.exchangeToMono(response -> {
                    log.info("[OUTBOUND RESPONSE] Url: [{}], Status Code: [{}]", ctx.getUrl(), response.statusCode());
                    if (response.statusCode().isError()) {
                        return response.createException().flatMap(Mono::error);
                    }
                    return response.bodyToMono(responseType);
                })
                .doOnNext(responseBody -> log.info("[OUTBOUND RESPONSE] responseBody: [{}]", responseBody.toString()))
                .doOnError(error -> log.error("[OUTBOUND ERROR] Url: [{}], Error: [{}]", ctx.getUrl(), error.getMessage()));
    }
}
