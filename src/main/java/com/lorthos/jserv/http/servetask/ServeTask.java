package com.lorthos.jserv.http.servetask;

import com.lorthos.jserv.http.Request;
import com.lorthos.jserv.http.Response;
import com.lorthos.jserv.http.ResponseFactory;


import java.io.IOException;
import java.nio.file.NoSuchFileException;

/**
 * ServeTask is like a Servlet
 * Request -> Response
 */
public abstract class ServeTask {

    protected Response process(Request request) {
        ResponseFactory response = ResponseFactory.getInstance();

        if (!request.getParsedRequest().isPresent()) {
            return response.badRequest(request);
        }
        try {
            switch (request.getParsedRequest().get().getMethod()) {
                case GET:
                    return processGET(request);
                case HEAD:
                    return processHEAD(request);
                default:
                    return response.notAllowed(request);
            }
        } catch (NoSuchFileException e) {
            return response.notFound(request);
        } catch (Exception e) {
            e.printStackTrace();
            return response.internalError(request);
        }
    }

    protected abstract Response processGET(Request request) throws IOException;

    protected abstract Response processHEAD(Request request) throws IOException;
}
