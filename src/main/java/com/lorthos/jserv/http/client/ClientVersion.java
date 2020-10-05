package com.lorthos.jserv.http.client;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ClientVersion {
    HTTP_1_0("http/1.0"),
    HTTP_1_1("http/1.1");

    private static final Map<String, ClientVersion> map;

    static {
        map = new HashMap<>();
        for (ClientVersion foo : EnumSet.allOf(ClientVersion.class)) {
            map.put(foo.specName, foo);
        }
    }

    private final String specName;

    ClientVersion(String specName) {
        this.specName = specName;
    }

    public static ClientVersion fromSpecName(String specName) {
        return map.get(specName.toLowerCase());
    }


}
