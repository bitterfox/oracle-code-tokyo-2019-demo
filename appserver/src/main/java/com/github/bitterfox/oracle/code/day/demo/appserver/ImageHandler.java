package com.github.bitterfox.oracle.code.day.demo.appserver;

import static com.github.bitterfox.oracle.code.day.demo.appserver.Util.CONTENT_TYPE_HEADER;
import static com.github.bitterfox.oracle.code.day.demo.appserver.Util.GET_METHOD;
import static com.github.bitterfox.oracle.code.day.demo.appserver.Util.POST_METHOD;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageHandler implements HttpHandler {
    private static final int BUFFER_SIZE = 512*1024;
    private final ImageEffectService imageEffectService = new ImageEffectService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        InputStream imageInputStream;
        switch (Util.getMethod(exchange)) {
            case GET_METHOD:
                Path fullpath = Paths.get(exchange.getRequestURI().getPath());
                if (fullpath.getNameCount() <= 1) {
                    Util.redirectTo(exchange, "/", ex -> {
                        try {
                            byte[] respBody = (fullpath + " is invalid")
                                    .getBytes(StandardCharsets.UTF_8);
                            exchange.getResponseBody().write(respBody);
                            exchange.getResponseBody().close();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
                    return;
                }
                Path path = fullpath.subpath(1, fullpath.getNameCount());
                log.info("Sample image {}", path);
                imageInputStream = new ByteArrayInputStream(Resources.getInstance().load("/" + path));
                if (imageInputStream == null) {
                    Util.redirectTo(exchange, "/", ex -> {
                        try {
                            byte[] respBody = (path + " is not found")
                                    .getBytes(StandardCharsets.UTF_8);
                            exchange.getResponseBody().write(respBody);
                            exchange.getResponseBody().close();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
                    return;
                }
                break;
            case POST_METHOD:
                try {
                    imageInputStream = loadFirstFile(exchange);
                } catch (FileUploadException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                Util.redirectTo(exchange, "/", ex -> {
                    try {
                        byte[] respBody = (exchange.getRequestMethod().toUpperCase() + " is not accepted")
                                .getBytes(StandardCharsets.UTF_8);
                        exchange.getResponseBody().write(respBody);
                        exchange.getResponseBody().close();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
                return;
        }
        try {
            exchange.getResponseHeaders().add(CONTENT_TYPE_HEADER, "image/jpeg"); // TODO
            exchange.sendResponseHeaders(200, 0);
            try (InputStream is = imageEffectService.applyEffect(imageInputStream);
                 OutputStream os = exchange.getResponseBody()) {
                IOUtils.copyLarge(is, os, new byte[BUFFER_SIZE]);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    InputStream loadFirstFile(HttpExchange exchange) throws FileUploadException, IOException {
        exchange.getRequestHeaders().forEach(
                (k, v) -> System.out.println(k + ": " + v));
        DiskFileItemFactory d = new DiskFileItemFactory();

        ServletFileUpload up = new ServletFileUpload(d);
        List<FileItem> result = up.parseRequest(new RequestContext() {
            @Override
            public String getCharacterEncoding() {
                return "UTF-8";
            }

            @Override
            @Deprecated
            public int getContentLength() {
                return 0;
            }

            @Override
            public String getContentType() {
                return exchange.getRequestHeaders().getFirst(CONTENT_TYPE_HEADER);
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return exchange.getRequestBody();
            }
        });
        return result.get(0).getInputStream();
    }
}
