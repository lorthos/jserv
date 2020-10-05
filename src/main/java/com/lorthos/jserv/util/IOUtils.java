package com.lorthos.jserv.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class IOUtils {
    public static String streamToString(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream)).lines()
                .parallel().collect(Collectors.joining("\n"));
    }
}
