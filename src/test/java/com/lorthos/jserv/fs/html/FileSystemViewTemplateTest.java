package com.lorthos.jserv.fs.html;

import com.lorthos.jserv.fs.PathElement;
import com.lorthos.jserv.util.Log;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertTrue;

public class FileSystemViewTemplateTest {

    FileSystemViewTemplate template = new FileSystemViewTemplate("test.html");

    @Test
    public void renderSimpleHTML() {
        byte[] renderedTemplate = template.render("uri1",
                Collections.singletonList(PathElement.build("src/test/resources/dir1/", "image1.png")));
        String template = new String(renderedTemplate);
        Log.INFO(template);
        assertTrue(template.contains("uri1"));
        assertTrue(template.contains("<a href=\"image1.png\">"));
    }
}