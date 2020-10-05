package com.lorthos.jserv.http;

import com.lorthos.jserv.http.client.ParsedRequest;
import com.lorthos.jserv.http.client.RequestParser;
import com.lorthos.jserv.http.client.RequestParserImpl;
import com.lorthos.jserv.tcp.SocketAttachment;

import java.io.IOException;
import java.util.Optional;

public class Request {
    private static final RequestParser requestParser = new RequestParserImpl();

    private final Optional<ParsedRequest> parsedRequest;

    public Request(Optional<ParsedRequest> parsedRequest) {
        this.parsedRequest = parsedRequest;
    }

    public Optional<ParsedRequest> getParsedRequest() {
        return parsedRequest;
    }

    public static Request build(SocketAttachment attachment) throws IOException {
        return new Request(
                Request.requestParser.parseRequest(attachment.getReadBuffer()));
    }

    @Override
    public String toString() {
        String parsed = parsedRequest.isPresent() ? parsedRequest.get().toString() : "<missing>";
        return "Request{" +
                "parsedRequest=" + parsed +
                '}';
    }
}
