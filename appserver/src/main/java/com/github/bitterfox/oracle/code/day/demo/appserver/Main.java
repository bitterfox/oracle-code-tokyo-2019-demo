package com.github.bitterfox.oracle.code.day.demo.appserver;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.HTTPServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException {
        switch (args[0].toLowerCase()) {
            case "fiber":
                new AppServer().start(20080, new FiberExecutor());
                new HTTPServer(new InetSocketAddress(Metrics.PORT), CollectorRegistry.defaultRegistry, false);
                break;
            case "thread": // Sync for RPC
                new AppServer().start(20081, Executors.newFixedThreadPool(Integer.parseInt(args[1])));
                new HTTPServer(new InetSocketAddress(Metrics.PORT+1), CollectorRegistry.defaultRegistry, false);
                break;
            // TODO: Async for RPC
        }
    }
}
