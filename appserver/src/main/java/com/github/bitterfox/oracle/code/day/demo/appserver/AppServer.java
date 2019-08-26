package com.github.bitterfox.oracle.code.day.demo.appserver;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodySubscribers;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

@Slf4j
public class AppServer {
    private int port;

    private static final HttpClient httpClient = HttpClient.newBuilder()
                                      .executor(Runnable::run)
                                      .build();

    private HttpHandler sleep = new MetricsTakingHandler(new SleepHandler());
    private HttpHandler remote = new MetricsTakingHandler(exchange-> {
        try {
            HttpResponse<byte[]> response = httpClient.send(HttpRequest.newBuilder()
                                                                       .uri(new URI("http://localhost:" + port + "/sleep"))
                                                                       .GET()
                                                                       .build(),
                                                            responseInfo -> BodySubscribers.ofByteArray());
            exchange.sendResponseHeaders(response.statusCode(), response.body().length);
            exchange.getResponseBody().write(response.body());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    });
    private HttpHandler image = new MetricsTakingHandler(new ImageHandler());
    private HttpHandler resource = new MetricsTakingHandler(new ResourceHandler());

    public void start(int port, Executor executor) throws IOException {
        this.port = port;
        HttpServer httpServer = HttpServer.create();

        Map<String, String> templates = new HashMap<>();
        templates.put("executorClass", executor.getClass().getSimpleName());

        httpServer.createContext("/", new MetricsTakingHandler(new TopHandler(templates)));
        httpServer.createContext("/sleep", sleep);
        httpServer.createContext("/remote/sleep", remote);
        httpServer.createContext("/image", image);
        httpServer.createContext("/resource", resource);

        httpServer.bind(new InetSocketAddress(port), 0);
        httpServer.setExecutor(command -> {
            double requestStart = System.currentTimeMillis();
            executor.execute(() -> {
                double handleStart = System.currentTimeMillis();
                try {
                    command.run();
                } finally {
                    double responseEnd = System.currentTimeMillis();
                    String uri = MetricsTakingHandler.LAST_URI.get();
                    String portString = String.valueOf(port);
                    Metrics.APP_RESPONSE_DURATION.labels(uri, portString)
                                                 .observe((responseEnd - requestStart) / 1000);
                    Metrics.APP_THREAD_WAIT_DURATION.labels(uri, portString)
                                                 .observe((handleStart - requestStart) / 1000);
                    Metrics.APP_HANDLE_DURATION.labels(uri, portString)
                                               .observe((responseEnd - handleStart) / 1000);
                }
            });
        });
        httpServer.start();
        System.out.println(httpServer.getAddress());
    }
}
