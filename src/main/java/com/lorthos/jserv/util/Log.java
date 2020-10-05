package com.lorthos.jserv.util;

import java.util.logging.Logger;

public class Log {
    private final static Logger logger = Logger.getLogger(Log.class.getName());

    public static void INFO(String message, Object... args) {
        String threadName = Thread.currentThread().getName();
        logger.info(String.format("[%s] %s", threadName, String.format(message, args)));
    }

    public static void DEBUG(String message, Object... args) {
        String threadName = Thread.currentThread().getName();
        logger.fine(String.format("[%s] %s", threadName, String.format(message, args)));
    }

    public static void WARN(String message, Object... args) {
        String threadName = Thread.currentThread().getName();
        logger.warning(String.format("[%s] %s", threadName, String.format(message, args)));
    }
}
