package com.github.bitterfox.oracle.code.day.demo.appserver;

import java.io.IOException;
import java.util.function.Consumer;

import com.sun.net.httpserver.HttpExchange;

public class Util {
    public static final String CONTENT_TYPE_HEADER = "Content-type";
    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";

    public static void redirectTo(HttpExchange exchange, String to, Consumer<HttpExchange> delegate)
            throws IOException {
        exchange.getResponseHeaders().add("Location", to);
        exchange.sendResponseHeaders(302, 0);
        delegate.accept(exchange);
        exchange.close();
    }

    public static String getMethod(HttpExchange exchange) {
        return exchange.getRequestMethod().toUpperCase();
    }

    private Util() {
        throw new UnsupportedOperationException("No instance for you");
    }
}
