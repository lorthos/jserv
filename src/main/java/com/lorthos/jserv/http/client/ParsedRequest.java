package com.lorthos.jserv.http.client;

import java.util.Map;

public class ParsedRequest {
    private final Method method;
    private final String uri;
    private final Map<String, String> headers;
    private final ClientVersion clientVersion;

    public ParsedRequest(Method method, String uri, Map<String, String> headers, ClientVersion clientVersion) {
        this.method = method;
        this.uri = uri;
        this.headers = headers;
        this.clientVersion = clientVersion;
    }

    public Method getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public ClientVersion getClientVersion() {
        return clientVersion;
    }

    public Boolean clientDisabledKeepAlive() {
        String connectionHeader = headers.get("connection");
        return null != connectionHeader &&
                connectionHeader.equals("close");
    }

    @Override
    public String toString() {
        return "ParsedRequest{" +
                "method=" + method +
                ", uri='" + uri + '\'' +
                ", headers=" + headers +
                ", clientVersion=" + clientVersion +
                '}';
    }
}
