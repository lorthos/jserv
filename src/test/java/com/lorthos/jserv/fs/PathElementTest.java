package com.lorthos.jserv.fs;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PathElementTest extends BaseFsTest {
    @Test
    public void withFile() {
        PathElement pathElement = PathElement.build(serverRoot, "SERVER_ROOT.txt");
        assertEquals(PathElement.Type.FILE, pathElement.getType());
        assertEquals("text/plain", pathElement.getContentType());
        assertEquals("SERVER_ROOT.txt", pathElement.getRelativePath());
    }

    @Test
    public void withDirectory() {
        PathElement pathElement = PathElement.build(serverRoot, "dir1");
        assertEquals(PathElement.Type.DIRECTORY, pathElement.getType());
        assertNull(pathElement.getContentType());
        assertEquals("dir1", pathElement.getRelativePath());
    }
}