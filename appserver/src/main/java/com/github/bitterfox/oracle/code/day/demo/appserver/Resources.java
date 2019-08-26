package com.github.bitterfox.oracle.code.day.demo.appserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Resources {
    private static final Resources INSTANCE = new Resources();
    private static final int BUFFER_SIZE = 128*1024;

    private Map<String, byte[]> resources = new ConcurrentHashMap<>();

    private Resources() {

    }

    public static final Resources getInstance() {
        return INSTANCE;
    }

    public byte[] load(String path) {
        return resources.computeIfAbsent(path, p -> {
            log.info("Resource requested {}", path);
            try (InputStream is = getClass().getResourceAsStream(path)) {
                if (is == null) {
                    log.warn("Resource not found: {}", path);
                    return null;
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOUtils.copyLarge(is, baos, new byte[BUFFER_SIZE]);
                return baos.toByteArray();
            } catch (IOException e) {
                log.error("Resource error: {}", path, e);
                return null;
            }
        });
    }
}
