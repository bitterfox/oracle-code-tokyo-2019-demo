package com.github.bitterfox.oracle.code.day.demo.appserver;

import io.prometheus.client.Gauge;
import io.prometheus.client.Summary;
import io.prometheus.client.hotspot.DefaultExports;

public class Metrics {
    static {
        DefaultExports.initialize();
    }

    public static final Gauge APP_REQUEST_COUNT =
            Gauge.build()
                 .name("app_request_count")
                 .help("app_request_count")
                 .labelNames("uri", "port")
                 .register();
    public static final Gauge APP_RESPONSE_COUNT =
            Gauge.build().name("app_response_count")
                 .labelNames("uri", "port", "code")
                 .help("app_response_count")
                 .register();
    public static final Gauge APP_HANDLING_HANDLER_COUNT =
            Gauge.build().name("app_handling_handler_count")
                 .help("app_handling_handler_count")
                 .labelNames("uri", "port")
                 .register();

    public static final Gauge APP_FIBER_EXECUTOR_TASK_COUNT =
            Gauge.build().name("APP_FIBER_EXECUTOR_TASK_COUNT")
                 .help("app_handling_handler_count")
                 .register();

    public static final Summary APP_HANDLE_DURATION =
            Summary.build()
                   .name("app_handle_duration")
                   .help("app_handle_duration")
                   .quantile(0.5, 0.05)
                   .quantile(0.9, 0.01)
                   .quantile(0.99, 0.001)
                   .quantile(0.999, 0.001)
                   .labelNames("uri", "port")
                   .register();

    public static final Summary APP_THREAD_WAIT_DURATION =
            Summary.build()
                   .name("app_thread_wait_duration")
                   .help("app_thread_wait_duration")
                   .quantile(0.5, 0.05)
                   .quantile(0.9, 0.01)
                   .quantile(0.99, 0.001)
                   .quantile(0.999, 0.001)
                   .labelNames("uri", "port")
                   .register();

    public static final Summary APP_RESPONSE_DURATION =
            Summary.build()
                   .name("app_response_duration")
                   .help("app_response_duration")
                   .quantile(0.5, 0.05)
                   .quantile(0.9, 0.01)
                   .quantile(0.99, 0.001)
                   .quantile(0.999, 0.001)
                   .labelNames("uri", "port")
                   .register();

    public static final Summary APP_APPLY_EFFECT_DURATION =
            Summary.build()
                   .name("app_apply_effect_duration")
                   .help("app_apply_effect_duration")
                   .quantile(0.5, 0.05)
                   .quantile(0.9, 0.01)
                   .quantile(0.99, 0.001)
                   .quantile(0.999, 0.001)
                   .register();

    public static final int PORT = 60080;
}
