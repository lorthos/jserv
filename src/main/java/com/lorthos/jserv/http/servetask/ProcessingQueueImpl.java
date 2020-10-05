package com.lorthos.jserv.http.servetask;

import com.lorthos.jserv.tcp.SocketAttachment;
import com.lorthos.jserv.util.Config;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ProcessingQueueImpl implements ProcessingQueue {

    private final ThreadPoolExecutor executorService;

    public ProcessingQueueImpl(Config config) {
        executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getServerTaskThreadCount());
    }

    @Override
    public void add(SocketAttachment attachment) {
        executorService.execute(
                ProcessorFactory.getInstance().newProcessor(attachment));

    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }

}
