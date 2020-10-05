package com.lorthos.jserv;

import com.lorthos.jserv.fs.FileSystemPackageFactory;
import com.lorthos.jserv.http.ResponseFactory;
import com.lorthos.jserv.http.servetask.*;
import com.lorthos.jserv.tcp.*;
import com.lorthos.jserv.tcp.ConnectionRegistry;
import com.lorthos.jserv.tcp.ConnectionRegistryImpl;
import com.lorthos.jserv.util.Config;
import com.lorthos.jserv.util.Log;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {

    private final Config config;

    /**
     * runs the socket loop, 1 thread
     */
    private ExecutorService socketService;
    private SocketServer socketServer;

    /**
     * runs the processing service, configurable number of threads
     */
    private ProcessingQueue processingQueue;

    public App(Config config) {
        this.config = config;
    }

    public void start() throws IOException {
        prepareDependencies();
        socketService.execute(socketServer);
    }

    public void stop() {
        processingQueue.shutdown();
        socketService.shutdown();
    }

    private void prepareDependencies() throws IOException {
        //fs package
        FileSystemPackageFactory.setInstance(
                new FileSystemPackageFactory(config));

        //http package
        ResponseFactory.setInstance(new ResponseFactory(config));

        //tcp package
        Selector selector = Selector.open();
        ConnectionRegistry connectionRegistry = new ConnectionRegistryImpl();
        ConnectionManagerImpl connectionManager = new ConnectionManagerImpl(
                config,
                selector,
                connectionRegistry);

        //processors
        ProcessorFactory.setInstance(new ProcessorFactory(connectionManager));
        processingQueue = new ProcessingQueueImpl(config);

        //server
        socketServer = new SocketServer(
                config,
                selector,
                connectionRegistry,
                connectionManager,
                processingQueue);
        socketService = Executors.newSingleThreadExecutor();
    }

    public static void main(String[] args) throws IOException {
        App app = App.build(args);
        app.start();
    }

    public static App build(String[] args) {
        if (args.length < 1) {
            Log.WARN("path to configuration file is required...");
            throw new RuntimeException("path to configuration file is required...");
        }
        String configPath = args[0];
        Config config = new Config(configPath);
        return new App(config);
    }
}
