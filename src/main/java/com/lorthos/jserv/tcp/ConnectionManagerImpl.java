package com.lorthos.jserv.tcp;

import com.lorthos.jserv.http.client.ParsedRequest;
import com.lorthos.jserv.util.Config;
import com.lorthos.jserv.util.Log;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Optional;
import java.util.OptionalLong;

public class ConnectionManagerImpl implements ConnectionManager {
    private final Config config;
    private final Selector selector;
    private final ConnectionRegistry connectionRegistry;

    public ConnectionManagerImpl(Config config, Selector selector, ConnectionRegistry connectionRegistry) {
        this.config = config;
        this.selector = selector;
        this.connectionRegistry = connectionRegistry;
    }

    /**
     * possible cases to close the connection
     * - keepalive disabled by server
     * - keepalive disabled by client
     * - keepalive reuse count or timeout exceeded
     * - technical error when cannot read the key attachment
     *
     * @param k
     * @throws IOException
     */
    @Override
    public void closeOrReuseConnection(SelectionKey k) throws IOException {
        SocketAttachment attachment = (SocketAttachment) k.attachment();
        if (!config.keepAliveEnabled() ||
                attachmentNull(attachment) ||
                clientDisabledKeepAlive(attachment) ||
                connectionReuseCountExceeded(attachment) ||
                connectionInactive(k)
        ) {
            closeConnection(k);
        } else {
            reuseConnection(k);
        }
    }


    public void closeChannel(SocketChannel socketChannel) throws IOException {
        connectionRegistry.remove(socketChannel);
        socketChannel.socket().close();
    }

    public void reuseConnection(SelectionKey k) throws ClosedChannelException {
        markConnectionRead((SocketAttachment) k.attachment());
    }

    private void closeConnection(SelectionKey k) throws IOException {
        SocketChannel channel = (SocketChannel) k.channel();
        closeChannel(channel);
    }

    private Boolean attachmentNull(SocketAttachment attachment) {
        boolean attachmentIsNull = attachment == null;
        if (attachmentIsNull) {
            Log.DEBUG("Closing connection, SocketAttachment is null");
        }
        return attachmentIsNull;
    }

    private Boolean clientDisabledKeepAlive(SocketAttachment attachment) {
        Optional<ParsedRequest> parsedRequest = attachment.getResponse().getRequest().getParsedRequest();
        if(parsedRequest.isPresent()) {
            Boolean clientDisabledKeepAlive = parsedRequest.get().clientDisabledKeepAlive();
            if (clientDisabledKeepAlive) {
                Log.DEBUG("Closing connection, client disabled keepAlive");
            }
            return clientDisabledKeepAlive;
        }
        return false;
    }

    private Boolean connectionReuseCountExceeded(SocketAttachment attachment) {
        boolean countExceeded = attachment.getReuseCount() > config.getKeepAliveCount();
        if (countExceeded) {
            Log.DEBUG("Closing connection due to reuseCount, found: %d", attachment.getReuseCount());
        }
        return countExceeded;
    }

    private Boolean connectionInactive(SelectionKey k) {
        SocketChannel channel = (SocketChannel) k.channel();
        OptionalLong lastActivityTime = connectionRegistry.getLastActivityTime(channel);
        boolean inactive =
                lastActivityTime.isPresent() &&
                        lastActivityTime.getAsLong() + config.getKeepAliveTimeout() * 1000 < System.currentTimeMillis();
        if (inactive) {
            Log.DEBUG("Closing connection due to inactivity, last-active: %d",
                    lastActivityTime.getAsLong());
        }
        return inactive;
    }

    public void markConnectionRead(SocketAttachment attachment) throws ClosedChannelException {
        SocketChannel channel = attachment.getChannel();
        attachment.getReadBuffer().clear();
        channel.register(selector, SelectionKey.OP_READ, attachment);
    }

    public void markConnectionForWrite(SocketAttachment attachment) throws ClosedChannelException {
        SocketChannel channel = attachment.getChannel();
        channel.register(selector, SelectionKey.OP_WRITE, attachment);
    }



}
