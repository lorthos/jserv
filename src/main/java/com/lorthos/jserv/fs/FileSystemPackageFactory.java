package com.lorthos.jserv.fs;

import com.lorthos.jserv.fs.html.FileSystemViewTemplate;
import com.lorthos.jserv.fs.servetask.ContentServeTask;
import com.lorthos.jserv.util.Config;
import com.lorthos.jserv.util.Log;

public class FileSystemPackageFactory {

    private final FileSystemManager fileSystemManager;
    private final FileSystemViewTemplate fileSystemViewTemplate;

    public FileSystemPackageFactory(Config config) {
        fileSystemManager = new FileSystemManagerImpl(config.getServerRoot());
        fileSystemViewTemplate = new FileSystemViewTemplate(config.getHtmlTemplatePath());
    }

    public ContentServeTask buildContentServeTask() {
        return new ContentServeTask(fileSystemManager, fileSystemViewTemplate);
    }


    private static FileSystemPackageFactory instance;

    public static FileSystemPackageFactory getInstance() {
        if (instance == null) {
            Log.WARN("Initialize Factory properly");
            throw new RuntimeException("Initialize Factory properly");
        }
        return instance;
    }

    public static void setInstance(FileSystemPackageFactory instance) {
        FileSystemPackageFactory.instance = instance;
    }


}
