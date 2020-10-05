package com.lorthos.jserv.http.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

public interface RequestParser {

    Optional<ParsedRequest> parseRequest(ByteBuffer buffer) throws IOException;

}
