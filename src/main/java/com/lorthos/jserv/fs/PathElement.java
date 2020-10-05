package com.lorthos.jserv.fs;

import com.lorthos.jserv.util.Log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * PathElement represents a serviceable item in the Servers' root File tree
 */
public class PathElement {

    private final String relativePath;
    private final Type type;
    private final String contentType;
    private final Path serverFSPath;
    private final String name;

    public PathElement(String relativePath, Type type, String contentType, Path serverFSPath, String name) {
        this.relativePath = relativePath;
        this.type = type;
        this.contentType = contentType;
        this.serverFSPath = serverFSPath;
        this.name = name;
    }

    public static PathElement build(String serverRoot, String relativePath) {
        Path serverFSPath = Paths.get(serverRoot + File.separator + relativePath);
        File file = serverFSPath.toFile();
        Type type = file.isFile() ? Type.FILE : Type.DIRECTORY;
        String mimeType = ContentTypeResolver.resolve(file.getName());
        Log.DEBUG("resolved mimetype: %s for file %s", mimeType, file, file.getName());
        return new PathElement(relativePath, type, mimeType, serverFSPath, file.getName());
    }

    public enum Type {
        FILE,
        DIRECTORY
    }

    public String getContentType() {
        return contentType;
    }

    public Type getType() {
        return type;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public String getName() {
        return name;
    }

    public Path getServerFSPath() {
        return serverFSPath;
    }
}
