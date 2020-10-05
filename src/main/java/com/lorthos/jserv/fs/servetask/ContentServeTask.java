package com.lorthos.jserv.fs.servetask;

import com.lorthos.jserv.fs.FileSystemManager;
import com.lorthos.jserv.fs.PathElement;
import com.lorthos.jserv.fs.html.FileSystemViewTemplate;
import com.lorthos.jserv.http.Request;
import com.lorthos.jserv.http.Response;
import com.lorthos.jserv.http.ResponseFactory;
import com.lorthos.jserv.http.servetask.ServeTask;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;

/**
 * Converts FS to HTML representation
 */
public class ContentServeTask extends ServeTask {

    private final FileSystemManager fileSystemManager;
    private final FileSystemViewTemplate fileSystemViewTemplate;

    public ContentServeTask(FileSystemManager fileSystemManager, FileSystemViewTemplate fileSystemViewTemplate) {
        this.fileSystemManager = fileSystemManager;
        this.fileSystemViewTemplate = fileSystemViewTemplate;
    }

    @Override
    public Response processGET(Request request) throws IOException {
        String resourceURI = request.getParsedRequest().get().getUri();
        PathElement pathElement = PathElement.build(fileSystemManager.getServerRoot(), resourceURI);
        // content can be dynamic html to render or a concrete file from filesystem
        Optional<ByteBuffer> content;
        Optional<FileChannel> fileChannel;
        if (pathElement.getType() == PathElement.Type.FILE) {
            //slurp the content
            content = Optional.empty();
            fileChannel = fileSystemManager.getFileChannel(pathElement);
        } else {
            //TODO could be cached
            List<PathElement> list = fileSystemManager.list(pathElement);
            content = Optional.of(ByteBuffer.wrap(fileSystemViewTemplate.render(resourceURI, list)));
            fileChannel = Optional.empty();
        }
        return ResponseFactory.getInstance().build(request, pathElement, content, fileChannel);
    }

    @Override
    public Response processHEAD(Request request) throws IOException {
        return processGET(request);
    }


}