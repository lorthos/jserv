package com.lorthos.jserv.http;

import org.junit.Test;

import static org.junit.Assert.*;
import static com.lorthos.jserv.fs.ContentTypeResolver.*;

public class ContentTypeResolverTest {

    @Test
    public void basicFlow() {
        assertEquals("image/png", resolve("a.png"));
        assertEquals("application/zip", resolve("a.zip"));
        assertEquals("text/plain", resolve("a.properties"));
        assertNull(resolve("a.xxx"));
    }
}