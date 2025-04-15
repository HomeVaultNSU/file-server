package ru.homevault.fileserver.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.homevault.fileserver.dto.DirectoryListing;

public interface FileService {

    DirectoryListing getDirectoryListing(String path, int depth);

    String uploadFile(MultipartFile file, String path);

    Resource downloadFile(String filePath);
}
