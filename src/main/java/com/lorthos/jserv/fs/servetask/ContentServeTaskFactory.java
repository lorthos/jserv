package com.lorthos.jserv.fs.servetask;

import com.lorthos.jserv.fs.FileSystemManager;
import com.lorthos.jserv.fs.FileSystemManagerImpl;
import com.lorthos.jserv.fs.html.FileSystemViewTemplate;
import com.lorthos.jserv.util.Config;
import com.lorthos.jserv.util.Log;

public class ContentServeTaskFactory {

    private final FileSystemManager fileSystemManager;
    private final FileSystemViewTemplate fileSystemViewTemplate;

    public ContentServeTaskFactory(Config config) {
        fileSystemManager = new FileSystemManagerImpl(config.getServerRoot());
        fileSystemViewTemplate = new FileSystemViewTemplate(config.getHtmlTemplatePath());
    }

    public ContentServeTask buildContentServeTask() {
        return new ContentServeTask(fileSystemManager, fileSystemViewTemplate);
    }


    private static ContentServeTaskFactory instance;

    public static ContentServeTaskFactory getInstance() {
        if (instance == null) {
            Log.WARN("Initialize Factory properly");
            throw new RuntimeException("Initialize Factory properly");
        }
        return instance;
    }

    public static void setInstance(ContentServeTaskFactory instance) {
        ContentServeTaskFactory.instance = instance;
    }


}
