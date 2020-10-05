package com.lorthos.jserv.tcp;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.OptionalLong;

/**
 * stores the connections for future reference
 * such as logging or keep-alive
 */
public interface ConnectionRegistry {

    /**
     * add a new SocketChannel
     * or add an existing connection with updated timestamp
     *
     * @param channel
     * @throws IOException
     */
    void add(SocketChannel channel);

    /**
     * remove a closed connection
     *
     * @param channel
     * @throws IOException
     */
    void remove(SocketChannel channel);

    Integer getOpenConnectionCount();

    /**
     * returns the timestamp for when
     * socket had a last know activity
     * @param channel
     * @return
     */
    OptionalLong getLastActivityTime(SocketChannel channel);

}
