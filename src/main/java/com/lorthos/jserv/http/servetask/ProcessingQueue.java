package com.lorthos.jserv.http.servetask;

import com.lorthos.jserv.tcp.SocketAttachment;

public interface ProcessingQueue {

    void add(SocketAttachment attachment);

    void shutdown();

}
