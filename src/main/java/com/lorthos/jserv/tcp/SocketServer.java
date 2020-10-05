package com.lorthos.jserv.tcp;

import com.lorthos.jserv.http.servetask.ProcessingQueue;
import com.lorthos.jserv.util.Config;
import com.lorthos.jserv.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * Accepts new connections and creates sockets for
 * further communication
 */
public class SocketServer implements Runnable {

    private final Config config;
    private final Selector selector;
    private final ConnectionRegistry connectionRegistry;
    private final ConnectionManager connectionManager;
    private final ProcessingQueue processingQueue;


    public SocketServer(Config config,
                        Selector selector,
                        ConnectionRegistry connectionRegistry,
                        ConnectionManager connectionManager,
                        ProcessingQueue processingQueue) {
        this.config = config;
        this.selector = selector;
        this.connectionRegistry = connectionRegistry;
        this.connectionManager = connectionManager;
        this.processingQueue = processingQueue;
    }

    private void accept() throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        serverSocketChannel.socket().bind(getBindAddress());
        Log.INFO("Listening on %s:%s", config.getBindInterface(), config.getPort());

        while (!Thread.currentThread().isInterrupted()) {
            selector.select(config.getSelectTimeoutMs());

            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey k = keys.next();
                keys.remove();

                if (k.isValid()) {
                    if (k.isAcceptable()) {
                        ServerSocketChannel chan = (ServerSocketChannel) k.channel();
                        SocketChannel socketChannel = chan.accept();
                        socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);

                        connectionRegistry.add(socketChannel);
                    } else if (k.isReadable()) {
                        SocketChannel chan = (SocketChannel) k.channel();
                        connectionRegistry.add(chan);
                        //in case of re-use
                        SocketAttachment attachment = (SocketAttachment) k.attachment();
                        if (attachment == null) {
                            attachment = SocketAttachment.build(chan);
                            k.attach(attachment);
                        }
                        boolean readSuccess = attachment.read();

                        if (readSuccess) {
                            processingQueue.add(attachment);
                        } else {
                            Log.INFO("channel has reached end-of-stream, closing connection.");
                            connectionManager.closeChannel(chan);
                        }
                    } else if (k.isWritable()) {
                        //close or re=use
                        connectionManager.closeOrReuseConnection(k);
                    }
                }

            }

            Log.INFO("Open Connections: %d", connectionRegistry.getOpenConnectionCount());
        }
    }

    private InetSocketAddress getBindAddress() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(config.getBindInterface());
        return new InetSocketAddress(inetAddress, config.getPort());
    }

    @Override
    public void run() {
        try {
            accept();
        } catch (IOException e) {
            Log.INFO("Error in Server.run %s", e);
            e.printStackTrace();
        }
    }

}
