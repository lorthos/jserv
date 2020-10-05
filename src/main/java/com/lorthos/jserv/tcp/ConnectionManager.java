package com.lorthos.jserv.tcp;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * deals with closing or re-using connections
 */
public interface ConnectionManager {

    void closeOrReuseConnection(SelectionKey k) throws IOException;
    void closeChannel(SocketChannel socketChannel) throws IOException;

    void markConnectionRead(SocketAttachment attachment) throws ClosedChannelException;
    void markConnectionForWrite(SocketAttachment attachment) throws ClosedChannelException;

}
