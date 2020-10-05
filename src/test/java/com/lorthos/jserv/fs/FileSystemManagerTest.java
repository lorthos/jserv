package com.lorthos.jserv.fs;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileSystemManagerTest extends BaseFsTest {

    private final FileSystemManager fileSystemManager = new FileSystemManagerImpl(serverRoot);

    @Test
    public void basicFlow() throws IOException {

        List<String> expectedPaths = new ArrayList<>();
        expectedPaths.add("SERVER_ROOT.txt");
        expectedPaths.add("dir1");

        List<PathElement> received = fileSystemManager.list(PathElement.build(serverRoot, "/"));

        assertTrue("should contain all expected paths",
                received.stream().map(PathElement::getRelativePath).collect(Collectors.toList()).containsAll(expectedPaths));

    }

    @Test
    public void readContent() throws IOException {
        FileChannel fileChannel = fileSystemManager.getFileChannel(PathElement.build(serverRoot, "SERVER_ROOT.txt")).get();
        assertEquals("CONTENTS_OF_SERVER_ROOT.txt", fromFileChannel(fileChannel));


        fileChannel = fileSystemManager.getFileChannel(PathElement.build(serverRoot, "dir1/TEXT_FILE.txt")).get();
        assertEquals("CONTENTS_OF_TEXT_FILE.txt",
                fromFileChannel(fileChannel));
    }

    @Test
    public void readContentLength() throws IOException {
        assertEquals(27,
                fileSystemManager.getContentLength(PathElement.build(serverRoot, "SERVER_ROOT.txt")));

        assertEquals(25,
                fileSystemManager.getContentLength(PathElement.build(serverRoot, "dir1/TEXT_FILE.txt")));
    }

    private String fromFileChannel(FileChannel fileChannel) throws IOException {
        ByteBuffer buff = ByteBuffer.allocate(Math.toIntExact(fileChannel.size()));
        fileChannel.read(buff);
        fileChannel.close();
        return new String(buff.array(), StandardCharsets.UTF_8);
    }
}