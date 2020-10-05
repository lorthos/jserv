package com.lorthos.jserv.fs;

import com.lorthos.jserv.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileSystemManagerImpl implements FileSystemManager {

    private final Path serverRoot;

    public FileSystemManagerImpl(String serverRoot) {
        this.serverRoot = Paths.get(serverRoot);
        Log.INFO("Serving root folder: %s", serverRoot);
    }

    @Override
    public List<PathElement> list(PathElement pathElement) throws IOException {
        List<Path> localPaths;
        try (Stream<Path> stream = Files.list(pathElement.getServerFSPath())) {
            localPaths = stream.collect(Collectors.toList());
        }
        List<String> contents = localPaths.stream().map(path -> pathElement.getServerFSPath().relativize(path).toString()).collect(Collectors.toList());
        return contents.stream().map(p -> PathElement.build(getServerRoot(), p)).collect(Collectors.toList());
    }

    @Override
    public long getContentLength(PathElement path) throws IOException {
        FileChannel fileChannel = FileChannel.open(path.getServerFSPath());
        long size = fileChannel.size();
        fileChannel.close();
        return size;
    }

    @Override
    public String getServerRoot() {
        return serverRoot.toString();
    }

    @Override
    public Optional<FileChannel> getFileChannel(PathElement pathElement) throws FileNotFoundException {
        return Optional.of(new FileInputStream(pathElement.getServerFSPath().toFile()).getChannel());
    }
}
