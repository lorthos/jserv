package com.lorthos.jserv.http.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

public class RequestParserTest {

    static final String GET_REQ1 = "GET / http/1.1\n" +
            "Host: localhost:8080\n" +
            "User-Agent: curl/7.64.1\n" +
            "Connection: Close\n" +
            "Accept: */*\n";

    static final String HEAD_REQ1 = "HEAD / HTTP/1.0\n" +
            "Accept: */*\n";

    static final String HEAD_REQ2 = "HEAD /path1?x=y&a=b HTTP/1.1\n" +
            "Accept: */*\n";

    RequestParser p = new RequestParserImpl();

    @Test
    public void basicGET() throws IOException {
        ParsedRequest request = p.parseRequest(ByteBuffer.wrap(GET_REQ1.getBytes(StandardCharsets.UTF_8))).get();
        assertEquals(Method.GET, request.getMethod());
        assertEquals("/", request.getUri());
        assertEquals(ClientVersion.HTTP_1_1, request.getClientVersion());

        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "*/*");
        headers.put("user-agent", "curl/7.64.1");
        headers.put("host", "localhost:8080");
        headers.put("connection", "close");

        assertEquals(headers, request.getHeaders());
    }

    @Test
    public void basicHEAD() throws IOException {
        ParsedRequest request = p.parseRequest(ByteBuffer.wrap(HEAD_REQ1.getBytes(StandardCharsets.UTF_8))).get();
        assertEquals(Method.HEAD, request.getMethod());
        assertEquals("/", request.getUri());
        assertEquals(ClientVersion.HTTP_1_0, request.getClientVersion());
    }

    @Test
    public void withQueryString() throws IOException {
        ParsedRequest request = p.parseRequest(ByteBuffer.wrap(HEAD_REQ2.getBytes(StandardCharsets.UTF_8))).get();
        assertEquals("/path1", request.getUri());
        assertEquals(ClientVersion.HTTP_1_1, request.getClientVersion());
    }

    @Test()
    public void parseInvalidLine() throws IOException {
        Optional<ParsedRequest> optional = p.parseRequest(ByteBuffer.wrap("this is not http".getBytes(StandardCharsets.UTF_8)));
        assertFalse(optional.isPresent());
    }
}