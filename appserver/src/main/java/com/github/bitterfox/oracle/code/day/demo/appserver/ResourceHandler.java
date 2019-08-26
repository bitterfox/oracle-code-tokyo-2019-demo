package com.github.bitterfox.oracle.code.day.demo.appserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResourceHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Path fullpath = Paths.get(exchange.getRequestURI().getPath());
        Path path = fullpath.subpath(1, fullpath.getNameCount());
        log.info("Resource requested {}", path);
        try (OutputStream os = exchange.getResponseBody()) {
            byte[] bytes = Resources.getInstance().load("/" + path);
            if (bytes == null) {
                exchange.sendResponseHeaders(404, 0);
                return;
            }
            exchange.sendResponseHeaders(200, 0);
            os.write(bytes);
        }
    }
}
