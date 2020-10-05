package com.lorthos.jserv.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    protected final Properties prop = new Properties();

    public Config(String configPath) {
        try (InputStream inputStream = new FileInputStream(configPath)) {
            prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot initialize the application!:", e);
        }
    }

    public int getPort() {
        return Integer.parseInt(prop.getProperty("port"));
    }

    public String getBindInterface() {
        return prop.getProperty("bindInterface");
    }

    public String getServerRoot() {
        return prop.getProperty("serverRoot");
    }

    public Boolean keepAliveEnabled() {
        return Boolean.parseBoolean(prop.getProperty("keepAliveEnabled"));
    }

    public int getServerTaskThreadCount() {
        return Integer.parseInt(prop.getProperty("serverTaskThreadCount"));
    }

    public int getKeepAliveTimeout() {
        return Integer.parseInt(prop.getProperty("keepAliveTimeoutInSeconds"));
    }

    public int getKeepAliveCount() {
        return Integer.parseInt(prop.getProperty("keepAliveCount"));
    }

    public int getSelectTimeoutMs() {
        return Integer.parseInt(prop.getProperty("selectTimeoutMs"));
    }

    public String getHtmlTemplatePath() {
        return prop.getProperty("htmlTemplatePath");
    }

}
