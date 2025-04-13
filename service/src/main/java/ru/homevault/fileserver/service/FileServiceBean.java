package ru.homevault.fileserver.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.homevault.fileserver.dto.DirectoryListing;
import ru.homevault.fileserver.dto.FileItem;
import ru.homevault.fileserver.dto.FileType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class FileServiceBean implements FileService {

    @Value("${app.fs.root-dir}")
    private String baseDir;

    @PostConstruct
    private void init() throws IOException {
        Path vaultPath = Paths.get(baseDir).normalize().toAbsolutePath();

        if (!Files.exists(vaultPath)) {
            Files.createDirectories(vaultPath);
            log.info("Created vault directory: {}", vaultPath);

        } else if (!Files.isDirectory(vaultPath)) {
            throw new IllegalStateException("Vault path exists but is not a directory: " + vaultPath);
        }
    }

    @Override
    public DirectoryListing getDirectoryListing(String path, int depth) {
        List<FileItem> items = getDirectoryContent(path);

        if (depth == 0) {
            return DirectoryListing.builder()
                    .path(path)
                    .items(items)
                    .subdirectories(List.of())
                    .build();
        }

        List<DirectoryListing> subdirectories = new ArrayList<>();
        for (FileItem item : items) {
            if (item.getType() == FileType.D) {
                String subPath = path.isEmpty()
                        ? item.getName()
                        : (path + "/" + item.getName()).replace("//", "/");

                DirectoryListing subdirectory = getDirectoryListing(subPath, depth - 1);
                subdirectories.add(subdirectory);
            }
        }

        return DirectoryListing.builder()
                .path(path)
                .items(items)
                .subdirectories(subdirectories)
                .build();
    }

    @Override
    public List<FileItem> getDirectoryContent(String path) {
        Path fullPath = Paths.get(baseDir, path).normalize().toAbsolutePath();

        File directory = fullPath.toFile();

        if (!directory.exists()) {
            throw new IllegalArgumentException("Path does not exist: " + path);
        }

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Path is not a directory: " + path);
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return List.of();
        }

        return Arrays.stream(files)
                .map(
                        file -> FileItem.builder()
                                .name(file.getName())
                                .type(file.isDirectory() ? FileType.D : FileType.F)
                                .size(file.isDirectory() ? null : file.length())
                                .lastModifiedAt(convertToLocalDateTime(file.lastModified()))
                                .build()
                )
                .toList();
    }

    private LocalDateTime convertToLocalDateTime(long millis) {
        return Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
