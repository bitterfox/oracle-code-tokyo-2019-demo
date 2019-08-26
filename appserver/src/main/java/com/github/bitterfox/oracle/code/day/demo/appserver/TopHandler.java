package com.github.bitterfox.oracle.code.day.demo.appserver;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class TopHandler implements HttpHandler {
    private static final String TOP_PAGE_PATH = "/top.html";

    private String top;

    public TopHandler(Map<String, String> template) throws IOException {
        try (Stream<String> lines = new BufferedReader(
                new InputStreamReader(
                        getClass().getResourceAsStream(TOP_PAGE_PATH))).lines()) {
            top = lines.collect(Collectors.joining(System.lineSeparator()));
            template.forEach((k, v) -> {
                top = top.replace("{{" + k + "}}", v);
            });
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, 0);
        try (Writer w = new OutputStreamWriter(new BufferedOutputStream(exchange.getResponseBody()))) {
            w.write(top);
        }
    }
}
