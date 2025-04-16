package ru.homevault.fileserver.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.homevault.fileserver.dto.DirectoryListing;
import ru.homevault.fileserver.dto.FileItem;
import ru.homevault.fileserver.mapper.FileMapper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceBean implements FileService {

    @Value("${app.fs.root-dir}")
    private String baseDir;

    private final FileMapper fileMapper;

    @Override
    public DirectoryListing getDirectoryListing(String path, int depth) {
        String normalizedPath = normalizePath(path);

        List<FileItem> items = getDirectoryContent(path);

        List<DirectoryListing> subdirectories = new ArrayList<>();

        if (depth != 0) {
            items.stream()
                    .filter(FileItem::isDirectory)
                    .forEach(item -> {
                        String subPath = normalizePath(normalizedPath + "/" + item.getName());
                        subdirectories.add(getDirectoryListing(subPath, depth - 1));
                    });
        }

        return DirectoryListing.builder()
                .path(normalizedPath)
                .items(items)
                .subdirectories(subdirectories)
                .build();
    }

    @Override
    public String uploadFile(MultipartFile file, String path) {
        Path targetDir = Paths.get(baseDir, path).normalize().toAbsolutePath();

        try {
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            String filename = StringUtils.cleanPath(file.getOriginalFilename());

            Path targetPath = targetDir.resolve(filename);
            file.transferTo(targetPath);

            return normalizePath(path + "/" + filename);

        } catch (IOException e) {
            throw new RuntimeException("Can't upload file");
        }
    }

    @Override
    public Resource downloadFile(String filePath) {
        Path file = Paths.get(baseDir, filePath).normalize().toAbsolutePath();

        if (!Files.exists(file)) {
            throw new IllegalArgumentException("File not found: " + filePath);
        }

        if (!Files.isRegularFile(file)) {
            throw new IllegalArgumentException("Path is not a file: " + filePath);
        }

        try {
            return new UrlResource(file.toUri());

        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filePath, e);
        }
    }

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

    private String normalizePath(String path) {
        return (path.startsWith("/") ? path : "/" + path).replace("//", "/");
    }

    private List<FileItem> getDirectoryContent(String path) {
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

        return Arrays.stream(files).map(fileMapper::mapFileToFileItem).toList();
    }

}
