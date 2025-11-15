package com.hcmut.voltrent.lib.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
@Slf4j
public class AsyncHttpClient {

    private final WebClient webClient;

    public AsyncHttpClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public <T> void executeAsync(
            HttpRequest ctx,
            Class<T> responseType,
            HttpCallbackHandler<T> callbackHandler) {
        log.info("[OUTBOUND REQUEST] Method={}", ctx.getHttpMethod());
        log.info("[OUTBOUND REQUEST] Endpoint={} ", ctx.getUrl());

        WebClient.RequestBodySpec requestBuilder = webClient.method(ctx.getHttpMethod()).uri(uriBuilder -> {
            uriBuilder.path(ctx.getUrl());
            if (ctx.getQueryParams() != null && !ctx.getQueryParams().isEmpty()) {
                ctx.getQueryParams().forEach((k, v) -> {
                    log.info("[OUTBOUND REQUEST] queryParams: [{}] = [{}] ", k, v);
                    uriBuilder.queryParam(k, v);
                });
            }
            return uriBuilder.build();
        });

        if (ctx.getHeaders() != null && !ctx.getHeaders().isEmpty()) {
            ctx.getHeaders().forEach((k, v) -> {
                requestBuilder.header(k, v);
                log.info("[OUTBOUND REQUEST] header: [{}] = [{}]", k, v);
            });
        }

        if (ctx.getBody() != null) {
            log.info("[OUTBOUND REQUEST] body={}", ctx.getBody());
            requestBuilder.bodyValue(ctx.getBody());
        }

        // Use exchange to get raw response without deserialization
        Mono<ClientResponse> responseMono =
                (ctx.getBody() != null)
                        ? requestBuilder.bodyValue(ctx.getBody()).exchange()
                        : requestBuilder.exchange();

        responseMono
                .doOnSuccess(response -> {
                    log.info("[OUTBOUND RESPONSE] url: [{}], status: [{}]", ctx.getUrl(), response.statusCode());
                    response.headers().asHttpHeaders().forEach((headerName, headerValues) -> {
                        headerValues.forEach(headerValue ->
                                log.info("[OUTBOUND RESPONSE] header: [{}] = [{}]", headerName, headerValue)
                        );
                    });
                })
                .flatMap(response -> {
                    return response.bodyToMono(responseType);
                })
                .doOnNext(response ->
                        log.info("[OUTBOUND RESPONSE] responseBody: [{}]", response.toString())
                )
                .doOnError(error ->
                        log.error("[OUTBOUND RESPONSE] url: [{}], error: [{}]", ctx.getUrl(), error.getMessage(), error))
                .subscribe(
                        callbackHandler.getOnSuccess(),
                        callbackHandler.getOnError()
                );

    }
}
