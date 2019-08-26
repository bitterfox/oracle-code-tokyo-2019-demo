package com.github.bitterfox.oracle.code.day.demo.appserver;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetricsTakingHandler implements HttpHandler {
    private final HttpHandler delegate;
    static final ThreadLocal<String> LAST_URI = new ThreadLocal<>();

    public MetricsTakingHandler(HttpHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
//        log.info("handling");
        String uri = exchange.getRequestURI().toString();
        LAST_URI.set(uri);
        String port = String.valueOf(exchange.getLocalAddress().getPort());
        try {
            Metrics.APP_REQUEST_COUNT.labels(uri, port).inc();
            Metrics.APP_HANDLING_HANDLER_COUNT.labels(uri, port).inc();
            delegate.handle(exchange);
        } catch (Throwable e) {
            log.error("Exception while delegating to {}", delegate.getClass().getName(), e);
            throw e;
        } finally {
            Metrics.APP_RESPONSE_COUNT.labels(uri, port, String.valueOf(exchange.getResponseCode()))
                                      .inc();
            Metrics.APP_HANDLING_HANDLER_COUNT.labels(uri, port).dec();
            exchange.close();
        }
    }
}
