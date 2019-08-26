package com.github.bitterfox.oracle.code.day.demo.appserver;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SleepHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            log.warn("Interrupted while sleeping", e);
            Thread.currentThread().interrupt();
        } finally {
            Util.redirectTo(exchange, "/", ex -> {});
        }
    }
}
