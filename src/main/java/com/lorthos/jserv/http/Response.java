package com.lorthos.jserv.http;

import com.lorthos.jserv.http.client.Method;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Response {
    private final Map<String, String> headers = new HashMap<>();
    private final Request request;
    private final int status;
    private final String statusMessage;
    private final Optional<ByteBuffer> contentBody;
    private final Optional<FileChannel> fileChannel;
    byte[] renderedHeaders;

    public Response(Request request,
                    int status,
                    String statusMessage,
                    Optional<ByteBuffer> contentBody,
                    Optional<FileChannel> fileChannel) {
        this.request = request;
        this.status = status;
        this.statusMessage = statusMessage;
        this.contentBody = contentBody;
        this.fileChannel = fileChannel;

        headers.put("Server", "JServ");
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] renderBody() throws IOException {
        String headerSection = prepareHeaders(status, statusMessage);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(headerSection.getBytes(StandardCharsets.UTF_8));

        //dont write body if its HEAD
        //TODO clean
        if (request.getParsedRequest().isPresent()
                && request.getParsedRequest().get().getMethod() == Method.GET) {
            if (status == 200 && contentBody.isPresent()) {
                outputStream.write((contentBody.get().array()));
            }
        }
        renderedHeaders = outputStream.toByteArray();
        return renderedHeaders;
    }

    public Request getRequest() {
        return request;
    }

    public Optional<FileChannel> getFileChannel() {
        return fileChannel;
    }

    private String prepareHeaders(int code, String message) {
        StringBuilder headerString = new StringBuilder("HTTP/1.1 " + code + " " + message + "\n");
        for (String key : headers.keySet()) {
            headerString.append(key).append(": ").append(headers.get(key)).append("\n");
        }
        headerString.append('\n');
        return headerString.toString();
    }

}
