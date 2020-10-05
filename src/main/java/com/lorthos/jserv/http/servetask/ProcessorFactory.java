package com.lorthos.jserv.http.servetask;

import com.lorthos.jserv.fs.servetask.ContentServeTaskFactory;
import com.lorthos.jserv.tcp.ConnectionManager;
import com.lorthos.jserv.tcp.SocketAttachment;
import com.lorthos.jserv.util.Log;

public class ProcessorFactory {

    private final ConnectionManager connectionManager;

    public ProcessorFactory(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public Runnable newProcessor(SocketAttachment attachment) {
        return new QueueProcessor(
                connectionManager,
                ContentServeTaskFactory.getInstance().buildContentServeTask(),
                attachment
        );
    }


    private static ProcessorFactory instance;

    public static ProcessorFactory getInstance() {
        if (instance == null) {
            Log.WARN("Initialize Factory properly");
            throw new RuntimeException("Initialize Factory properly");
        }
        return instance;
    }

    public static void setInstance(ProcessorFactory instance) {
        ProcessorFactory.instance = instance;
    }

}
