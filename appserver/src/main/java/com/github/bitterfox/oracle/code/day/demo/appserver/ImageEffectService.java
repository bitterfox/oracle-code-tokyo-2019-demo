package com.github.bitterfox.oracle.code.day.demo.appserver;

import static com.github.bitterfox.oracle.code.day.demo.appserver.Util.CONTENT_TYPE_HEADER;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodySubscribers;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.prometheus.client.Summary.Timer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageEffectService {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
                                                            .executor(Runnable::run)
                                                            .build();
    private static final List<URI> EFFECT_SERVERS;

    static {
        try (Stream<String> lines = new BufferedReader(
                new InputStreamReader(
                        ImageHandler.class.getResourceAsStream("/effect-servers." + System.getProperty("appserver.profile", "local")))).lines()) {
            EFFECT_SERVERS = lines
                    .filter(l -> !l.isEmpty())
                    .filter(l -> !l.startsWith("#"))
                    .map(l -> {
                        try {
                            return new URI(l);
                        } catch (URISyntaxException e) {
                            log.warn("Ignore {}", l, e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            System.out.println("Effect servers: " + EFFECT_SERVERS);
        }
    }

    public InputStream applyEffect(InputStream is) throws IOException, InterruptedException, URISyntaxException {
        try (Timer t = Metrics.APP_APPLY_EFFECT_DURATION.startTimer()) {
            URI server = chooseEffectServer();
            log.info("Calling: {}", server);
            HttpResponse<InputStream> response = HTTP_CLIENT.send(
                    HttpRequest.newBuilder()
                               .uri(server)
                               .header(CONTENT_TYPE_HEADER, "image/png")
                               .POST(BodyPublishers.ofInputStream(() -> is))
                               .build(),
                    responseInfo -> BodySubscribers.ofInputStream());
            System.out.println(response);
            return response.body();
        }
    }

    protected URI chooseEffectServer() throws URISyntaxException {
        int i = ThreadLocalRandom.current().nextInt(EFFECT_SERVERS.size());
        return EFFECT_SERVERS.get(i);
    }
}
