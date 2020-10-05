package com.lorthos.jserv.fs;

import java.net.URLConnection;
import java.util.HashMap;

/**
 * Simple implementation
 */
public class ContentTypeResolver {

    private static final HashMap<String, String> additionalTypes = new HashMap<>();

    static {
        additionalTypes.put("js", "text/javascript");
        additionalTypes.put("css", "text/css");
        additionalTypes.put("mp4", "video/mp4");
        additionalTypes.put("properties", "text/plain");
    }

    public static String resolve(String fileName) {
        return
                additionalTypes.keySet().stream()
                        .filter(fileName::endsWith).map(additionalTypes::get).findFirst()
                        .orElse(URLConnection.guessContentTypeFromName(fileName));

    }
}
