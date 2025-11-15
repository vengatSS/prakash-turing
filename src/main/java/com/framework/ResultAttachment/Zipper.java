package com.framework.ResultAttachment;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.*;
import java.io.UncheckedIOException;
import com.framework.Logger.Log;
import org.apache.logging.log4j.Logger;

public class Zipper {
    private static final Logger logger = Log.getLogger(Zipper.class);

    public static void main(String[] args) throws Exception {
        Path folderToZip = Paths.get("test-output");

        String timestamp = getCurrentTimestamp();
        Path zipFile = Paths.get("test-reports-" + timestamp + ".zip");

        deletePreviousZipFiles();

        if (!Files.exists(folderToZip) || !Files.isDirectory(folderToZip)) {
            logger.info("Folder 'test-output' does not exist.");
            throw new IllegalArgumentException("Folder 'test-output' does not exist.");
        }
        logger.info("Zipping folder...");

        zipFolder(folderToZip, zipFile);

        if (!Files.exists(zipFile) || Files.size(zipFile) == 0) {
            logger.info("Zip file is missing or empty.");
            throw new IllegalStateException("Zip file is missing or empty.");
        }
        logger.info("Successfully zipped 'test-output' to '{}'",zipFile.getFileName());
    }

    public static void zipFolder(Path sourceDir, Path outputZip) throws IOException {
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(outputZip))) {
            Files.walk(sourceDir)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }

    private static String getCurrentTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return LocalDateTime.now().format(formatter);
    }

    private static void deletePreviousZipFiles() throws IOException {
        Path dir = Paths.get(".");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "test-reports*.zip")) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    logger.info("Deleting existing zip file: {}",entry.getFileName());
                    Files.delete(entry);
                }
            }
        } catch (IOException e) {
            logger.info("Error while trying to delete old zip files");
            throw new IOException("Error while trying to delete old zip files", e);
        }
    }
}
