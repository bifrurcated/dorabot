package com.flowixlab.dorabot.utils;

import com.flowixlab.dorabot.DoraBotApplication;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class ResourcesManager {
    private static URI jrtBaseURI;
    private static boolean IS_JRT;

    private static URI PATH_TO_LANG;
    private static URI PATH_TO_MENUS;
    private static URI PATH_TO_ROLES;

    public static final String LANG_DIR;
    public static final String MENUS_DIR;
    public static final String ROLES_DIR;

    static {
        String currentPath = new File("").getAbsolutePath();
        LANG_DIR = currentPath + File.separator + "lang";
        MENUS_DIR = currentPath + File.separator + "menus";
        ROLES_DIR = currentPath + File.separator + "roles";
    }

    public Path getFileSystemPath(URI uri, String ... more){
        Path uriPath = Path.of(uri);
        if (IS_JRT) {
            FileSystem fileSystem = FileSystems.getFileSystem(URI.create("jrt:/"));
            return fileSystem.getPath(uriPath.toString(), more);
        }
        return Path.of(uriPath.toString(), more);
    }

    public static void initialize() {
        if (checkJRT()) {
            PATH_TO_LANG = URI.create(jrtBaseURI + "/lang");
            PATH_TO_MENUS = URI.create(jrtBaseURI + "/menus");
            PATH_TO_ROLES = URI.create(jrtBaseURI + "/roles");
        } else {
            PATH_TO_LANG = URI.create(String.valueOf(DoraBotApplication.class.getResource("/lang")));
            PATH_TO_MENUS = URI.create(String.valueOf(DoraBotApplication.class.getResource("/menus")));
            PATH_TO_ROLES = URI.create(String.valueOf(DoraBotApplication.class.getResource("/roles")));
        }
        copyResourceData();
    }

    private static boolean checkJRT() {
        URL resource = DoraBotApplication.class.getResource("/");
        if(resource == null || resource.getProtocol().equals("jrt")) {
            jrtBaseURI = URI.create("jrt:/dorabot/");
            IS_JRT = true;
            return true;
        }
        return false;
    }

    private static void copyResourceData(){
        try {
            //FileHelper.copyDirectory(Path.of(PATH_TO_LANG), LANG_DIR);
            FileHelper.copyDirectory(Path.of(PATH_TO_MENUS), MENUS_DIR);
            FileHelper.copyDirectory(Path.of(PATH_TO_ROLES), ROLES_DIR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
