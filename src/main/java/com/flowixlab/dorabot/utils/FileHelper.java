package com.flowixlab.dorabot.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
public class FileHelper {

    public static void copyDirectory(Path sourceDirectoryLocation, String destinationDirectoryLocation) throws IOException {
        try (var walk = Files.walk(sourceDirectoryLocation)) {
            walk.forEach(source -> {
                Path destination = Paths.get(destinationDirectoryLocation, source.toString()
                        .substring(sourceDirectoryLocation.toString().length()));
                boolean exists = Files.exists(destination);
                log.info("file: " + destination.getFileName() + "; exists: " + exists);
                if (!exists && !Files.isDirectory(destination)) {
                    try {
                        log.info("copy: " + destination.getFileName());
                        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }
}
