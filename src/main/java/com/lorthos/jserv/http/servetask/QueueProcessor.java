package com.lorthos.jserv.http.servetask;

import com.lorthos.jserv.http.Request;
import com.lorthos.jserv.http.Response;
import com.lorthos.jserv.http.ResponseFactory;
import com.lorthos.jserv.tcp.ConnectionManager;
import com.lorthos.jserv.tcp.SocketAttachment;
import com.lorthos.jserv.util.Log;

import java.io.IOException;

class QueueProcessor implements Runnable {

    private final ConnectionManager connectionManager;
    private final ServeTask serveTask;

    private final SocketAttachment attachment;

    public QueueProcessor(ConnectionManager connectionManager,
                          ServeTask serveTask,
                          SocketAttachment attachment) {
        this.connectionManager = connectionManager;
        this.serveTask = serveTask;
        this.attachment = attachment;
    }

    @Override
    public void run() {
        try {
            process(attachment, serveTask);
            connectionManager.markConnectionForWrite(attachment);
        } catch (IOException e) {
            e.printStackTrace();
            Log.WARN("QueueProcessor:IOException %s", e.getMessage());
            throw new RuntimeException("QueueProcessor:IOException", e);
        }
    }

    private void process(SocketAttachment attachment, ServeTask serveTask) throws IOException {
        Request request = Request.build(attachment);
        Response response;
        if (!request.getParsedRequest().isPresent()) {
            response = ResponseFactory.getInstance().badRequest(request);
        } else {
            response = serveTask.process(request);
            if (response == null) {
                response = ResponseFactory.getInstance().internalError(request);
            }
        }
        attachment.setResponse(response);
        attachment.write();
    }
}

