package com.lorthos.jserv.fs;

import java.io.File;
import java.util.Objects;

public class BaseFsTest {
    protected String serverRoot = getServerRoot();

    private String getServerRoot() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("SERVER_ROOT.txt")).getFile());
        System.out.println(file.getAbsolutePath());
        return file.getAbsolutePath().replace("SERVER_ROOT.txt", "");
    }

}
