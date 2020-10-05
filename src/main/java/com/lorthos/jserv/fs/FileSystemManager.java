package com.lorthos.jserv.fs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;

/**
 * Local Filesystem operations from Server's perspective
 */
public interface FileSystemManager {

    Optional<FileChannel> getFileChannel(PathElement pathElement) throws FileNotFoundException;

    List<PathElement> list(PathElement listPath) throws IOException;

    long getContentLength(PathElement path) throws IOException;

    String getServerRoot();

}
