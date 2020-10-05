package com.lorthos.jserv.http.client;

import com.lorthos.jserv.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestParserImpl implements RequestParser {
    @Override
    public Optional<ParsedRequest> parseRequest(ByteBuffer buffer) throws IOException {
        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                new ByteArrayInputStream(buffer.array())));


        String methodLine = reader.readLine();
        if (isInvalidLine(methodLine)) {
            Log.WARN("Invalid methodLine: %s", methodLine);
            return Optional.empty();
        }

        String[] cmd = methodLine.split("\\s");
        if (cmd.length != 3) {
            Log.WARN("Bad methodLine: %s", methodLine);
            return Optional.empty();
        }
        final String method = cmd[0];
        int indexOf = cmd[1].indexOf('?');
        final String uri = indexOf > 0 ? cmd[1].substring(0, indexOf) : cmd[1];
        final String httpVersion = cmd[2];

        // parse headers
        boolean foundSplit = false;
        Map<String, String> parsedHeaders = new HashMap<>();
        while (!foundSplit) {
            String headerLine = reader.readLine();
            if (isInvalidLine(headerLine) || headerLine.equals("\n")) {
                foundSplit = true;
            } else {
                int firstIndex = headerLine.indexOf(':');
                if (firstIndex != -1) {
                    String key = headerLine.substring(0, firstIndex).trim().toLowerCase();
                    String value = headerLine.substring(firstIndex + 1).trim().toLowerCase();
                    parsedHeaders.put(key, value);
                }
            }
        }
        Method clientMethod;
        try {
            clientMethod = Method.valueOf(method);
        } catch (IllegalArgumentException e) {
            Log.WARN("Cannot parse HTTP method: %s", e.getMessage());
            return Optional.empty();
        }
        return Optional.of(
                new ParsedRequest(
                        clientMethod,
                        uri,
                        parsedHeaders,
                        ClientVersion.fromSpecName(httpVersion)));
    }

    private boolean isInvalidLine(String line) {
        return line == null ||
                line.length() == 0 ||
                Character.isWhitespace(line.charAt(0));
    }
}
