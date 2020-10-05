package com.lorthos.jserv.tcp;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.OptionalLong;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionRegistryImpl implements ConnectionRegistry {

    private final Map<SocketChannel, Long> socketTime = new ConcurrentHashMap<>();

    @Override
    public void add(SocketChannel channel) {
        socketTime.put(channel, System.currentTimeMillis());
    }

    @Override
    public void remove(SocketChannel channel) {
        socketTime.remove(channel);
    }

    @Override
    public Integer getOpenConnectionCount() {
        return socketTime.keySet().size();
    }

    @Override
    public OptionalLong getLastActivityTime(SocketChannel channel) {
        return OptionalLong.of(socketTime.get(channel));
    }
}
