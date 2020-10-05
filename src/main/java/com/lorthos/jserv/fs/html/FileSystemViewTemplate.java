package com.lorthos.jserv.fs.html;

import com.lorthos.jserv.fs.PathElement;
import com.lorthos.jserv.util.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Basic HTML templating used for rendering Directory Views
 * Can be navigated with a browser
 */
public class FileSystemViewTemplate {
    private final String templateContents;

    public FileSystemViewTemplate(String templatePath) {
        ClassLoader classLoader = FileSystemViewTemplate.class.getClassLoader();
        templateContents = IOUtils.streamToString(classLoader.getResourceAsStream(templatePath));
    }

    public byte[] render(String resourceURI, List<PathElement> contents) {
        String renderedLinks = contents.stream()
                .map(this::pathElementToHtmlLink)
                .reduce("", String::concat);
        return templateContents
                .replace("__ROOT_PATH__", resourceURI)
                .replace("__LIST_ELEMENT__", renderedLinks)
                .getBytes(StandardCharsets.UTF_8);
    }

    private String pathElementToHtmlLink(PathElement pe) {
        String suffix = "";
        if (pe.getType() == PathElement.Type.DIRECTORY) {
            suffix = "/";
        }
        return "<li><a href=\"" + pe.getRelativePath() + suffix + "\">" + pe.getName() + suffix + "</a></li>\n";
    }

}
