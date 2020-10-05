package com.lorthos.jserv.http;

import com.lorthos.jserv.fs.PathElement;
import com.lorthos.jserv.util.Config;
import com.lorthos.jserv.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Optional;

public class ResponseFactory {

    private final Config config;
    public ResponseFactory(Config config) {
        this.config = config;
    }

    public Response build(Request request,
                          PathElement pathElement,
                          Optional<ByteBuffer> dynamicBody,
                          Optional<FileChannel> fileChannel) throws IOException {
        Response response = new Response(request, 200, "OK", dynamicBody, fileChannel);
        String contentType = pathElement.getContentType();
        if (contentType == null) {
            contentType = "text/html";
        }
        if (contentType.contains("html") || contentType.contains("text")) {
            contentType += "; charset=utf-8";
        }
        response.getHeaders().put("Content-Type", contentType);
        if (fileChannel.isPresent()) {
            response.getHeaders().put("Content-Length", "" + fileChannel.get().size());
        } else if (dynamicBody.isPresent()) {
            response.getHeaders().put("Content-Length", "" + dynamicBody.get().remaining());
        } else {
            throw new RuntimeException("cannot determine content-length");
        }
        if (config.keepAliveEnabled()) {
            response.getHeaders().put("Connection", "Keep-Alive");
            response.getHeaders().put("Keep-Alive",
                    String.format("timeout=%d, max=%d", config.getKeepAliveTimeout(), config.getKeepAliveCount()));
        }

        return response;
    }

    public Response buildErrorResponse(Request request, int code, String message) {
        Log.WARN("[%d] : %s", code, request);
        Response response = new Response(request, code, message, Optional.empty(), Optional.empty());
        response.getHeaders().put("Content-Type", "text/plain");
        response.getHeaders().put("Content-Length", "0");
        if (config.keepAliveEnabled()) {
            response.getHeaders().put("Connection", "Keep-Alive");
            response.getHeaders().put("Keep-Alive",
                    String.format("timeout=%d, max=%d", config.getKeepAliveTimeout(), config.getKeepAliveCount()));
        } else {
            response.getHeaders().put("Connection", "Close");
        }
        return response;
    }

    public Response badRequest(Request request) {
        return ResponseFactory.getInstance().buildErrorResponse(request, 400, "Bad Request");
    }

    public Response notAllowed(Request request) {
        return ResponseFactory.getInstance().buildErrorResponse(request, 405, "Method Not Allowed");
    }

    public Response notFound(Request request) {
        return ResponseFactory.getInstance().buildErrorResponse(request, 404, "Not Found");
    }

    public Response internalError(Request request) {
        return ResponseFactory.getInstance().buildErrorResponse(request, 500, "Internal Server Error");
    }

    private static ResponseFactory instance;

    public static ResponseFactory getInstance() {
        if (instance == null) {
            Log.WARN("Initialize ResponseFactory properly");
            throw new RuntimeException("Initialize ResponseFactory properly");
        }
        return instance;
    }

    public static void setInstance(ResponseFactory instance) {
        ResponseFactory.instance = instance;
    }

}
